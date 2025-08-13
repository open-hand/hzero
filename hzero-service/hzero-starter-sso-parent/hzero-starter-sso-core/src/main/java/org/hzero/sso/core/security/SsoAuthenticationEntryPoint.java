package org.hzero.sso.core.security;

import java.io.IOException;
import javax.annotation.Nullable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import org.hzero.core.util.DomainUtils;
import org.hzero.sso.core.common.SsoAuthenticationRouter;
import org.hzero.sso.core.configuration.SsoPropertyService;
import org.hzero.sso.core.constant.OAuthParameters;
import org.hzero.sso.core.domain.Domain;
import org.hzero.sso.core.domain.DomainRepository;
import org.hzero.sso.core.support.SsoAuthenticationLocator;
import org.hzero.sso.core.support.SsoAuthenticationService;
import org.hzero.sso.core.support.SsoContextHolder;

/**
 * SSO 认证端点跳转入口
 *
 * @author bojiangzhou 2020/08/12
 */
public class SsoAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(SsoAuthenticationEntryPoint.class);

    private static final String AUTHORIZE_URI = "/oauth/authorize";

    private final SsoAuthenticationLocator authenticationLocator;
    private final SsoPropertyService propertyService;
    private final DomainRepository domainRepository;

    public SsoAuthenticationEntryPoint(SsoAuthenticationLocator authenticationLocator,
                                       SsoPropertyService propertyService,
                                       DomainRepository domainRepository) {
        this.authenticationLocator = authenticationLocator;
        this.propertyService = propertyService;
        this.domainRepository = domainRepository;
    }


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Domain domain = getSsoDomain(request);
        if (domain != null) {
            SsoContextHolder.setDomain(domain);
        } else {
            SsoContextHolder.clear(request);
        }

        SsoAuthenticationService authenticationService = authenticationLocator.getAuthenticationService();

        SsoAuthenticationRouter router = authenticationService.getAuthenticationRouter();

        router.commence(request, response, authException);
    }

    @Nullable
    protected Domain getSsoDomain(final HttpServletRequest request) {
        // 有些需求不需要跳转到单点登录页面，直接跳到标准登录页面
        String disable = request.getParameter(propertyService.getDisableSsoParameter());
        if (StringUtils.isNotBlank(disable) && !("0".equals(disable) || "false".equals(disable))) {
            return null;
        }

        boolean authorize = request.getRequestURI().endsWith(AUTHORIZE_URI);
        if (!authorize) {
            return null;
        }

        String redirectUri = request.getParameter(OAuthParameters.REDIRECT_URI);
        String host = DomainUtils.getDomain(redirectUri);

        Domain domain = domainRepository.selectByHost(host);

        LOGGER.debug("sso login request from [{}], host: [{}], domain: [{}]", redirectUri, host, domain);

        return domain;
    }

}
