package org.hzero.sso.core.standard;

import javax.servlet.http.HttpServletRequest;

import org.hzero.sso.core.common.SsoServerLogoutHandler;

/**
 *
 * @author bojiangzhou 2020/08/19
 */
public class StandardServerLogoutHandler implements SsoServerLogoutHandler {

    @Override
    public boolean isLogoutRequest(HttpServletRequest request) {
        return false;
    }

}
