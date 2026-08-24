package org.hzero.autoconfigure.message;

import java.lang.annotation.*;

import org.springframework.context.annotation.Import;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MessageAutoConfiguration.class)
public @interface EnableHZeroMessage {

}
