package org.hzero.mybatis.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * 数据安全注解，使用该字段注解的字段会被插入到数据库之前加密，读取时解密，需要使用BaseMapper
 * </p>
 *
 * @author qingsheng.chen 2018/9/19 星期三 19:40
 */
@Retention(RUNTIME)
@Target(FIELD)
@Documented
public @interface DataSecurity {
}
