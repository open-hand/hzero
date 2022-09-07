package org.hzero.sso.core.security;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import org.hzero.sso.core.common.SsoExtendedFilter;
import org.hzero.sso.core.common.SsoServerLogoutHandler;
import org.hzero.sso.core.constant.SsoAttributes;
import org.hzero.sso.core.exception.SsoLoginExceptions;
import org.hzero.sso.core.exception.SsoServiceException;
import org.hzero.sso.core.service.SsoLogoutUrlRecordService;
import org.hzero.sso.core.service.SsoTokenClearService;
import org.hzero.sso.core.support.CompatibleService;
import org.hzero.sso.core.support.SsoAuthenticationLocator;
import org.hzero.sso.core.support.SsoAuthenticationService;
import org.hzero.sso.core.support.SsoContextHolder;

/**
 * SSO 入口
 *
 * @author bojiangzhou 2020/08/17
 */
public class SsoEntryFilter implements Filter {

    // 放到 RequestContextFilter 后面
    public static final int ORDER = FilterRegistrationBean.REQUEST_WRAPPER_FILTER_MAX_ORDER - 100;

    private static final Logger LOGGER = LoggerFactory.getLogger(SsoEntryFilter.class);

    private static final String DEFAULT_FILTER_PROCESSES_URL = SsoAttributes.SSO_DEFAULT_PROCESS_URI;

    private String processesUrlPrefix;
    private RequestMatcher ssoRequestMatcher;
    protected final SsoAuthenticationLocator authenticationLocator;
    protected final SsoTokenClearService tokenClearService;
    protected final SsoLogoutUrlRecordService logoutUrlRecordService;


    public SsoEntryFilter(SsoAuthenticationLocator authenticationLocator,
                          SsoTokenClearService tokenClearService,
                          SsoLogoutUrlRecordService logoutUrlRecordService) {
        this.tokenClearService = tokenClearService;
        this.logoutUrlRecordService = logoutUrlRecordService;
        ssoRequestMatcher = new AntPathRequestMatcher(DEFAULT_FILTER_PROCESSES_URL);
        this.processesUrlPrefix = DEFAULT_FILTER_PROCESSES_URL.replace("/**", "");
        this.authenticationLocator = authenticationLocator;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        boolean requires = ssoRequestMatcher.matches(request);
        boolean compatible = false;
        if (!requires) {
            compatible = CompatibleService.requiresAuthentication(request, response);
            if (!compatible) {
                chain.doFilter(request, response);
                return;
            }
        }

        String ssoType;
        if (compatible) {
            ssoType = CompatibleService.extractSsoType(request);
        } else {
            ssoType = extractSsoType(request);
        }

        if (ssoType == null || !authenticationLocator.registeredSsoTypes().contains(ssoType)) {
            throw new SsoServiceException(SsoLoginExceptions.SSO_NOT_SUPPORTED.value());
        }
        SsoContextHolder.setSsoType(ssoType);

        SsoAuthenticationService authenticationService = authenticationLocator.getAuthenticationService();
        SsoServerLogoutHandler logoutHandler = authenticationService.getLogoutHandler();
        SsoExtendedFilter extendedFilter;

        if (logoutHandler.isLogoutRequest(request)) {
            LOGGER.debug("sso logout request");
            String token = logoutHandler.handleLogoutRequest(request, response);
            if (token != null) {
                tokenClearService.clearTokenOnLogout(request, response, token);
                logoutUrlRecordService.clearLogoutUrl(token);
            }
        } else if ((extendedFilter = authenticationService.getExtendedFilter()) != null && extendedFilter.requiresFilter(request)) {
            extendedFilter.doFilter(servletRequest, servletResponse, chain);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }

    protected String extractSsoType(HttpServletRequest request) {
        String uri = request.getRequestURI();
        uri = uri.substring(request.getContextPath().length());
        if (!uri.startsWith(processesUrlPrefix)) {
            return null;
        }
        uri = uri.substring(processesUrlPrefix.length());
        if (uri.startsWith("/")) {
            String ssoType = StringUtils.split(uri, "/")[0];
            return ssoType;
        }
        throw new IllegalStateException("invalid sso request, sso request pattern should like '/sso/cas' ");
    }

    public void setFilterProcessesUrl(String filterProcessesUrl) {
        this.ssoRequestMatcher = new AntPathRequestMatcher(filterProcessesUrl);
        this.processesUrlPrefix = filterProcessesUrl.replace("/**", "");
    }

}
