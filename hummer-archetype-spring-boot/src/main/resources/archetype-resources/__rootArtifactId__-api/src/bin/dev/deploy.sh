#!/bin/bash

#
# MIT License
#
# Copyright (c) 2022 LiGuo <bingyang136@163.com>
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.
#
#

# 修改APP_NAME为云效上的应用名
APP_NAME=${rootArtifactId}

PROG_NAME=$0
ACTION=$1
APP_START_TIMEOUT=45    # 等待应用启动的时间
APP_PORT=20000          # 应用端口
HEALTH_CHECK_URL=http://127.0.0.1:${APP_PORT}/warmup  # 应用健康检查URL
#HEALTH_CHECK_FILE_DIR=/usr/app/${APP_NAME}/status   # 脚本会在这个目录下生成nginx-status文件
APP_HOME=/usr/app/${APP_NAME} # 从package.tgz中解压出来的jar包放到这个目录下
JAR_NAME=${APP_NAME}-api-1.0-SNAPSHOT.jar # jar包的名字
JAR_PATH=${APP_HOME}/target/${JAR_NAME}
APP_LOG_DIR=/usr/app/logs/${APP_NAME}
JAVA_OUT=${APP_LOG_DIR}/start-${APP_NAME}.log #应用的启动日志
RESOURCES_PATH=${APP_HOME}/target/resources/
#创建出相关目录

if [ ! -d "${APP_HOME}" ]; then
  mkdir -p ${APP_HOME}
fi

if [ ! -d "${APP_LOG_DIR}" ]; then
  mkdir -p ${APP_LOG_DIR}
fi

#
JAVA_OPT="${JAVA_OPT} -server -Xms512m -Xmx512m -Xmn200m"
JAVA_OPT="${JAVA_OPT} -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${APP_LOG_DIR}/java_heapdump.hprof"
JAVA_OPT="${JAVA_OPT} -Dnacos.standalone=true"
JAVA_OPT="${JAVA_OPT} -Xlog:gc*:file=${APP_LOG_DIR}/gc.log:time,tags:filecount=10,filesize=102400"
JAVA_OPT="${JAVA_OPT} -Dserver.max-http-header-size=524288"
#JAVA_OPT="${JAVA_OPT} -Dspring.config.additional-location=${RESOURCES_PATH}"
#
JAVA_AFTER_OPT=" --spring.config.location=${RESOURCES_PATH}"


JAVA=${JAVA_HOME}/bin/java
echo "JAVA_HOME is ${JAVA}"

usage() {
    echo "Usage: $PROG_NAME {start|stop|restart}"
    exit 2
}

health_check() {
    exptime=0
    echo "checking ${HEALTH_CHECK_URL}"
    while true
        do
            status_code=`/usr/bin/curl -L -o /dev/null --connect-timeout 5 -s -w %{http_code} ${HEALTH_CHECK_URL}`
            if [ "$?" != "0" ]; then
               echo -n -e "\rapplication not started"
            else
                echo "code is $status_code"
                if [ $status_code = 200 ]; then
                    break
                fi
            fi
            sleep 1
            exptime=$(( $exptime+1 ))

            echo -e "application starting cost time: $exptime..."

            if [ $exptime -gt ${APP_START_TIMEOUT} ]; then
                echo "app start failed cost $exptime s"
                exit 2
            fi
        done
    echo "check ${HEALTH_CHECK_URL} success, application start ok cost `expr $exptime` s"
}
start_application() {
    echo "starting java process:"
    echo "${JAVA} $JAVA_OPT -jar ${JAR_PATH} ${JAVA_AFTER_OPT} > ${JAVA_OUT} 2>&1 &"
    nohup ${JAVA} $JAVA_OPT -jar ${JAR_PATH} ${JAVA_AFTER_OPT} > ${JAVA_OUT} 2>&1 &
    echo "started java process"
}

stop_application() {
   appjavapid=`ps -ef | grep java | grep ${JAR_NAME} | grep -v grep |grep -v 'deploy.sh'| awk '{print$2}'`

   if [ ! $appjavapid ]; then
      echo -e "\rno java process"
      return
   fi

   echo "stop the application ${JAR_NAME} pid is $appjavapid"

   times=60
   for e in $(seq 60)
   do
        COSTTIME=$(($times - $e ))
        appjavapid=`ps -ef | grep java | grep ${JAR_NAME} | grep -v grep |grep -v 'deploy.sh'| awk '{print$2}'`
        if [ -n "$appjavapid" ]; then
            kill $appjavapid
            echo -e  "\r        -- stopping java lasts `expr $COSTTIME` seconds."
        else
            echo -e "\rjava process has exited"
            break;
        fi
        sleep 1
   done
   if [ -n "$appjavapid" ]; then
      echo -e "java process ${JAR_NAME} timeout, now force kill."
      kill -9 $appjavapid
   fi
   if [ -n "$appjavapid" ]; then
      echo -e "\r${JAR_NAME} stop fail"
      exit 2
   fi
   echo -e "\r${JAR_NAME} stop success"
}
start() {
    JAVA_OPT="${JAVA_OPT} -Dspring.profiles.active=${1}"
    JAVA_OPT="${JAVA_OPT} -Dlog4j.configurationFile=file:${RESOURCES_PATH}log4j2-${1}.xml"
    start_application
    health_check
}
stop() {
    stop_application
}
case "$1" in
    start)
        start $2
    ;;
    stop)
        stop
    ;;
    restart)
        stop
        start $2
    ;;
    check)
        health_check
    ;;
    *)
        echo "start dev|uat|prd or stop or restart dev|uat|prd"
    ;;
esac
