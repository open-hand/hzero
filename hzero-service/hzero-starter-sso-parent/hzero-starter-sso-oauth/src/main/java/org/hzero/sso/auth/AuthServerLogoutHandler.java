package org.hzero.sso.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hzero.sso.core.common.SsoServerLogoutHandler;

/**
 * @author bojiangzhou 2020/08/19
 */
public class AuthServerLogoutHandler implements SsoServerLogoutHandler {

    @Override
    public boolean isLogoutRequest(HttpServletRequest request) {
        return false;
    }

}
