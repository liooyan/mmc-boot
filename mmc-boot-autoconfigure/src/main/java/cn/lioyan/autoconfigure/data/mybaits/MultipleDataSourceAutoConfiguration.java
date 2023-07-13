package cn.lioyan.autoconfigure.data.mybaits;

import cn.lioyan.autoconfigure.config.ConfigProperties;
import cn.lioyan.autoconfigure.config.scope.NamingScopeBeanRegistry;
import cn.lioyan.autoconfigure.config.scope.NamingScopeContextRefreshedListener;
import cn.lioyan.autoconfigure.data.SqlInit;
import cn.lioyan.autoconfigure.util.SpringPropertyUtil;
import cn.lioyan.core.util.NullUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.SpringBootVFS;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
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
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
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

    protected static final String MULTIPLY_DATASOURCE_PREFIX = "mybatis";

    protected static class MultipleDataSourceConfig implements ImportBeanDefinitionRegistrar, EnvironmentAware, ResourceLoaderAware, BeanFactoryAware {
        private ResourceLoader resourceLoader;
        private Environment environment;

        private BeanFactory beanFactory;


        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            NamingScopeBeanRegistry namingScopeBeanRegistry = beanFactory.getBean(NamingScopeBeanRegistry.class);

            List<String> dbs = getDbs();
            ResourcePatternResolver pathResolver = new PathMatchingResourcePatternResolver();
            for (String dsName : dbs) {
                initMybaits(dsName, pathResolver, registry, namingScopeBeanRegistry);
            }

            //默认连接
           initMybaits(null,pathResolver,registry,namingScopeBeanRegistry);


        }
        private Set<String> daoPackages = new HashSet<>();

        private void initMybaits(String dsName, ResourcePatternResolver pathResolver, BeanDefinitionRegistry registry, NamingScopeBeanRegistry namingScopeBeanRegistry) {

            String basePath = dsName == null ? MULTIPLY_DATASOURCE_PREFIX : MULTIPLY_DATASOURCE_PREFIX + "." + dsName;
            MultipleMybaitsProperties multipleMybaitsProperties = null;
            if (dsName == null) {
                if(!daoPackages.add(basePath)){
                    return;
                }
                dsName = "";
                try {
                    multipleMybaitsProperties = Binder.get(environment).bind(basePath, Bindable.of(MultipleMybaitsProperties.class)).get();
                } catch (Exception e) {
                    multipleMybaitsProperties = new MultipleMybaitsProperties();
                }
                //默认路径
                String defBasePackage = environment.getProperty(ConfigProperties.APP_BASE_PACKAGE);
                if (multipleMybaitsProperties.getBasePackage() == null) {
                    multipleMybaitsProperties.setBasePackage(defBasePackage);
                }
            } else {
                multipleMybaitsProperties = Binder.get(environment).bind(basePath, Bindable.of(MultipleMybaitsProperties.class)).get();
            }
            daoPackages.add(basePath);

            //创建配置类
            String sqlSessionFactoryName = dsName + "SqlSessionFactory";
            //添加扫描的dao层，并注册 SqlSessionFactory
            scanMapper(registry, sqlSessionFactoryName, multipleMybaitsProperties);
            //注册 sqlSessionTemplate
            registry.registerBeanDefinition(dsName + "sqlSessionTemplate", BeanDefinitionBuilder.genericBeanDefinition(SqlSessionTemplate.class)
                    .setRole(BeanDefinition.ROLE_INFRASTRUCTURE).setSynthetic(true).addConstructorArgReference(sqlSessionFactoryName).getBeanDefinition());

            //获取数据库连接
            String dataSourceName = multipleMybaitsProperties.getDataSourceName() == null ? dsName : multipleMybaitsProperties.getDataSourceName();
            DataSource dataSource = (DataSource) namingScopeBeanRegistry.getBean(DataSource.class, dataSourceName);
            if (dataSource == null) {
                throw new RuntimeException("数据源不存在,数据库连接名称:" + dataSourceName);
            }
            String schema = null;
            try {
                schema = getSchema(dataSource);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
            factoryBean.setVfs(SpringBootVFS.class);
            MultimoduleMybatisConfiguration conf = new MultimoduleMybatisConfiguration();

            conf.setMapUnderscoreToCamelCase(true);
            conf.addInterceptor(mybatisPlusInterceptor(schema));
            conf.setJdbcType(schema);
            registry.registerBeanDefinition(sqlSessionFactoryName,
                    loadMapper(pathResolver, multipleMybaitsProperties.getXmlBasePath(), dsName, schema)
                            .addPropertyValue("dataSource", dataSource)
                            .addPropertyValue("typeAliasesPackage", getBeanPackage(multipleMybaitsProperties))
                            .addPropertyValue("typeHandlersPackage", getDaoHandlerPackage(multipleMybaitsProperties))
                            .addPropertyValue("configuration", conf).getBeanDefinition());

            SqlInit.determineDataSource(dataSource, environment, registry, basePath);

        }


        private String getDaoPackage(MultipleMybaitsProperties multipleMybaitsProperties) {
            String daoPackage = multipleMybaitsProperties.getDaoPackage();
            if (NullUtil.notNull(daoPackage)) {
                return daoPackage;
            }
            String basePackage = multipleMybaitsProperties.getBasePackage();
            if (NullUtil.notNull(basePackage)) {
                return basePackage + "." + multipleMybaitsProperties.getDaoName();
            } else {
                throw new RuntimeException("daoPackage or basePackage must be not null");
            }
        }


        private String getBeanPackage(MultipleMybaitsProperties multipleMybaitsProperties) {
            String beanPackage = multipleMybaitsProperties.getBeanPackage();
            if (NullUtil.notNull(beanPackage)) {
                return beanPackage;
            }
            String basePackage = multipleMybaitsProperties.getBasePackage();
            if (NullUtil.notNull(basePackage)) {
                return basePackage + "." + multipleMybaitsProperties.getBeanName();
            } else {
                throw new RuntimeException("beanPackage or basePackage must be not null");
            }
        }


        private String getDaoHandlerPackage(MultipleMybaitsProperties multipleMybaitsProperties) {
            String daoHandlerPackage = multipleMybaitsProperties.getDaoHandlerPackage();
            if (NullUtil.notNull(daoHandlerPackage)) {
                return daoHandlerPackage;
            }
            String basePackage = multipleMybaitsProperties.getBasePackage();
            if (NullUtil.notNull(basePackage)) {
                return basePackage + "." + multipleMybaitsProperties.getDaoHandlerName();
            } else {
                throw new RuntimeException("daoHandlerPackage or basePackage must be not null");
            }
        }


        public MybatisPlusInterceptor mybatisPlusInterceptor(String scheme) {
            DbType dbType;
            if (scheme == null) {
                dbType = DbType.MYSQL;
            } else {
                dbType = DbType.getDbType(scheme);
            }

            MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
            interceptor.addInnerInterceptor(new PaginationInnerInterceptor(dbType));
            return interceptor;
        }

        /**
         * 与 {@link  DbType}类型一致
         *
         * @param dataSource 数据源
         * @return
         */
        private String getSchema(DataSource dataSource) throws SQLException {
            return getDatabaseType(dataSource).toLowerCase();
        }


        private BeanDefinitionBuilder loadMapper(ResourcePatternResolver pathResolver, String xmlPackage, String dsName, String schema) {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(SqlSessionFactoryBean.class)
                    .setRole(BeanDefinition.ROLE_INFRASTRUCTURE).addPropertyValue("vfs", SpringBootVFS.class);
            Resource[] resources = null;
            ArrayList<Resource> paths = new ArrayList<>();
            try {
                resources = pathResolver.getResources(xmlPackage + "/" + dsName + "/*.xml");
                for (Resource resource : resources) {
                    paths.add(resource);
                }
            } catch (IOException e) {
                //ignore not exist
            }

            try {
                if (NullUtil.notNull(schema)) {
                    resources = pathResolver.getResources(xmlPackage + "-" + schema + "/" + dsName + "/*.xml");
                    for (Resource resource : resources) {
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
            return SpringPropertyUtil.getPropertyInAllSource(environment, MULTIPLY_DATASOURCE_PREFIX + "." + NamingScopeContextRefreshedListener.SOURCE_NAME);
        }

        private void scanMapper(BeanDefinitionRegistry registry, String sqlSessionFactoryName, MultipleMybaitsProperties multipleMybaitsProperties) {

            String pkg = getDaoPackage(multipleMybaitsProperties);
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


        public String getDatabaseType(DataSource dataSource) throws SQLException {
            try (Connection connection = dataSource.getConnection()) {
                DatabaseMetaData metaData = connection.getMetaData();
                return metaData.getDatabaseProductName();
            } catch (SQLException e) {
                throw e;
            }
        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }
    }

}
