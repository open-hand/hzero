package org.hzero.autoconfigure.imported;

import java.lang.annotation.*;

import org.springframework.context.annotation.Import;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ImportAutoConfiguration.class)
public @interface EnableHZeroImport {

}
