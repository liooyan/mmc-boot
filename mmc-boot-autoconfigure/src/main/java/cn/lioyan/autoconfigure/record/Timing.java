package cn.lioyan.autoconfigure.record;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link Timing}
 *
 * @author com.lioyan
 * @since 2022/4/14 14:23
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Timing {
}
