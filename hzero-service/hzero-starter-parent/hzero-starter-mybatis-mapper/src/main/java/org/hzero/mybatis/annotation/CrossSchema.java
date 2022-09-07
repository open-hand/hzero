package org.hzero.mybatis.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * 跨 Schema 查询多个租户的信息
 * </p>
 *
 * @author qingsheng.chen 2018/9/20 星期四 14:15
 */
@Retention(RUNTIME)
@Target(METHOD)
@Documented
public @interface CrossSchema {
    /**
     * 租户 ID 参数的名称，参数必须为 long/Long 或者 List&lt;Long&gt;
     */
    String value() default "tenantId";
}
