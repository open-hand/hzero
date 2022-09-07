package org.hzero.boot.imported.infra.validator.annotation;

import java.lang.annotation.*;

import org.springframework.stereotype.Component;

/**
 * 自定义校验处理类注解参数
 *
 * @author shuangfei.zhu@hand-china.com 2018/12/06 20:22
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ImportValidator {

    String templateCode();

    String tenantNum() default "";

    /**
     * @return 页下标，从0开始
     */
    int sheetIndex() default 0;

    /**
     * @return 页名称
     */
    String sheetName() default "";
}
