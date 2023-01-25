package cn.lioyan.autoconfigure.queue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link QueueProducers}
 *
 * @author com.lioyan
 * @since  2022/10/12  11:18
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface QueueProducers {
    String topic();


}
