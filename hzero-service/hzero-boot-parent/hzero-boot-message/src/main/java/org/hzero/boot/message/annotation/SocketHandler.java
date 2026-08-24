package org.hzero.boot.message.annotation;

import java.lang.annotation.*;

import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/08/28 20:44
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface SocketHandler {

    String value();
}
