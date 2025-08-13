package org.hzero.sso.idm;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import org.hzero.sso.core.common.SsoAuthenticationFactory;
import org.hzero.sso.core.common.SsoAuthenticationProvider;
import org.hzero.sso.core.common.SsoAuthenticationRouter;
import org.hzero.sso.core.common.SsoAuthenticationSuccessHandler;

/**
 *
 * @author bojiangzhou 2020/08/20
 */
public class IdmAuthenticationFactory implements SsoAuthenticationFactory {

    @Autowired
    private IdmAuthenticationProvider authenticationProvider;
    @Autowired
    private IdmAuthenticationRouter authenticationRouter;
    @Autowired
    private IdmAuthenticationSuccessHandler authenticationSuccessHandler;

    @Override
    public Set<String> supportiveSsoType() {
        return IdmAttributes.SSO_TYPE;
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
}
