package org.hzero.core.redis.handler;

import java.lang.annotation.*;

import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/10/23 16:13
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface QueueHandler {

    String value();
}
