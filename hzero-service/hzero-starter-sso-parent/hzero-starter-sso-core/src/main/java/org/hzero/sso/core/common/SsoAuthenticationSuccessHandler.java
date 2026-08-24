package org.hzero.sso.core.common;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import org.hzero.sso.core.constant.OAuthParameters;
import org.hzero.sso.core.constant.SsoAttributes;
import org.hzero.sso.core.domain.Domain;
import org.hzero.sso.core.support.SsoContextHolder;

/**
 * 认证成功处理器
 *
 * @author bojiangzhou 2020/08/18
 */
public class SsoAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // 如果 session 中没有缓存登出地址，则将 domain 中的登出地址缓存进去
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(SsoAttributes.SESSION_ATTRIBUTE_LOGOUT_URL) == null) {
            Domain domain = SsoContextHolder.getDomain();
            String logoutUrl;
            if (domain != null && (logoutUrl = domain.getSsoLogoutUrl()) != null) {
                if (logoutUrl.indexOf("?") > 0) {
                    logoutUrl = logoutUrl + "&" + OAuthParameters.REDIRECT_URI + "=" + domain.getDomainUrl();
                } else {
                    logoutUrl = logoutUrl + "?" + OAuthParameters.REDIRECT_URI + "=" + domain.getDomainUrl();
                }
                session.setAttribute(SsoAttributes.SESSION_ATTRIBUTE_LOGOUT_URL, logoutUrl);
            }
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

}
