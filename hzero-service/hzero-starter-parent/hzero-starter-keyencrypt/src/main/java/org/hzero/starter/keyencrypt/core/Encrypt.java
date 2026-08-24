package org.hzero.starter.keyencrypt.core;

import com.fasterxml.jackson.annotation.JacksonAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xiangyu.qi01@hand-china.com on 2019-11-29.
 */
@JacksonAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface Encrypt {

    /**
     * @return 加密或者解密的key
     */
    String value() default "";

    /**
     * @return 加密用的字段名，配置该属性，会使用字段对应的值进行加密，value值失效
     */
    String fieldName() default "";

    /**
     * @return 忽略值
     */
    String[] ignoreValue() default {};

    /**
     * @return 解密时忽略用户冲突
     */
    boolean ignoreUserConflict() default false;
}
