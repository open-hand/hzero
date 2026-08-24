package org.hzero.oauth.security.sms.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * 启用短信登录，同时需向 HttpSecurity 中加入 SmsLoginConfigurer 的配置.
 * <pre>
 *     SmsLoginConfigurer smsLoginConfigurer = new SmsLoginConfigurer();
 *     smsLoginConfigurer
 *          .authenticationDetailsSource(smsAuthenticationDetailsSource)
 *          .successHandler(authenticationSuccessHandler)
 *          .failureHandler(smsAuthenticationFailureHandler)
 *     ;
 *     http.apply(smsLoginConfigurer);
 *     http.authenticationProvider(smsAuthenticationProvider);
 * </pre>
 * @author bojiangzhou 2019/02/25
 * @see SmsLoginConfigurer
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SmsLoginConfiguration.class)
public @interface EnableSmsLogin {

}
