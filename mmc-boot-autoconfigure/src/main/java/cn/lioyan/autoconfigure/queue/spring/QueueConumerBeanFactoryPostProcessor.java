package cn.lioyan.autoconfigure.queue.spring;

import cn.lioyan.autoconfigure.queue.MethodQueueReceiver;
import cn.lioyan.autoconfigure.queue.QueueConsumer;
import cn.lioyan.autoconfigure.queue.QueueRegistered;
import cn.lioyan.autoconfigure.queue.model.QueueConsumerParameter;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;

/**
 * {@link QueueConumerBeanFactoryPostProcessor}
 *
 * @author com.lioyan
 * @since 2022/10/12  11:28
 */

public class QueueConumerBeanFactoryPostProcessor implements InstantiationAwareBeanPostProcessor {
    private static final Class<? extends Annotation> queueConsumerClass = QueueConsumer.class;

    private QueueRegistered queueFactory;

    public QueueConumerBeanFactoryPostProcessor(QueueRegistered queueFactory) {
        this.queueFactory = queueFactory;
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
        Class<?> clazz = bean.getClass();
        ReflectionUtils.doWithLocalMethods(clazz, method -> {
            QueueConsumer annotation = method.getAnnotation(QueueConsumer.class);
            if(annotation == null){
                return;
            }else {
                QueueConsumerParameter queueConsumerParameter = new QueueConsumerParameter();
                queueConsumerParameter.setTopic(annotation.topic());

                MethodQueueReceiver localQueueReceiver = new MethodQueueReceiver(queueConsumerParameter,bean,method);
                queueFactory.registeredQueueReceiver(localQueueReceiver);
            }


        });

        return pvs;
    }
}
