package org.hzero.sso.core.standard;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.hzero.sso.core.common.SsoAuthenticationProviderAdapter;
import org.hzero.sso.core.domain.Domain;
import org.hzero.sso.core.service.SsoUserDetailsService;

/**
 *
 * @author bojiangzhou 2020/08/18
 */
public class StandardAuthenticationProvider extends SsoAuthenticationProviderAdapter {

    public StandardAuthenticationProvider(SsoUserDetailsService userDetailsService) {
        super(userDetailsService);
    }

    @Override
    protected Authentication extractAuthentication(final HttpServletRequest request, final HttpServletResponse response, final Domain domain) throws AuthenticationException {
        return null;
    }

}
