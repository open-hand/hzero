package org.hzero.sso.auth;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import org.hzero.sso.core.common.SsoAuthenticationFactory;
import org.hzero.sso.core.common.SsoAuthenticationProvider;
import org.hzero.sso.core.common.SsoAuthenticationRouter;
import org.hzero.sso.core.common.SsoServerLogoutHandler;

/**
 *
 * @author bojiangzhou 2020/08/19
 */
public class AuthAuthenticationFactory implements SsoAuthenticationFactory {

    @Autowired
    private AuthAuthenticationProvider authenticationProvider;
    @Autowired
    private AuthAuthenticationRouter authenticationRouter;
    @Autowired
    private AuthServerLogoutHandler logoutHandler;

    @Override
    public Set<String> supportiveSsoType() {
        return AuthAttributes.SSO_TYPE;
    }

    @Override
    public SsoAuthenticationRouter getAuthenticationRouter() {
        return authenticationRouter;
    }

    @Override
    public SsoAuthenticationProvider getAuthenticationProvider() {
        return authenticationProvider;
    }

    @Override
    public SsoServerLogoutHandler getLogoutHandler() {
        return logoutHandler;
    }
}
