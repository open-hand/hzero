package org.hzero.sso.core.security;

import java.io.IOException;
import javax.annotation.Nonnull;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import org.hzero.sso.core.constant.OAuthParameters;
import org.hzero.sso.core.constant.SsoAttributes;
import org.hzero.sso.core.domain.Domain;
import org.hzero.sso.core.domain.DomainRepository;
import org.hzero.sso.core.exception.SsoServiceException;
import org.hzero.sso.core.support.CompatibleService;
import org.hzero.sso.core.support.SsoAuthenticationLocator;
import org.hzero.sso.core.support.SsoAuthenticationService;
import org.hzero.sso.core.support.SsoContextHolder;

/**
 * 单点登录统一Filter
 *
 * @author bojiangzhou 2020/08/17
 */
public class SsoAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SsoAuthenticationFilter.class);

    private static final String DEFAULT_FILTER_PROCESSES_URL = SsoAttributes.SSO_DEFAULT_PROCESS_URI;

    private final DomainRepository domainRepository;
    private final SsoAuthenticationLocator authenticationLocator;

    public SsoAuthenticationFilter(DomainRepository domainRepository,
                                   SsoAuthenticationLocator authenticationLocator) {
        super(DEFAULT_FILTER_PROCESSES_URL);
        this.domainRepository = domainRepository;
        this.authenticationLocator = authenticationLocator;
    }

    @Override
    protected final boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        boolean requires = super.requiresAuthentication(request, response);
        if (!requires) {
            requires = CompatibleService.requiresAuthentication(request, response);
            if (!requires) {
                return false;
            }
        }

        Domain domain = SsoContextHolder.getDomain();
        if (domain == null) {
            domain = getSsoDomain(request);
            SsoContextHolder.setDomain(domain);
        }
        return true;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        SsoAuthenticationService authenticationService = authenticationLocator.getAuthenticationService();

        return authenticationService.getAuthenticationProvider().authenticate(request, response);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        LOGGER.debug("user authentication success");

        SecurityContextHolder.getContext().setAuthentication(authResult);

        SsoAuthenticationService authenticationService = authenticationLocator.getAuthenticationService();

        authenticationService.getAuthenticationSuccessHandler().onAuthenticationSuccess(request, response, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        LOGGER.info("user authentication failure, failed message: {}", failed.getMessage());
        LOGGER.debug("user authentication failure", failed);

        SecurityContextHolder.clearContext();

        SsoAuthenticationService authenticationService = authenticationLocator.getAuthenticationService();

        authenticationService.getAuthenticationFailureHandler().onAuthenticationFailure(request, response, failed);
    }

    @Nonnull
    protected Domain getSsoDomain(final HttpServletRequest request) {
        String host = request.getParameter(OAuthParameters.STATE);

        if (StringUtils.isBlank(host)) {
            throw new SsoServiceException("sso callback request must contains [" + OAuthParameters.STATE + "] parameter");
        }

        Domain domain = domainRepository.selectByHost(host);

        if (domain == null) {
            throw new SsoServiceException("Not found domain for host [" + host + "]");
        }

        LOGGER.debug("sso callback request, host: [{}], domain: [{}]", host, domain);

        return domain;
    }

    @Override
    public void setFilterProcessesUrl(String filterProcessesUrl) {
        super.setFilterProcessesUrl(filterProcessesUrl);
    }

    @Override
    public void afterPropertiesSet() {
        //
    }
}
