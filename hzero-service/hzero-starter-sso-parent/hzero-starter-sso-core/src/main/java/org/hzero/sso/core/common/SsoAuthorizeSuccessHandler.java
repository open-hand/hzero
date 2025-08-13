package org.hzero.sso.core.common;

import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;

import org.hzero.sso.core.constant.SsoAttributes;
import org.hzero.sso.core.service.SsoLogoutUrlRecordService;
import org.hzero.sso.core.support.SsoContextHolder;

/**
 * 认证成功处理器（创建 Token 成功）
 *
 * @author bojiangzhou 2020/08/18
 */
public abstract class SsoAuthorizeSuccessHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SsoAuthorizeSuccessHandler.class);

    @Autowired(required = false)
    private SsoLogoutUrlRecordService logoutUrlRecordService;


    /**
     * 判断是否使用此处理器
     */
    public final boolean requiresHandle(final HttpServletRequest request) {
        String ssoType = SsoContextHolder.getSsoType();
        if (ssoType == null) {
            return false;
        }
        return supportiveSsoType().contains(ssoType);
    }

    /**
     * @return SSO 类型
     */
    protected abstract Set<String> supportiveSsoType();

    /**
     * 认证成功处理
     */
    public void onAuthorizeSuccessHandler(final HttpServletRequest request, final AuthorizationRequest authorizationRequest,
                                          final OAuth2AccessToken oAuth2AccessToken) {
        HttpSession session = request.getSession(false);
        Object logoutUrl;
        if (logoutUrlRecordService != null && session != null
                && (logoutUrl = session.getAttribute(SsoAttributes.SESSION_ATTRIBUTE_LOGOUT_URL)) != null) {
            String logoutRedirectUrl = (String) logoutUrl;
            String accessToken = oAuth2AccessToken.getValue();

            logoutUrlRecordService.recordLogoutUrl(accessToken, logoutRedirectUrl);

            session.removeAttribute(SsoAttributes.SESSION_ATTRIBUTE_LOGOUT_URL);

            LOGGER.debug("record sso logout redirect_url, access_token: [{}], logoutRedirectUrl: [{}]", accessToken, logoutRedirectUrl);
        }
    }

}
