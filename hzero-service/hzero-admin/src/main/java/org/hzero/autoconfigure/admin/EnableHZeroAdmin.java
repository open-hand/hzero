package org.hzero.autoconfigure.admin;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author XCXCXCXCX
 * @date 2019/9/6
 * @project hzero-admin
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AdminAutoConfiguration.class)
public @interface EnableHZeroAdmin {
}
