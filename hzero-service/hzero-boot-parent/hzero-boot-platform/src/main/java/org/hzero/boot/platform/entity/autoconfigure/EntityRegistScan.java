package org.hzero.boot.platform.entity.autoconfigure;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * description
 *
 * @author xingxing.wu@hand-china.com 2019/07/16 9:39
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(EntityRegistScannerRegistrar.class)
public @interface EntityRegistScan {
    String[] basePackages();
}
