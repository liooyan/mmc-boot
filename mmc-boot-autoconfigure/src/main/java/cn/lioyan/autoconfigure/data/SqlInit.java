package cn.lioyan.autoconfigure.data;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

public class SqlInit {



    public static void determineDataSource( String dataSourceName, Environment environment, BeanDefinitionRegistry registry,String basePath) {
        SqlInitializationProperties sqlInitializationProperties = Binder.get(environment).bind(basePath + ".sql.init", Bindable.of(SqlInitializationProperties.class)).get();
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(SqlDataSourceScriptDatabaseInitializer.class)
                .setRole(BeanDefinition.ROLE_INFRASTRUCTURE)
                .addConstructorArgReference(dataSourceName)
                .addConstructorArgValue(sqlInitializationProperties)
                .getBeanDefinition();
        registry.registerBeanDefinition(basePath+".SqlDataSourceScriptDatabaseInitializer",beanDefinition);
    }


}
