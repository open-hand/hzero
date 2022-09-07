package org.hzero.oauth.security.custom;

import java.io.IOException;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import io.choerodon.core.oauth.CustomUserDetails;

import org.hzero.core.util.TokenUtils;
import org.hzero.oauth.domain.entity.User;
import org.hzero.oauth.security.config.SecurityProperties;
import org.hzero.oauth.security.event.LogoutEvent;
import org.hzero.oauth.security.service.LoginRecordService;
import org.hzero.oauth.security.service.UserAccountService;

/**
 * 登出处理器
 *
 * @author bojiangzhou
 */
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomLogoutSuccessHandler.class);

    private final TokenStore tokenStore;
    private final LoginRecordService loginRecordService;
    private final SecurityProperties securityProperties;
    private final UserAccountService userAccountService;
    private ApplicationContext applicationContext;

    public CustomLogoutSuccessHandler(TokenStore tokenStore,
                                      LoginRecordService loginRecordService,
                                      SecurityProperties securityProperties,
                                      UserAccountService userAccountService) {
        this.tokenStore = tokenStore;
        this.loginRecordService = loginRecordService;
        this.securityProperties = securityProperties;
        this.userAccountService = userAccountService;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        String token = TokenUtils.getToken(request);

        // 退出地址
        String logoutUrl = getLogoutUrl(request, token);

        LOGGER.debug("logout info, token={}, logoutUrl={}", token, logoutUrl);

        if (authentication == null && token != null) {
            authentication = tokenStore.readAuthentication(token);
        }
        if (authentication == null) {
            LOGGER.warn("logout user not found. token={}", token);
            response.sendRedirect(logoutUrl);
            return;
        }

        // 查询登出用户
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails details = (CustomUserDetails) principal;
            User logoutUser = userAccountService.findLoginUser(details.getUserId());
            loginRecordService.saveLocalLoginUser(logoutUser);
        }

        // 发布退出登录事件
        LogoutEvent logoutEvent = new LogoutEvent(request, response, authentication);
        applicationContext.publishEvent(logoutEvent);

        response.sendRedirect(logoutUrl);
    }

    @Nonnull
    protected String getLogoutUrl(HttpServletRequest request, String token) {
        String logoutUrl = request.getParameter(OAuth2Utils.REDIRECT_URI);
        if (StringUtils.isBlank(logoutUrl) && token != null) {
            logoutUrl = loginRecordService.getLogoutUrl(token);
            if (logoutUrl != null) {
                loginRecordService.removeLogoutUrl(token);
            }
        }
        if (StringUtils.isBlank(logoutUrl)) {
            String referer = request.getHeader("Referer");
            if (referer != null) {
                logoutUrl = referer;
            } else {
                logoutUrl = securityProperties.getLogin().getSuccessUrl();
            }
        }
        return logoutUrl;
    }

    protected TokenStore getTokenStore() {
        return tokenStore;
    }

    protected LoginRecordService getLoginRecordService() {
        return loginRecordService;
    }

    protected SecurityProperties getSecurityProperties() {
        return securityProperties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
