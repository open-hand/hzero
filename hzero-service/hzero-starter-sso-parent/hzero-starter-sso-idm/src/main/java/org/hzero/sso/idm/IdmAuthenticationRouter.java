package org.hzero.sso.idm;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;

import org.hzero.sso.core.common.SsoAuthenticationRouter;
import org.hzero.sso.core.constant.OAuthParameters;
import org.hzero.sso.core.domain.Domain;
import org.hzero.sso.core.support.SsoContextHolder;

/**
 * @author bojiangzhou 2020/08/20
 */
public class IdmAuthenticationRouter implements SsoAuthenticationRouter {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Domain domain = SsoContextHolder.getNonNullDomain();

        // IDM 直接跳转到认证地址
        String redirectUrl = domain.getClientHostUrl();

        if (!redirectUrl.contains(OAuthParameters.STATE)) {
            redirectUrl = redirectUrl + "?" + OAuthParameters.STATE + "=" + domain.getHost();
        }

        response.sendRedirect(redirectUrl);
    }
}
