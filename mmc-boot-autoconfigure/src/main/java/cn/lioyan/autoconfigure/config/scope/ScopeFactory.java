package cn.lioyan.autoconfigure.config.scope;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * 用于需要区域范围的工程类
 */
public interface ScopeFactory<T, Config extends ScopeConfig> {


    String getConfigBasePath();

    Class<? extends ScopeConfig> getConfigClass();
    Class<T> getBeanClass();

    T getBean(Config config);

    default void  registryBeanDefinition(Config config,BeanDefinitionRegistry beanDefinitionRegistry){}

}
