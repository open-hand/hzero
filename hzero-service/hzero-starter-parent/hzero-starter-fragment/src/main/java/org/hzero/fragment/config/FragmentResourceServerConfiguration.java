package org.hzero.fragment.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 跨域配置
 *
 * @author shuangfei.zhu@hand-china.com 2019/12/27 9:41
 */
@Configuration
@Order(199)
public class FragmentResourceServerConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable()
                .and().csrf().disable();
    }
}
