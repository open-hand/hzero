package org.hzero.autoconfigure.swagger;

import java.lang.annotation.*;

import org.springframework.context.annotation.Import;

/**
 * @author bojiangzhou 2018/10/25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SwaggerAutoConfiguration.class)
public @interface EnableHZeroSwagger {

}
