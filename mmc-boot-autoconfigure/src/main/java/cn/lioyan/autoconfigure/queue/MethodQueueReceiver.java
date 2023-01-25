package cn.lioyan.autoconfigure.queue;

import cn.lioyan.autoconfigure.queue.model.QueueConsumerParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * {@link MethodQueueReceiver}
 *
 * @author com.lioyan
 * @since 2022/10/12  14:21
 */
public class MethodQueueReceiver<T> implements QueueReceiver<T> {

    private QueueConsumerParameter queueConsumerParameter;
    private Object target;
    private Method method;
    private Object[] args;
    private int dataIndex = 0;

    public MethodQueueReceiver(QueueConsumerParameter queueConsumerParameter, Object target, Method method) {
        this.queueConsumerParameter = queueConsumerParameter;
        this.target = target;
        this.method = method;
    }

    @Override
    public void receiver(List<T> list) {
        if(args == null){
            Class<?>[] parameterTypes = method.getParameterTypes();
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            args = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameterType = parameterTypes[i];
                if(parameterType == QueueConsumerParameter.class){
                    args[i] = queueConsumerParameter;
                }else  if(parameterAnnotations[i].length != 0 ){
                    Annotation[] parameterAnnotation = parameterAnnotations[i];
                    for (Annotation annotation : parameterAnnotation) {
                        if(annotation.annotationType() == QueueData.class){
                            dataIndex = i;
                        }
                    }
                }
            }
        }
        try {
            args[dataIndex] = list;
            method.invoke(target,args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public QueueConsumerParameter getQueueConsumerParameter() {
        return queueConsumerParameter;
    }
}
