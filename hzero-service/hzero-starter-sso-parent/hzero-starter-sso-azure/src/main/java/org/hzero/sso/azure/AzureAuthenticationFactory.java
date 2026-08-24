package org.hzero.sso.azure;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import org.hzero.sso.core.common.SsoAuthenticationFactory;
import org.hzero.sso.core.common.SsoAuthenticationProvider;
import org.hzero.sso.core.common.SsoAuthenticationRouter;

/**
 * @author bojiangzhou 2020/08/19
 */
public class AzureAuthenticationFactory implements SsoAuthenticationFactory {

    @Autowired
    private AzureAuthenticationProvider authenticationProvider;
    @Autowired
    private AzureAuthenticationRouter authenticationRouter;

    @Override
    public Set<String> supportiveSsoType() {
        return AzureAttributes.SSO_TYPE;
    }

    @Override
    public SsoAuthenticationRouter getAuthenticationRouter() {
        return authenticationRouter;
    }

    @Override
    public SsoAuthenticationProvider getAuthenticationProvider() {
        return authenticationProvider;
    }

}
