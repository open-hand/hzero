package org.hzero.oauth.security.custom;

import org.hzero.oauth.security.config.SecurityProperties;
import org.hzero.oauth.security.event.LoginEvent;
import org.hzero.oauth.security.util.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 基础登录成功处理器
 *
 * @author bergturing 2021/06/18
 */
public abstract class BaseAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseAuthenticationSuccessHandler.class);

    /**
     * 配置对象
     */
    private final SecurityProperties securityProperties;

    private ApplicationContext applicationContext;

    public BaseAuthenticationSuccessHandler(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // 发布登录成功事件
        LoginEvent loginEvent = new LoginEvent(request);
        applicationContext.publishEvent(loginEvent);

        super.onAuthenticationSuccess(request, response, authentication);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        String targetUrl = RequestUtil.getBaseURL(request) + "/oauth/authorize" +
                "?response_type=token" +
                "&client_id=" + securityProperties.getLogin().getDefaultClientId() +
                "&redirect_uri=" + securityProperties.getLogin().getSuccessUrl();
        LOGGER.debug("Using default authorize target url: [{}]", targetUrl);
        return targetUrl;
    }

    protected SecurityProperties getSecurityProperties() {
        return securityProperties;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
