package cn.lioyan.autoconfigure.web.response;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link ExcludeRestRespResponse}
 *
 * @author com.lioyan
 * @since 2022/4/13 15:14
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExcludeRestRespResponse {
}
