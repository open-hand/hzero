package org.hzero.autoconfigure.oauth;

import java.lang.annotation.*;

import org.springframework.context.annotation.Import;

/**
 * 启用 HZero OAuth 服务扫描
 *
 * @author bojiangzhou 2018/10/25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(OauthAutoConfiguration.class)
public @interface EnableHZeroOauth {

}
