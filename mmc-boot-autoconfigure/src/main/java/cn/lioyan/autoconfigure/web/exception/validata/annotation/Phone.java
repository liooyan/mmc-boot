package cn.lioyan.autoconfigure.web.exception.validata.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author cn.lioyan
 * @since 2022/4/21 14:00
 */
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidatorForPhone.class)
public @interface Phone {

    String message() default "手机号不正确";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
