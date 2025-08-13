package org.hzero.sso.saml;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hzero.sso.core.common.SsoServerLogoutHandler;

/**
 *
 * @author bojiangzhou 2020/08/30
 */
public class SamlServerLogoutHandler implements SsoServerLogoutHandler {

    private static final String SAML_LOGOUT_URI = "/saml/SingleLogout";

    @Override
    public boolean isLogoutRequest(HttpServletRequest request) {
        return request.getRequestURI().endsWith(SAML_LOGOUT_URI);
    }

    @Override
    public String handleLogoutRequest(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

}
