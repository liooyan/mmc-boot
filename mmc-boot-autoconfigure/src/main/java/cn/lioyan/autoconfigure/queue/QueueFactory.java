package cn.lioyan.autoconfigure.queue;

/**
 * {@link QueueFactory}
 *
 * @author com.lioyan
 * @since 2022/10/12  11:20
 */
public interface QueueFactory {

    /**
     * 发送一条数据
     */
    <T> QueueSend<T> getQueueSend(String topic);




}
