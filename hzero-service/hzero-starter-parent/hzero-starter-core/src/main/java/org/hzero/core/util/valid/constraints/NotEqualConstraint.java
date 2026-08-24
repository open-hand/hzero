package org.hzero.core.util.valid.constraints;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hzero.core.util.valid.NotEqual;

/**
 * <p>
 * 字符串通用校验，校验字段必须不等于某个值
 * </p>
 *
 * @author qingsheng.chen 2018/7/6 星期五 10:48
 */
public class NotEqualConstraint implements ConstraintValidator<NotEqual, String> {
    private Set<String> values = null;

    @Override
    public void initialize(NotEqual constraintAnnotation) {
        values = new HashSet<>(Arrays.asList(constraintAnnotation.values()));
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return values == null || !values.contains(value);
    }
}
