package cn.lioyan.autoconfigure.queue.spring;

import cn.lioyan.autoconfigure.queue.QueueProducers;
import cn.lioyan.autoconfigure.queue.QueueRegistered;
import cn.lioyan.autoconfigure.queue.QueueSend;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;

/**
 * {@link QueueSendBeanFactoryPostProcessor}
 *
 * @author com.lioyan
 * @since 2022/10/12  11:28
 */

public class QueueSendBeanFactoryPostProcessor implements InstantiationAwareBeanPostProcessor {
    private static final Class<? extends Annotation> QueueProducers = QueueProducers.class;

    private QueueRegistered queueFactory;


    public QueueSendBeanFactoryPostProcessor(QueueRegistered queueFactory) {
        this.queueFactory = queueFactory;
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
        Class<?> clazz = bean.getClass();

        ReflectionUtils.doWithLocalFields(clazz, field -> {
            if (QueueProducers != null && field.isAnnotationPresent(QueueProducers)) {
                if (Modifier.isStatic(field.getModifiers())) {
                    throw new IllegalStateException("@QueueProducers annotation is not supported on static fields");
                }

                if (!field.getType().isAssignableFrom(QueueSend.class)) {
                    throw new IllegalStateException("@QueueProducers annotation is not supported on assignable QueueSend");
                }
                QueueProducers queueProducers = field.getAnnotation(QueueProducers.class);
                String topic = queueProducers.topic();
                QueueSend<?> queueSend = queueFactory.getQueueSend(topic);
                field.setAccessible(true);
                field.set(bean, queueSend);
            }
        });

        return pvs;
    }
}
