package org.hzero.boot.scheduler.infra.annotation;

import java.lang.annotation.*;

import org.springframework.stereotype.Component;

/**
 * 任务执行类注解
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/16 20:22
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface JobHandler {

    String value();
}