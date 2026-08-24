/*
 * #{copyright}#
 */

package io.choerodon.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 多语言字段注解.
 *
 * @author fan@choerodon.io
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiLanguageField {
}