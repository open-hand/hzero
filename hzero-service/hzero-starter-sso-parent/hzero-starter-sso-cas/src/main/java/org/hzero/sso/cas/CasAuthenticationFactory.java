package org.hzero.sso.cas;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import org.hzero.sso.core.common.*;

/**
 * @author bojiangzhou 2020/08/18
 */
public class CasAuthenticationFactory implements SsoAuthenticationFactory {

    @Autowired
    private CasAuthenticationRouter authenticationRouter;
    @Autowired
    private CasAuthenticationProvider authenticationProvider;
    @Autowired
    private CasAuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    private CasServerLogoutHandler logoutHandler;

    @Override
    public Set<String> supportiveSsoType() {
        return CasAttributes.SSO_TYPE;
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
    public SsoAuthenticationSuccessHandler getAuthenticationSuccessHandler() {
        return authenticationSuccessHandler;
    }

    @Override
    public SsoServerLogoutHandler getLogoutHandler() {
        return logoutHandler;
    }

}
