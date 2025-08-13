package org.hzero.sso.saml;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import org.hzero.sso.core.common.*;
import org.hzero.sso.saml.metadata.SamlMetadataExtendedFilter;

/**
 *
 * @author bojiangzhou 2020/08/21
 */
public class SamlAuthenticationFactory implements SsoAuthenticationFactory {

    @Autowired
    private SamlAuthenticationRouter samlAuthenticationRouter;
    @Autowired
    private SamlAuthenticationProvider samlAuthenticationProvider;
    @Autowired
    private SamlMetadataExtendedFilter metadataExtendedFilter;
    @Autowired
    private SamlServerLogoutHandler serverLogoutHandler;

    @Override
    public Set<String> supportiveSsoType() {
        return SamlAttributes.SSO_TYPE;
    }

    @Override
    public SsoAuthenticationRouter getAuthenticationRouter() {
        return samlAuthenticationRouter;
    }

    @Override
    public SsoAuthenticationProvider getAuthenticationProvider() {
        return samlAuthenticationProvider;
    }

    @Override
    public SsoServerLogoutHandler getLogoutHandler() {
        return serverLogoutHandler;
    }

    @Override
    public SsoExtendedFilter getExtendedFilter() {
        return metadataExtendedFilter;
    }
}
