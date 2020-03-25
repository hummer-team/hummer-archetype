package ${package}.dao.initialize;


import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

/**
 * open openapi data source mysql
 * Created by liguo on 2017/8/11.
 * @author liguo
 */
@Configuration
@MapperScan(value = {"com.jiappo.open.api.dao.mapper.openapi"}
        , sqlSessionFactoryRef = "openApiSessionFactory")
@EnableTransactionManagement
public class OpenApiDataSourceConfig {
    @Bean(name = "openApiSessionFactory")
    public SqlSessionFactory userSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:mapper/openapi/*.xml");
        sqlSessionFactoryBean.setMapperLocations(resources);

        return sqlSessionFactoryBean.getObject();
    }

    @Primary
    @Bean(name = "openApiDataSource", initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties(prefix = "jdbc.open-api")
    public DataSource dataSource() {
        DruidDataSource druidSource = new DruidDataSource();
        druidSource.setValidationQuery("select 1");
        druidSource.setTestWhileIdle(true);
        //connection pool connection timeout abandoned
        druidSource.setRemoveAbandoned(true);
        //connection pool timeout 180s
        druidSource.setRemoveAbandonedTimeout(180);
        //output error log
        druidSource.setLogAbandoned(true);

        return druidSource;
    }

    @Bean(name = "openApiJdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Qualifier("openApiDataSource") DataSource msDataSource) {
        return new JdbcTemplate(msDataSource);
    }

    @Bean(name = "openApiTransactionTemplate")
    public TransactionTemplate transactionTemplate() {
        TransactionTemplate transactionTemplate = new TransactionTemplate();
        transactionTemplate.setTransactionManager(transactionManager());
        transactionTemplate.setTimeout(2000);
        return transactionTemplate;
    }

    @Bean(name = "openApiTransactionManager")
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }
}
