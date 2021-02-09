package ${package}.domain;

import io.elves.common.util.IpUtil;

/**
 * define domain constant variable
 *
 * @author lee
 */
public class Constant {
    public static final String systemIdentityId2 = IpUtil.getLocalIp();
    public static final Integer DEFAULT_LOCKED_TIME_WINDOW_SECOND = 60;
    public static final Integer DEFAULT_MAX_LOCK_TARGET_MAP_SIZE = 10000;
    public static final Integer PUBLISH_TO_REDIS_TIMEOUT_MILLS = 200;
    public static final String LOCK_TARGET_IP = "ip";
    public static final String ROOT_PATH = "*";
    public static final double DEFAULT_LIMIT_COUNT_PER_SECOND = 10;
    public static final int FLOW_BLOCK_CODE = 40001;
    public static final int BLACK_LOCK_CODE = 40000;
    public static final int WHITELIST_CODE = 40002;
    public static final int SYS_CODE = 40003;
    public static final int BLOCK_CODE = 40005;
    public static final String LOCK_SYS_TARGET = "sys*";
    //sentinel
    public static final String DEFAULT_RATELIMIT_DRIVER = "0";
    public static final int MAX_RESOURCE_SLOT = 30000;
    public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    public static String getSystemIdentityId2() {
        return systemIdentityId2;
    }
}

