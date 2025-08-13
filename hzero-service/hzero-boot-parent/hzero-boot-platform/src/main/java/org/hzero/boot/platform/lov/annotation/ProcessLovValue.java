package org.hzero.boot.platform.lov.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.commons.lang3.StringUtils;

/**
 * 启用值集值自动处理,将值集值转化为值集含义<br/>
 * 可注解在Spring代理的方法上<br/>
 * 会拦截被注解方法的返回值,执行{@link org.hzero.boot.platform.lov.handler.LovValueHandle#process(String[], Object)}方法
 *
 * @author gaokuo.dai@hand-china.com 2018年7月3日下午3:28:07
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface ProcessLovValue {

    /**
     * <p>待处理的对象在返回值的哪个字段中,不指定的话默认处理返回值本身</p>
     * <p>
     *  例:
     *  <ul>
     *      <li>方法返回值为Page、List等Collection接口,视为处理返回值本身,直接使用@ProcessLovValue</li>
     *      <li>方法返回值为头行结构的Object,使用@ProcessLovValue(targetField = {"", "children"}),扫描自身和children字段所包含对象(或Collection)</li>
     *      <li>方法返回值为ResponseEntity,实际对象是在其body字段中,使用@ProcessLovValue(targetField = {body})</li>
     *  </ul>
     * </p>
     * <p>目前只支持一层嵌套,既""、"children", 不支持"children.subChildren"</p>
     */
    String[] targetField() default StringUtils.EMPTY;
    
}
