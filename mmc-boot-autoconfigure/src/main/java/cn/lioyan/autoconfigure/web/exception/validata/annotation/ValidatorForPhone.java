package cn.lioyan.autoconfigure.web.exception.validata.annotation;

import cn.lioyan.core.util.NullUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * @author cn.lioyan
 * @since 2022/4/21 14:00
 */
public class ValidatorForPhone implements ConstraintValidator<Phone, String> {

    private final Pattern pattern = Pattern.compile("^\\d{11}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (NullUtil.isNull(value)) {
            return true;
        }
        return pattern.matcher(value).find();
    }

}

