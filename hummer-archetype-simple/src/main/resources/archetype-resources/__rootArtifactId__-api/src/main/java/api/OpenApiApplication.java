package ${package}.api;


import com.hummer.core.init.HummerApplicationStart;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
* application enter
* @author liguo
* @date 2019/7/11 14:55
* @since 1.0.0
* @param
* @return
**/
@SpringBootApplication(scanBasePackages = "${package}")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class OpenApiApplication {

    public static void main(String[] args) {
        HummerApplicationStart.start(OpenApiApplication.class,args);
    }

}
