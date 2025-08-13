package org.hzero.boot.imported.infra.validator.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

/**
 * 自定义校验处理类注解
 *
 * @author shuangfei.zhu@hand-china.com 2018/12/06 20:22
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ImportValidators {

    ImportValidator[] value();
}
