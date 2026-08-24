package org.hzero.core.jackson.annotation;

import java.lang.annotation.*;

import org.hzero.core.jackson.config.ObjectMapperConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

/**
 * <p>
 * 启用忽略时区注解，由于猪齿鱼提供的 ObjectMapper Bean 标记为 Primary，需要手动引入优先级更高的依赖，
 *
 * @author qingsheng.chen 2018/8/27 星期一 11:28
 * @see org.hzero.core.jackson.annotation.IgnoreTimeZone
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ImportAutoConfiguration(ObjectMapperConfiguration.class)
public @interface EnableObjectMapper {
}
