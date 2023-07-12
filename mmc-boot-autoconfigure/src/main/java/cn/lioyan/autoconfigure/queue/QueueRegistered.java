package cn.lioyan.autoconfigure.queue;

/**
 * {@link QueueRegistered}
 *
 * @author com.lioyan
 * @since 2022/10/12  11:20
 */
public interface QueueRegistered {

    /**
     * 获取队列发送器
     * @param topic 队列名称
     * @return 队列发送器
     * @param <T> 泛型
     */
    <T> QueueSend<T> getQueueSend(String topic);


    <T> void  registeredQueueReceiver(QueueReceiver<T> queueReceiver);


}
