package cn.lioyan.autoconfigure.config.scope;

import cn.lioyan.autoconfigure.util.SpringPropertyUtil;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;

import java.util.List;
import java.util.NoSuchElementException;


public class NamingScopeContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {

    public static final String SOURCE_NAME = "source_name";
    public static final String ALIAS_KEY = "alias";


    private NameReference nameReference;
    private NamingScopeBeanRegistry namingScopeBeanRegistry;


    private void init(ContextRefreshedEvent event) {
        nameReference = event.getApplicationContext().getBean(NameReference.class);
        namingScopeBeanRegistry = event.getApplicationContext().getBean(NamingScopeBeanRegistry.class);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        init(event);
        ApplicationContext applicationContext = event.getApplicationContext();
        Environment environment = applicationContext.getEnvironment();
        String[] scopeFactoryBeanNames = applicationContext.getBeanNamesForType(ScopeFactory.class);

        for (String scopeFactoryBeanName : scopeFactoryBeanNames) {
            ScopeFactory scopeFactory = applicationContext.getBean(scopeFactoryBeanName, ScopeFactory.class);
            String configBasePath = scopeFactory.getConfigBasePath();
            List<String> scopes = SpringPropertyUtil.getPropertyInAllSource(applicationContext.getEnvironment(), configBasePath + "." + SOURCE_NAME);
            Class<? extends ScopeConfig> configClass = scopeFactory.getConfigClass();

            //加载别名
            for (String scope : scopes) {
                String alias = environment.getProperty(configBasePath + "." + scope + "." + ALIAS_KEY, String.class, scope);
                nameReference.addAlias(scope, alias);
            }
            String[] aliasGroundName = nameReference.getAliasGroundName();


            // 加载配置
            for (String groundName : aliasGroundName) {
                ScopeConfig scopeConfig;
                try {
                    scopeConfig = Binder.get(environment).bind(configBasePath + "." + groundName, Bindable.of(configClass)).get();
                    Object scopeBean = scopeFactory.getBean(scopeConfig);
                    namingScopeBeanRegistry.registry(scopeFactory.getBeanClass(), groundName, scopeBean);
                } catch (NoSuchElementException e) {
                    //没有配置,获取默认配置
                    try {
                        Object beanDef = namingScopeBeanRegistry.getBeanDef(scopeFactory.getBeanClass());
                        if(beanDef == null){
                            scopeConfig = Binder.get(environment).bind(configBasePath , Bindable.of(configClass)).get();
                            beanDef = scopeFactory.getBean(scopeConfig);
                            namingScopeBeanRegistry.registry(scopeFactory.getBeanClass(), null, beanDef);
                        }
                        //同时也注册到groundName中
                        namingScopeBeanRegistry.registry(scopeFactory.getBeanClass(), groundName, beanDef);
                    } catch (NoSuchElementException exception) {
                        continue;
                    }
                }
            }

            //结束后，也要尝试加载默认配置
            Object beanDef = namingScopeBeanRegistry.getBeanDef(scopeFactory.getBeanClass());
            try {
                if(beanDef == null){
                    ScopeConfig  scopeConfig = Binder.get(environment).bind(configBasePath , Bindable.of(configClass)).get();
                    beanDef = scopeFactory.getBean(scopeConfig);
                    namingScopeBeanRegistry.registry(scopeFactory.getBeanClass(), null, beanDef);
                }
            } catch (NoSuchElementException exception) {
                continue;
            }

            System.out.println(aliasGroundName);
        }

        BeanDefinitionRegistry registry = getBeanDefinitionRegistry(event);
        String[] beanNames = registry.getBeanDefinitionNames();

        for (String beanName : beanNames) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
            String beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName != null) {
                // 使用AnnotationUtils.findAnnotation方法获取指定注解的信息
                ModuleImport annotation = AnnotationUtils.findAnnotation(ClassUtils.resolveClassName(beanClassName, null), ModuleImport.class);

                // 如果Bean定义中带有指定注解，则进行处理
                if (annotation != null) {
                    System.out.println("Found bean with @MyAnnotation: " + beanName);
                    // 在这里处理带有@MyAnnotation注解的Bean定义，执行你想要的操作
                    // ...
                }
            }
        }
    }

    private BeanDefinitionRegistry getBeanDefinitionRegistry(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() != null) {
            // 如果存在父ApplicationContext，则获取父ApplicationContext的Bean定义注册表
            return (BeanDefinitionRegistry) event.getApplicationContext().getParentBeanFactory();
        } else {
            // 否则直接获取ApplicationContext的Bean定义注册表
            return (BeanDefinitionRegistry) event.getApplicationContext().getAutowireCapableBeanFactory();
        }
    }

}