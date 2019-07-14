package ${package}.api;


import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.hujiang.basic.framework.rest.config.service.HJundertow;
import com.hujiang.basic.framework.rest.main.HJApplication;

@SpringBootApplication(scanBasePackages="${package}")
@HJundertow
public class Application{

    public static void main(String[] args) {
        String env = System.getProperty("spring.profiles.active");
        if(env == null || env.startsWith("dev")){
            System.setProperty("log4j.configurationFile", "classpath:log4j2-local.xml");
        } else {
            System.setProperty("log4j.configurationFile", "classpath:log4j2.xml");
        }
    	HJApplication.start(Application.class, args);
    }

}
