package org.hzero.autoconfigure.register;

import java.lang.annotation.*;

import org.springframework.context.annotation.Import;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RegisterAutoConfiguration.class)
public @interface EnableHZeroRegister {

}
