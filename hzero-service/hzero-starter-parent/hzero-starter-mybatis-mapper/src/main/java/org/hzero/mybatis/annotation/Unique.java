package org.hzero.mybatis.annotation;


import java.lang.annotation.*;

/**
 * <p>
 * 唯一校验
 * </p>
 *
 * @author qingsheng.chen 2019/3/18 星期一 17:18
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Unique.List.class)
public @interface Unique {
    String DEFAULT_CONSTRAINT_NAME = "allColumnWithAnnotation";

    /**
     * Alias for {@code constraintName()}
     *
     * @return Constraint Name
     */
    String value() default DEFAULT_CONSTRAINT_NAME;

    /**
     * Alias for {@code value()}
     *
     * @return Constraint Name
     */
    String constraintName() default DEFAULT_CONSTRAINT_NAME;

    /**
     * Defines several {@code @Unique} constraints on the same element.
     *
     * @see Unique
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        Unique[] value();
    }
}
