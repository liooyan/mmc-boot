package cn.lioyan.autoconfigure.queue;

import cn.lioyan.core.util.NullUtil;
import com.google.common.collect.Lists;
import cn.lioyan.autoconfigure.queue.model.QueueConsumerParameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link DefQueueRegistered}
 *
 * @author com.lioyan
 * @since 2022/10/12  11:22
 */
public class DefQueueRegistered implements QueueRegistered {

    private Map<String, QueueSend<?>> queueSendMap = new HashMap<>();
    private Map<String, List<QueueReceiver>> queueReceiverMap = new HashMap<>();
    private QueueFactory queueFactory;


    public DefQueueRegistered(QueueFactory queueFactory) {
        this.queueFactory = queueFactory;
        new Thread(() -> {
            while (true) {
                for (Map.Entry<String, QueueSend<?>> queueSendEntry : queueSendMap.entrySet()) {
                    QueueSend queueSend = queueSendEntry.getValue();
                    if (queueSend.hasValue()) {
                        List<QueueReceiver> queueReceivers = queueReceiverMap.get(queueSendEntry.getKey());
                        if (NullUtil.notNull(queueReceivers)) {
                            for (QueueReceiver<?> queueReceiver : queueReceivers) {
                                queueSend.consumption(queueReceiver);
                            }
                        }
                    }

                }
            }

        }).start();
    }



    @Override
    public <T> QueueSend<T> getQueueSend(String topic) {
        if (queueSendMap.containsKey(topic)) {
            return (QueueSend<T>) queueSendMap.get(topic);
        } else {
            QueueSend<T> queueSend = queueFactory.getQueueSend(topic);
            queueSendMap.put(topic, queueSend);
            return queueSend;
        }
    }

    @Override
    public <T> void registeredQueueReceiver(QueueReceiver<T> queueReceiver) {
        QueueConsumerParameter queueConsumerParameter = queueReceiver.getQueueConsumerParameter();
        String topic = queueConsumerParameter.getTopic();
        if (queueReceiverMap.containsKey(topic)) {
            queueReceiverMap.get(topic).add(queueReceiver);
        } else {
            ArrayList<QueueReceiver> queueReceivers = Lists.newArrayList(queueReceiver);
            queueReceiverMap.put(topic, queueReceivers);
        }

    }
}
