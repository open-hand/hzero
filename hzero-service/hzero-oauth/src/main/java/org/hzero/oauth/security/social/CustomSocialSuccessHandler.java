package org.hzero.oauth.security.social;

import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.Authentication;

import org.hzero.oauth.security.config.SecurityProperties;
import org.hzero.oauth.security.event.LoginEvent;
import org.hzero.starter.social.core.security.SocialSuccessHandler;

/**
 * 登录成功处理器
 *
 * @author bojiangzhou 2019/02/25
 */
public class CustomSocialSuccessHandler extends SocialSuccessHandler implements ApplicationContextAware {

    private SecurityProperties securityProperties;
    private ApplicationContext applicationContext;

    public CustomSocialSuccessHandler(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @PostConstruct
    private void init() {
        this.setDefaultTargetUrl(securityProperties.getLogin().getSuccessUrl());
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 发布登录成功事件
        LoginEvent loginEvent = new LoginEvent(request);
        applicationContext.publishEvent(loginEvent);

        super.onAuthenticationSuccess(request, response, authentication);
    }

    protected SecurityProperties getSecurityProperties() {
        return securityProperties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}


