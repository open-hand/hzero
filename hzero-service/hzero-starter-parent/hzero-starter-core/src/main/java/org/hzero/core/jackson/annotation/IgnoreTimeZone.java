package org.hzero.core.jackson.annotation;

import java.lang.annotation.*;

import org.hzero.core.jackson.config.ObjectMapperConfiguration;

/**
 * <p>
 * 注解在Bean的 java.util.Date 类型的属性上，
 * Jackson 在序列化和反序列化时忽略时区，仅在
 * org.hzero.common.jackson.config.ObjectMapperConfiguration
 * 中声明的ObjectMapper有效
 *
 * @author qingsheng.chen 2018/8/27 星期一 9:17
 * @see ObjectMapperConfiguration#objectMapper()
 * </p>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreTimeZone {
}
