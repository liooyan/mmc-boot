package cn.lioyan.autoconfigure.config.scope;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

public class NamingScopeContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        BeanDefinitionRegistry registry = getBeanDefinitionRegistry(event);
        String[] beanNames = registry.getBeanDefinitionNames();

        for (String beanName : beanNames) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
            String beanClassName = beanDefinition.getBeanClassName();
            if(beanClassName != null){
                // 使用AnnotationUtils.findAnnotation方法获取指定注解的信息
                ModuleImport annotation = AnnotationUtils.findAnnotation(
                        ClassUtils.resolveClassName(beanClassName, null), ModuleImport.class);

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