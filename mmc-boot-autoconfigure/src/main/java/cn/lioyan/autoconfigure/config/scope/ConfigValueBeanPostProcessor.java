package cn.lioyan.autoconfigure.config.scope;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ConfigValueBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();

//        for (Field field : clazz.getDeclaredFields()) {
//            if (field.isAnnotationPresent(ConfigValue.class)) {
//                ConfigValue configValue = field.getAnnotation(ConfigValue.class);
//                String propertyName = configValue.value();
//                if (propertyName.isEmpty()) {
//                    propertyName = field.getName();
//                }
//                Object propertyValue = applicationContext.getEnvironment().getProperty(propertyName);
//                ReflectionUtils.setField(field, bean, propertyValue);
//            }
//        }
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