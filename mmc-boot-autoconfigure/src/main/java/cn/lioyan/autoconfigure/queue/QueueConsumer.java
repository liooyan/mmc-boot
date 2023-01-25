package cn.lioyan.autoconfigure.queue;

import java.lang.annotation.*;

/**
 * {@link QueueConsumer}
 *
 * @author com.lioyan
 * @since 2022/10/12  11:07
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueueConsumer {

    String topic();

    String groupId() default "default";


    boolean exceptionRetry() default false;


    Class<? extends QueueExceptionHandler> exceptionHandler() default DefaultQueueExceptionHandler.class;

}
