package org.hzero.lock.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import org.hzero.lock.enums.LockType;

/**
 * 加锁注解
 *
 * @author xianzhi.chen@hand-china.com 2019年1月14日下午4:07:44
 */
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Lock {

    /**
     * 锁的名称
     */
    String name() default "";

    /**
     * name 是否使用spel表达式
     */
    boolean nameIsSpel() default false;

    /**
     * 锁类型，默认可重入锁
     */
    LockType lockType() default LockType.FAIR;

    /**
     * 尝试加锁，最多等待时间
     */
    long waitTime() default 60L;

    /**
     * 上锁以后xxx秒自动解锁
     */
    long leaseTime() default 60L;

    /**
     * 锁时长单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 自定义业务key
     */
    String[] keys() default {};
}
