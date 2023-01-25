package cn.lioyan.autoconfigure.queue;

import java.util.List;

/**
 * {@link QueueSend}
 *
 * 队列数据发送器
 * @author com.lioyan
 * @since 2022/10/12  11:20
 */
public interface QueueSend<T> {

    /**
     * 发送一条数据
     * @param t 数据
     */
    void send(T t);


    /**
     * 批量发送数据
     * @param list 批量发送数据
     */
    void send(List<T> list);


    boolean hasValue();

    void consumption(QueueReceiver<T> queueReceiver);






}
