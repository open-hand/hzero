package org.hzero.sso.core.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.hzero.sso.core.domain.Domain;
import org.hzero.sso.core.security.SsoAuthenticationToken;
import org.hzero.sso.core.service.SsoUserDetailsService;
import org.hzero.sso.core.support.SsoContextHolder;

/**
 * @author bojiangzhou 2020/08/18
 */
public abstract class SsoAuthenticationProviderAdapter implements SsoAuthenticationProvider {

    protected final SsoUserDetailsService userDetailsService;
    protected AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

    public SsoAuthenticationProviderAdapter(SsoUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * 认证SSO请求，返回已认证的 Authentication
     */
    @Override
    public Authentication authenticate(final HttpServletRequest request, final HttpServletResponse response) {
        Domain domain = SsoContextHolder.getNonNullDomain();

        Authentication unauthenticated = extractAuthentication(request, response, domain);

        buildAuthenticationDetails(request, unauthenticated);

        Authentication authenticated = attemptAuthentication(request, response, unauthenticated, domain);

        if (authenticated instanceof SsoAuthenticationToken) {
            SsoAuthenticationToken authRequest = (SsoAuthenticationToken) authenticated;
            if (authRequest.getDetails() == null) {
                authRequest.setDetails(unauthenticated.getDetails());
            }
        }

        return authenticated;
    }

    /**
     * 解析SSO回调请求，封装 SsoAuthenticationToken
     */
    protected abstract Authentication extractAuthentication(final HttpServletRequest request, final HttpServletResponse response, final Domain domain) throws AuthenticationException;

    /**
     * 认证 Authentication
     */
    protected  Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response,
                                                    final Authentication authentication, final Domain domain) throws AuthenticationException {
        String username = authentication.getName();

        UserDetails user = getUserDetailsService().retrieveUser(username, domain.getTenants());

        return new SsoAuthenticationToken(user, authentication.getCredentials(), user.getAuthorities());
    }

    /**
     * 设置 details
     */
    protected void buildAuthenticationDetails(final HttpServletRequest request, Authentication authentication) {
        if (authentication instanceof SsoAuthenticationToken) {
            SsoAuthenticationToken authRequest = (SsoAuthenticationToken) authentication;
            authRequest.setDetails(getAuthenticationDetailsSource().buildDetails(request));
        }
    }

    protected final SsoUserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public final AuthenticationDetailsSource<HttpServletRequest, ?> getAuthenticationDetailsSource() {
        return authenticationDetailsSource;
    }

    public void setAuthenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        this.authenticationDetailsSource = authenticationDetailsSource;
    }
}
