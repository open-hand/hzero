package org.hzero.sso.core.standard;

import java.io.IOException;
import java.util.function.Function;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;

import org.hzero.sso.core.common.SsoAuthenticationRouter;
import org.hzero.sso.core.configuration.SsoPropertyService;

/**
 * 非 SSO 请求路由器
 *
 * @author bojiangzhou 2020/08/19
 */
public class StandardAuthenticationRouter implements SsoAuthenticationRouter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected final SsoPropertyService ssoPropertyService;
    protected final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public StandardAuthenticationRouter(SsoPropertyService ssoPropertyService) {
        this.ssoPropertyService = ssoPropertyService;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        String targetUrl = null;
        if (StringUtils.isNotBlank(ssoPropertyService.getBaseUrl())) {
            targetUrl = ssoPropertyService.getBaseUrl() + ssoPropertyService.getLoginPage();
        } else {
            Function<HttpServletRequest, String> fun = ssoPropertyService.getDynamicBaseUrlFunction();
            String baseUrl;
            if (fun != null && (baseUrl = fun.apply(request)) != null) {
                targetUrl = baseUrl + ssoPropertyService.getLoginPage();
            }
        }

        if (StringUtils.isBlank(targetUrl)) {
            throw new IllegalStateException("Standard authentication target url invalid");
        } else {
            logger.debug("Standard authentication url is: {}", targetUrl);
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

}
