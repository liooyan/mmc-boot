package cn.lioyan.autoconfigure.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**

 *
 * @author cn.lioyan
 * @since 2022/4/21 14:00
 */
public class SecEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String BOOTSTRAP_EAGER_LOAD = "sec.bootstrap.eagerLoad.enable";
    private static final String SEC_SOURCE_NAME = "sec-global-unique";
    private static final String SEC_DEFAULT_PROPERTY_SOURCE_NAME = "sec-default";
    private static final String BOOTSTRAP_PROPERTY_SOURCE_NAME = "bootstrap";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        //加载全局配置
        loadSECConfigFile(environment);

        if (environment.getPropertySources().contains(BOOTSTRAP_PROPERTY_SOURCE_NAME) && !environment.getProperty(BOOTSTRAP_EAGER_LOAD, Boolean.class, false)) {
            return;
        }
        loadDefaultConfig(environment, application.getMainApplicationClass());

    }

    /**
     * 无论在什么上下文中都加载的数据源,常见的就是Spring Cloud引导上下文
     *
     * @param environment 环境配置
     */
    private void loadSECConfigFile(ConfigurableEnvironment environment) {
        if (environment.getPropertySources().contains(SEC_SOURCE_NAME)) {
            return;
        }
        MutablePropertySources propertySources = environment.getPropertySources();
        //add global unique config file distinguish profile
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length > 0) {
            for (int i = activeProfiles.length - 1; i >= 0; i--) {//后面申明的优先级高
                String activeProfile = activeProfiles[i];
                addLast(propertySources, loadResourcePropertySource(SEC_SOURCE_NAME.concat("-").concat(activeProfile), "classpath:config/sec-" + activeProfile + ".properties"));
            }
        }

        addLast(propertySources, loadResourcePropertySource(SEC_SOURCE_NAME, "classpath:config/sec.properties"));
    }


    /**
     * 无论在什么上下文中都加载的数据源,常见的就是Spring Cloud引导上下文
     *
     * @param environment 环境配置
     */
    private void loadDefaultConfig(ConfigurableEnvironment environment, Class<?> mainApplicationClass) {
        MutablePropertySources propertySources = environment.getPropertySources();
        //已加载
        if (propertySources.contains(SEC_DEFAULT_PROPERTY_SOURCE_NAME)) {
            return;
        }
        //add MapPropertySource,包含主源的包名,日志文件名,SEC版本,以及dao包下的日志打印级别
        Map<String, Object> mapProp = new HashMap<>();
        if (Objects.nonNull(mainApplicationClass)) {
            String packageName = ClassUtils.getPackageName(mainApplicationClass);
            mapProp.put(ConfigProperties.APP_BASE_PACKAGE, packageName);
            mapProp.put(ConfigProperties.APP_APPLICATION_NAME, mainApplicationClass.getCanonicalName());
            mapProp.put("logging.level."+packageName, "debug");
        }
        addLast(propertySources, new MapPropertySource("SEC-map", mapProp));
    }


    private ResourcePropertySource loadResourcePropertySource(String name, Object resource) {
        try {
            if (resource instanceof Resource) {
                return new ResourcePropertySource(name, (Resource) resource);
            }
            return new ResourcePropertySource(name, resource.toString());
        } catch (IOException e) {
            return null;
        }
    }


    private void addLast(MutablePropertySources propertySources, PropertySource<?> propertySource) {
        if (propertySource == null) {
            return;
        }
        propertySources.addLast(propertySource);
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

}