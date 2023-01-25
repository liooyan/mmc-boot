package cn.lioyan.autoconfigure.queue;

/**
 * {@link QueueRegistered}
 *
 * @author com.lioyan
 * @since 2022/10/12  11:20
 */
public interface QueueRegistered {

    /**
     * 发送一条数据
     */
    <T> QueueSend<T> getQueueSend(String topic);


    <T> void  registeredQueueReceiver(QueueReceiver<T> queueReceiver);


}
