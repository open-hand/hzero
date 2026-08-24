package org.hzero.sso.cas;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import org.hzero.sso.core.common.SsoAuthenticationSuccessHandler;
import org.hzero.sso.core.constant.SsoAttributes;
import org.hzero.sso.core.domain.Domain;
import org.hzero.sso.core.support.SsoContextHolder;

/**
 *
 * @author bojiangzhou 2020/08/24
 */
public class CasAuthenticationSuccessHandler extends SsoAuthenticationSuccessHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CasAuthenticationSuccessHandler.class);

    private final CasServiceHelper casServiceHelper;

    public CasAuthenticationSuccessHandler(CasServiceHelper casServiceHelper) {
        this.casServiceHelper = casServiceHelper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        String ticket = authentication.getCredentials().toString();
        // ticket 放到 session 中
        session.setAttribute(CasAttributes.ATTRIBUTE_CAS_TICKET, ticket);
        // 单点退出地址放到 session 中
        Domain domain = SsoContextHolder.getNonNullDomain();
        String logoutRedirectUrl = casServiceHelper.createLogoutRedirectUrl(request, response, domain);
        session.setAttribute(SsoAttributes.SESSION_ATTRIBUTE_LOGOUT_URL, logoutRedirectUrl);

        LOGGER.debug("cas record session attribute, ticket: [{}], logoutRedirectUrl: [{}]", ticket, logoutRedirectUrl);

        super.onAuthenticationSuccess(request, response, authentication);
    }


}
