package cn.lioyan.autoconfigure.queue;

import cn.lioyan.autoconfigure.queue.model.QueueConsumerParameter;

import java.util.List;

/**
 * {@link QueueReceiver}
 *
 * 队列数据发送器
 * @author com.lioyan
 * @since 2022/10/12  11:20
 */
public interface QueueReceiver<T> {



    /**
     * 批量发送数据
     * @param list 批量发送数据
     */
    void receiver( List<T> list);

    QueueConsumerParameter getQueueConsumerParameter();

}
