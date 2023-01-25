package cn.lioyan.autoconfigure.queue.local;

import cn.lioyan.core.util.NullUtil;
import com.google.common.collect.Lists;
import cn.lioyan.autoconfigure.queue.QueueReceiver;
import cn.lioyan.autoconfigure.queue.QueueSend;

import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * {@link LocalQueueSend}
 *
 * @author com.lioyan
 * @since 2022/10/12  11:22
 */
public class LocalQueueSend<T> implements QueueSend<T> {

    BlockingDeque<T> queue = new LinkedBlockingDeque<>();


    @Override
    public void send(T o) {
        queue.add(o);
    }

    @Override
    public void send(List<T> list) {
        if (NullUtil.notNull(list)) {
            queue.addAll(list);
        }
    }

    @Override
    public boolean hasValue() {
        return queue.size() != 0;
    }

    @Override
    public void consumption(QueueReceiver<T> queueReceiver) {
        queueReceiver.receiver( Lists.newArrayList(queue.poll()));
    }


}
