package cn.lioyan.autoconfigure.config.scope;

import cn.lioyan.autoconfigure.util.SpringPropertyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Constructor;
import java.util.*;


public class NamingScopeContextRefreshedListener implements ApplicationListener<ApplicationPreparedEvent> {


    public static final String SOURCE_NAME = "source_name";
    public static final String ALIAS_KEY = "alias";


    public static NameReference nameReference = null;
    public static NamingScopeBeanRegistry namingScopeBeanRegistry = null;
    private ConfigurableApplicationContext applicationContext;

    private void init() {
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        nameReference = new NameReference();
        namingScopeBeanRegistry = new NamingScopeBeanRegistry();
        beanFactory.registerSingleton(NameReference.class.getName(), nameReference);
        beanFactory.registerSingleton(NamingScopeBeanRegistry.class.getName(), namingScopeBeanRegistry);

    }

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        applicationContext = event.getApplicationContext();
        init();
        BeanDefinitionRegistry registry = null;
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        if (beanFactory instanceof BeanDefinitionRegistry) {
            registry = (BeanDefinitionRegistry) beanFactory;
        }
        Environment environment = applicationContext.getEnvironment();
        List<String> scopeFactoryNames = SpringFactoriesLoader.loadFactoryNames(ScopeFactory.class, applicationContext.getClassLoader());
        List<ScopeFactory> springFactoriesInstances = createSpringFactoriesInstances(applicationContext.getClassLoader(), new HashSet<>(scopeFactoryNames));

        for (ScopeFactory scopeFactory : springFactoriesInstances) {
            String configBasePath = scopeFactory.getConfigBasePath();
            List<String> scopes = SpringPropertyUtil.getPropertyInAllSource(applicationContext.getEnvironment(), configBasePath + "." + SOURCE_NAME);
            Class configClass = scopeFactory.getConfigClass();

            //加载别名
            for (String scope : scopes) {
                String alias = environment.getProperty(configBasePath + "." + scope + "." + ALIAS_KEY, String.class, scope);
                nameReference.addAlias(scope, alias);
            }
            String[] aliasGroundName = nameReference.getAliasGroundName();


            // 加载配置
            for (String groundName : aliasGroundName) {
                Object scopeConfig;
                try {
                    scopeConfig = Binder.get(environment).bind(configBasePath + "." + groundName, Bindable.of(configClass)).get();
                    Object scopeBean = scopeFactory.getBean(scopeConfig);
                    if (registry != null) {
                        scopeFactory.registryBeanDefinition(scopeConfig, registry);
                    }
                    if (scopeBean != null) {
                        namingScopeBeanRegistry.registry(scopeFactory.getBeanClass(), groundName, scopeBean);
                    }
                } catch (NoSuchElementException e) {
                    //没有配置,获取默认配置
                    try {
                        Object beanDef = namingScopeBeanRegistry.getBeanDef(scopeFactory.getBeanClass());
                        if (beanDef == null) {
                            scopeConfig = Binder.get(environment).bind(configBasePath, Bindable.of(configClass)).get();
                            beanDef = scopeFactory.getBean(scopeConfig);
                            if (registry != null) {
                                scopeFactory.registryBeanDefinition(scopeConfig, registry);
                            }
                            if (beanDef != null) {
                                namingScopeBeanRegistry.registry(scopeFactory.getBeanClass(), null, beanDef);
                                //同时也注册到groundName中
                                namingScopeBeanRegistry.registry(scopeFactory.getBeanClass(), groundName, beanDef);
                            }
                        }
                    } catch (NoSuchElementException exception) {
                        continue;
                    }
                }
            }

            //结束后，也要尝试加载默认配置
            Object beanDef = namingScopeBeanRegistry.getBeanDef(scopeFactory.getBeanClass());
            try {
                if (beanDef == null) {
                    Object scopeConfig = Binder.get(environment).bind(configBasePath, Bindable.of(configClass)).get();
                    beanDef = scopeFactory.getBean(scopeConfig);
                    if (registry != null) {
                        scopeFactory.registryBeanDefinition(scopeConfig, registry);
                    }
                    if (beanDef != null) {
                        namingScopeBeanRegistry.registry(scopeFactory.getBeanClass(), null, beanDef);
                    }
                }
            } catch (NoSuchElementException exception) {
                continue;
            }

        }

    }


    private List<ScopeFactory> createSpringFactoriesInstances(
            ClassLoader classLoader, Set<String> names) {
        List<ScopeFactory> instances = new ArrayList<>(names.size());
        for (String name : names) {
            try {
                Class<?> instanceClass = ClassUtils.forName(name, classLoader);
                Constructor<?> constructor = instanceClass.getDeclaredConstructor(null);
                ScopeFactory instance = (ScopeFactory) BeanUtils.instantiateClass(constructor, new Object[0]);
                instances.add(instance);
            } catch (Throwable ex) {
                throw new IllegalArgumentException("Cannot instantiate ScopeFactory : " + name, ex);
            }
        }
        return instances;
    }

//    private BeanDefinitionRegistry getBeanDefinitionRegistry() {
//        if (event.getApplicationContext().getParent() != null) {
//            // 如果存在父ApplicationContext，则获取父ApplicationContext的Bean定义注册表
//            return (BeanDefinitionRegistry) event.getApplicationContext().getParentBeanFactory();
//        } else {
//            // 否则直接获取ApplicationContext的Bean定义注册表
//            return (BeanDefinitionRegistry) event.getApplicationContext().getAutowireCapableBeanFactory();
//        }
//    }


}