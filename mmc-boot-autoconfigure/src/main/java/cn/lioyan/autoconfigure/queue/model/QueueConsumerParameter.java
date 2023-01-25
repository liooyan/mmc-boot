package cn.lioyan.autoconfigure.queue.model;

/**
 * {@link QueueConsumerParameter}
 *
 * @author com.lioyan
 * @since 2022/10/12  11:16
 */
public class QueueConsumerParameter {
    private String topic;

    private String groupId = "default";

    private boolean exceptionRetry;


    private Class<?> exceptionHandler;


    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public boolean isExceptionRetry() {
        return exceptionRetry;
    }

    public void setExceptionRetry(boolean exceptionRetry) {
        this.exceptionRetry = exceptionRetry;
    }

    public Class<?> getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(Class<?> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }
}
