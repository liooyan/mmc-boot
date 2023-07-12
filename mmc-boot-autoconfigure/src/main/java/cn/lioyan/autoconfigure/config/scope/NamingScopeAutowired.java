package cn.lioyan.autoconfigure.config.scope;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link NamingScopeAutowired}
 *
 * @author com.lioyan
 * @since  2023/7/12  13:33
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NamingScopeAutowired {
    String value() default "";
}
