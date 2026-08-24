package org.hzero.boot.imported.infra.validator.annotation;

import java.lang.annotation.*;

import org.springframework.stereotype.Component;

/**
 * 通用校验处理类注解
 *
 * @author shuangfei.zhu@hand-china.com 2018/12/06 20:22
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ImportCommonValidator {

    /**
     * @return 检验器执行顺序
     */
    int order() default 0;
}
