package org.hzero.autoconfigure.config;

import java.lang.annotation.*;

import org.springframework.context.annotation.Import;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ConfigAutoConfiguration.class)
public @interface EnableHZeroConfig {

}
