package org.hzero.sso.core.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({SsoAutoConfiguration.class, SsoWebSecurityConfigurerAdapter.class})
public @interface EnableSsoLogin {

}
