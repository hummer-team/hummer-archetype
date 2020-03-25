package ${package}.test;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT,classes = ApplicationTest.class)
public abstract class BaseTest {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    public BaseTest(){

    }
}
