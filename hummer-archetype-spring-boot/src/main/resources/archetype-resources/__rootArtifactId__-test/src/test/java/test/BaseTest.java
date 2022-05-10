package ${package}.test;

import com.hummer.core.ApplicationContextInitBean;
import com.hummer.core.init.HummerApplicationContextInit;
import com.hummer.dao.starter.ExportDaoInitBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(value = SpringRunner.class)
@Import(value = {ApplicationContextInitBean.class, ExportDaoInitBean.class})
@ContextConfiguration(initializers =
        {ConfigFileApplicationContextInitializer.class, HummerApplicationContextInit.class})
@ComponentScan(basePackages = "${package}")
//@HummerRestApiClientBootScan(scanBasePackages = "cn.com.microsoftstore.thrift.store.api.integration")
@WebAppConfiguration
public class BaseTest {
    @Test
    public void bootstrap() {

    }
}