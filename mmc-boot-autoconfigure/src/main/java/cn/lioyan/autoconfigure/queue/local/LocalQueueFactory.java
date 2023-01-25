package cn.lioyan.autoconfigure.queue.local;

import cn.lioyan.autoconfigure.queue.QueueFactory;
import cn.lioyan.autoconfigure.queue.QueueSend;

/**
 * {@link LocalQueueFactory}
 *
 * @author com.lioyan
 * @since  2022/10/12  15:30
 */
public class LocalQueueFactory implements QueueFactory {
    @Override
    public <T> QueueSend<T> getQueueSend(String topic) {
        return new LocalQueueSend<>();
    }

}
