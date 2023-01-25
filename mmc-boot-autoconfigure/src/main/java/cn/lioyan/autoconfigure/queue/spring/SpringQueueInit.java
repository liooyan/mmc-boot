package cn.lioyan.autoconfigure.queue.spring;

import cn.lioyan.autoconfigure.queue.DefQueueRegistered;
import cn.lioyan.autoconfigure.queue.QueueFactory;
import cn.lioyan.autoconfigure.queue.QueueRegistered;
import cn.lioyan.autoconfigure.queue.local.LocalQueueFactory;
import org.springframework.context.annotation.Bean;

/**
 * {@link SpringQueueInit}
 *
 * @author com.lioyan
 * @since  2022/10/12  15:27
 */
public class SpringQueueInit {

    @Bean
    public QueueConumerBeanFactoryPostProcessor queueConumerBeanFactoryPostProcessor(QueueRegistered queueFactory) {
        return new QueueConumerBeanFactoryPostProcessor(queueFactory);
    }

    @Bean
    public QueueSendBeanFactoryPostProcessor queueSendBeanFactoryPostProcessor(QueueRegistered queueFactory) {
        return new QueueSendBeanFactoryPostProcessor(queueFactory);
    }

    @Bean
    public QueueRegistered queueRegistered(QueueFactory queueFactory) {
        return new DefQueueRegistered(queueFactory);
    }

    @Bean
    public LocalQueueFactory queueFactory() {
        return new LocalQueueFactory();
    }

}
