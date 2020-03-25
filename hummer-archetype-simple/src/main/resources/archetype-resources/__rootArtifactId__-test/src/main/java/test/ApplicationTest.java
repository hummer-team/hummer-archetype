package ${package}.test;


import com.hummer.core.init.HummerApplicationStart;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by liguo on 2017/6/23.
 */
@SpringBootApplication(scanBasePackages="com.panli.service.delivery")
@PropertySource(value = {"classpath:application.properties","classpath:application-qa.properties"})
public class ApplicationTest {
    public static void main(String[] args) {
        HummerApplicationStart.start(ApplicationTest.class, args);
    }
}

