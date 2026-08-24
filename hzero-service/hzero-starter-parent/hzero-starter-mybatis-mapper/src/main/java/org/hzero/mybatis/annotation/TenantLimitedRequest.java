package org.hzero.mybatis.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * 租户限制的请求
 * </p>
 *
 * @author qingsheng.chen 2019/2/27 星期三 16:52
 */
@Retention(RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface TenantLimitedRequest {

    /**
     * true  : TENANT_ID = ?
     * false : TENANT_ID IN (, ?)
     *
     * @return 是否使用等于条件
     */
    boolean equal() default false;
}
