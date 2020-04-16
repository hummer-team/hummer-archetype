package ${package}.api.main;


import com.hummer.core.starter.HummerApplicationStart;
import com.hummer.rest.webserver.UndertowServer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
* application enter
* @author liguo
**/
@SpringBootApplication(scanBasePackages = "${package}")
@EnableAspectJAutoProxy(proxyTargetClass = true)
@UndertowServer
public class ApplicationStart {

    public static void main(String[] args) {
        HummerApplicationStart.start(ApplicationStart.class,args);
    }

}
