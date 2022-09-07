package org.hzero.sso.idm;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;

import org.hzero.sso.core.common.SsoAuthenticationSuccessHandler;
import org.hzero.sso.core.domain.Domain;
import org.hzero.sso.core.support.SsoContextHolder;

/**
 *
 * @author bojiangzhou 2020/08/27
 */
public class IdmAuthenticationSuccessHandler extends SsoAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Domain domain = SsoContextHolder.getDomain();
        if (domain != null) {
            response.sendRedirect(domain.getDomainUrl());
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
