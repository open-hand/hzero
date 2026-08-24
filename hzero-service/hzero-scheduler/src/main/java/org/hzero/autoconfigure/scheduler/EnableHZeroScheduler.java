package org.hzero.autoconfigure.scheduler;

import java.lang.annotation.*;

import org.springframework.context.annotation.Import;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SchedulerAutoConfiguration.class)
public @interface EnableHZeroScheduler {

}
