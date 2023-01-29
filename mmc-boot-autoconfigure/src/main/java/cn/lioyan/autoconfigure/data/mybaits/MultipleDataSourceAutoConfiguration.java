package cn.lioyan.autoconfigure.data.mybaits;

import cn.lioyan.core.util.NullUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.SpringBootVFS;
import cn.lioyan.autoconfigure.config.ConfigProperties;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.web.context.support.StandardServletEnvironment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * MultipleDataSourceAutoConfiguration
 *
 * @author com.lioyan
 * @since 2022/1/2 22:21
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class, HikariDataSource.class})
@Import(MultipleDataSourceAutoConfiguration.MultipleDataSourceConfig.class)
public class MultipleDataSourceAutoConfiguration {

    protected static final String MULTIPLY_DATASOURCE_PREFIX = "multiply.datasource";

    protected static class MultipleDataSourceConfig implements ImportBeanDefinitionRegistrar, EnvironmentAware, ResourceLoaderAware {
        private ResourceLoader resourceLoader;
        private Environment environment;

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
           List<String> dbs = getDbs();
            String defBasePackage = environment.getProperty(ConfigProperties.APP_BASE_PACKAGE);
            ResourcePatternResolver pathResolver = new PathMatchingResourcePatternResolver();

            for (String dsName : dbs) {
                String scheme= environment.getProperty("spring.datasource." + dsName + ".jdbc_type");
                String property = environment.getProperty("spring.datasource." + dsName + ".basePackage");
                String daoProperty = environment.getProperty("spring.datasource." + dsName + ".daoPackage");
                String basePackage = defBasePackage;
                if (property != null) {
                    basePackage = property;
                }
                String sqlSessionFactoryName = dsName + "SqlSessionFactory";
                if(NullUtil.notNull(daoProperty)){
                    scanMapper(registry, sqlSessionFactoryName, daoProperty);
                }else {
                    scanMapper(registry, sqlSessionFactoryName, basePackage + ".dao." + dsName);
                }

                registry.registerBeanDefinition(dsName + "sqlSessionTemplate", BeanDefinitionBuilder.genericBeanDefinition(SqlSessionTemplate.class)
                        .setRole(BeanDefinition.ROLE_INFRASTRUCTURE).setSynthetic(true).addConstructorArgReference(sqlSessionFactoryName).getBeanDefinition());

                String dataSourceName = dsName + "DataSource";
                registry.registerBeanDefinition(dataSourceName, BeanDefinitionBuilder.genericBeanDefinition(HikariDataSource.class, () -> createDataSource(dsName))
                        .setRole(BeanDefinition.ROLE_INFRASTRUCTURE).setSynthetic(true).getBeanDefinition());
                SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
                factoryBean.setVfs(SpringBootVFS.class);
                MultimoduleMybatisConfiguration conf = new MultimoduleMybatisConfiguration();

                conf.setMapUnderscoreToCamelCase(true);
                conf.addInterceptor(mybatisPlusInterceptor(scheme));
                conf.setJdbcType(scheme);
                registry.registerBeanDefinition(sqlSessionFactoryName,
                        loadMapper(pathResolver, dsName,scheme)
                                .addPropertyValue("dataSource", new RuntimeBeanReference(dataSourceName))
                                .addPropertyValue("typeAliasesPackage", basePackage + ".bean" + dsName)
                                .addPropertyValue("typeHandlersPackage", basePackage + ".dao.handler" + dsName)
                                .addPropertyValue("configuration", conf).getBeanDefinition());

            }
        }

        public MybatisPlusInterceptor mybatisPlusInterceptor(  String scheme) {
            DbType dbType ;
           if(scheme == null){
               dbType = DbType.MYSQL;
           }else {
               dbType = DbType.getDbType(scheme);
           }

            MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
            interceptor.addInnerInterceptor(new PaginationInnerInterceptor(dbType));
            return interceptor;
        }


        private BeanDefinitionBuilder loadMapper(ResourcePatternResolver pathResolver, String dsName,String schema) {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(SqlSessionFactoryBean.class)
                    .setRole(BeanDefinition.ROLE_INFRASTRUCTURE).addPropertyValue("vfs", SpringBootVFS.class);
            Resource[] resources = null;
            ArrayList<Resource> paths = new ArrayList<>();
            try {
                resources = pathResolver.getResources("classpath:mapper/" + dsName + "/*.xml");
                for (Resource resource : resources)
                {
                    paths.add(resource);
                }
            } catch (IOException e) {
                //ignore not exist
            }

            try {
                if(NullUtil.notNull(schema)){
                    resources = pathResolver.getResources("classpath:mapper-"+schema+"/" + dsName + "/*.xml");
                    for (Resource resource : resources)
                    {
                        paths.add(resource);
                    }
                }
            } catch (IOException e) {
                //ignore not exist
            }

            resources = new Resource[paths.size()];
            paths.toArray(resources);
            if (resources != null) {
                beanDefinitionBuilder.addPropertyValue("mapperLocations", resources);
            }
            return beanDefinitionBuilder;
        }


        private List<String> getDbs() {
            Set<String> dbsSet = new HashSet<>();
            if (environment instanceof StandardServletEnvironment) {
                MutablePropertySources propertySources = ((StandardServletEnvironment) environment).getPropertySources();
                for (PropertySource<?> propertySource : propertySources) {
                    String[] dbs = environment.getProperty(MULTIPLY_DATASOURCE_PREFIX + ".name", String[].class, new String[0]);
                    if (dbs.length != 0) {
                        for (String db : dbs) {
                            dbsSet.add(db);
                        }
                    }
                }
            }
            return new ArrayList<>(dbsSet);
        }

        private void scanMapper(BeanDefinitionRegistry registry, String sqlSessionFactoryName, String pkg) {
            ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
            if (resourceLoader != null) {
                scanner.setResourceLoader(resourceLoader);
            }
            scanner.setBeanNameGenerator(new FullyQualifiedAnnotationBeanNameGenerator());
            scanner.setSqlSessionFactoryBeanName(sqlSessionFactoryName);
            scanner.registerFilters();
            scanner.doScan(pkg);
        }

        private HikariDataSource createDataSource(String dsName) {
            DataSourceProperties dataSourceProperties = Binder.get(environment).bind("spring.datasource." + dsName, Bindable.of(DataSourceProperties.class)).get();
            return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        }

        @Override
        public void setEnvironment(Environment environment) {
            this.environment = environment;
        }

        @Override
        public void setResourceLoader(ResourceLoader resourceLoader) {
            this.resourceLoader = resourceLoader;
        }

    }

}
