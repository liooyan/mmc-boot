package cn.lioyan.autoconfigure.config.scope;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NamingScopeBeanBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;
    private NameReference nameReference;
    private NamingScopeBeanRegistry namingScopeBeanRegistry;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.nameReference =NamingScopeContextRefreshedListener.nameReference;
        this.namingScopeBeanRegistry = NamingScopeContextRefreshedListener.namingScopeBeanRegistry;
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(NamingScopeAutowired.class)) {
                NamingScopeAutowired configValue = field.getAnnotation(NamingScopeAutowired.class);
                String scopeName = configValue.value();
                if (scopeName.isEmpty()) {
                    scopeName = null;
                }else {
                    scopeName = nameReference.getAlias(scopeName);
                }
                Object scopeBean = namingScopeBeanRegistry.getBean(field.getType(), scopeName);
                if(scopeBean != null){
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, bean, scopeBean);
                }
            }
        }
//
//        for (Method method : clazz.getDeclaredMethods()) {
//            if (method.isAnnotationPresent(ConfigValue.class)) {
//                ConfigValue configValue = method.getAnnotation(ConfigValue.class);
//                String propertyName = configValue.value();
//                if (propertyName.isEmpty()) {
//                    String methodName = method.getName();
//                    propertyName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
//                }
//                Object propertyValue = applicationContext.getEnvironment().getProperty(propertyName);
//                ReflectionUtils.invokeMethod(method, bean, propertyValue);
//            }
//        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}