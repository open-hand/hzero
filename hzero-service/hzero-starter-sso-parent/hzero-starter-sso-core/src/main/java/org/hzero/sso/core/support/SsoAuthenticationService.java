package org.hzero.sso.core.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.hzero.sso.core.common.*;

/**
 * @author bojiangzhou 2020/08/17
 */
public class SsoAuthenticationService implements SsoAuthenticationFactory {

    Map<String, Object> map = new HashMap<>();

    private static final String SSO_AUTHENTICATION_ROUTER = "SsoAuthenticationRouter";
    private static final String SSO_AUTHENTICATION_PROVIDER = "SsoAuthenticationProvider";
    private static final String SSO_LOGOUT_HANDLER = "SsoLogoutHandler";
    private static final String SSO_AUTHENTICATION_SUCCESS_HANDLER = "SsoAuthenticationSuccessHandler";
    private static final String SSO_AUTHENTICATION_FAILURE_HANDLER = "SsoAuthenticationFailureHandler";
    private static final String SSO_EXTENDED_FILTER = "SsoExtendedFilter";

    public SsoAuthenticationService(SsoAuthenticationFactory factory) {
        map.put(SSO_AUTHENTICATION_ROUTER, factory.getAuthenticationRouter());
        map.put(SSO_AUTHENTICATION_PROVIDER, factory.getAuthenticationProvider());
        map.put(SSO_LOGOUT_HANDLER, factory.getLogoutHandler());
        map.put(SSO_AUTHENTICATION_SUCCESS_HANDLER, factory.getAuthenticationSuccessHandler());
        map.put(SSO_AUTHENTICATION_FAILURE_HANDLER, factory.getAuthenticationFailureHandler());
        map.put(SSO_EXTENDED_FILTER, factory.getExtendedFilter());
    }

    @Override
    public Set<String> supportiveSsoType() {
        return Collections.emptySet();
    }

    @Override
    public SsoAuthenticationRouter getAuthenticationRouter() {
        return (SsoAuthenticationRouter) map.get(SSO_AUTHENTICATION_ROUTER);
    }

    @Override
    public SsoAuthenticationProvider getAuthenticationProvider() {
        return (SsoAuthenticationProvider) map.get(SSO_AUTHENTICATION_PROVIDER);
    }

    @Override
    public SsoServerLogoutHandler getLogoutHandler() {
        return (SsoServerLogoutHandler) map.get(SSO_LOGOUT_HANDLER);
    }

    @Override
    public SsoAuthenticationSuccessHandler getAuthenticationSuccessHandler() {
        return (SsoAuthenticationSuccessHandler) map.get(SSO_AUTHENTICATION_SUCCESS_HANDLER);
    }

    @Override
    public SsoAuthenticationFailureHandler getAuthenticationFailureHandler() {
        return (SsoAuthenticationFailureHandler) map.get(SSO_AUTHENTICATION_FAILURE_HANDLER);
    }

    @Override
    public SsoExtendedFilter getExtendedFilter() {
        return (SsoExtendedFilter) map.get(SSO_EXTENDED_FILTER);
    }
}
