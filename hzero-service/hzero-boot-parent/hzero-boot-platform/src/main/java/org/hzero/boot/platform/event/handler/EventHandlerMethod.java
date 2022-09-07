package org.hzero.boot.platform.event.handler;

import java.lang.annotation.*;

/**
 * 标注事件处理方法
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/13 17:27
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EventHandlerMethod {

    /**
     * 方法名称，配置到事件规则的方法名。<br/>
     * 默认为方法名称，通常，对于一些重载方法，需要配名字以区别。
     * 
     * @return 方法名称
     */
    String name() default "";

}
