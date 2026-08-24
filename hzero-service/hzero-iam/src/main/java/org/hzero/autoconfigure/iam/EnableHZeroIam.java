package org.hzero.autoconfigure.iam;

import java.lang.annotation.*;

import org.springframework.context.annotation.Import;

/**
 * 启用 HZero Iam 服务扫描
 *
 * @author bojiangzhou 2018/10/25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(IamAutoConfiguration.class)
public @interface EnableHZeroIam {

}
