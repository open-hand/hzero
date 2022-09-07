package org.hzero.core.util.valid;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

import org.hzero.core.util.valid.constraints.NotEqualConstraint;

/**
 * <p>
 * 字符串通用校验，校验字段必须不等于某个值
 * </p>
 *
 * @author qingsheng.chen 2018/7/6 星期五 10:46
 */
@Retention(RUNTIME)
@Target(FIELD)
@Documented
@Constraint(validatedBy = NotEqualConstraint.class)
public @interface NotEqual {
    String message() default "{org.hzero.core.util.valid.NotEqual.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] values() default {};
    /**
     * Defines several {@link NotEqual} annotations on the same element.
     *
     * @see org.hzero.core.util.valid.NotEqual
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        NotEqual[] value();
    }
}
