package org.hzero.sso.azure;

import static org.hzero.sso.core.constant.OAuthParameters.*;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.UnsupportedResponseTypeException;

import org.hzero.core.util.DomainUtils;
import org.hzero.sso.core.common.SsoAuthenticationRouter;
import org.hzero.sso.core.domain.Domain;
import org.hzero.sso.core.support.SsoContextHolder;

/**
 * @author bojiangzhou 2020/08/19
 */
public class AzureAuthenticationRouter implements SsoAuthenticationRouter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureAuthenticationRouter.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        Domain domain = SsoContextHolder.getNonNullDomain();

        String redirectUrl = buildAuthorizeUrl(request, domain);

        LOGGER.debug("sso auth request authorize, redirectUrl: [{}]", redirectUrl);

        response.sendRedirect(redirectUrl);
    }

    protected String buildAuthorizeUrl(final HttpServletRequest request, final Domain domain) {
        String redirectUrl = domain.getSsoLoginUrl();
        Map<String, String> params = DomainUtils.getQueryMap(redirectUrl);

        String responseType = params.get(RESPONSE_TYPE);
        if (StringUtils.isBlank(responseType)) {
            responseType = "code"; // 使用授权码模式
        }

        if (!"code".equals(responseType)) {
            throw new UnsupportedResponseTypeException("Unsupported response types: " + responseType);
        }

        String scope = params.get(SCOPE);
        if (StringUtils.isBlank(scope)) {
            scope = "default";
        }

        String redirectUri = params.get(REDIRECT_URI);
        if (StringUtils.isBlank(redirectUri)) {
            redirectUri = domain.getClientHostUrl();
        }

        String clientId = params.get(CLIENT_ID);
        if (StringUtils.isBlank(clientId)) {
            clientId = domain.getSsoClientId();
        }

        int index = redirectUrl.indexOf("?");
        if (index > 0) {
            redirectUrl = redirectUrl.substring(0, index);
        }

        StringBuilder url = new StringBuilder(redirectUrl).append("?")
                .append(RESPONSE_TYPE).append("=").append(responseType)
                .append("&").append(CLIENT_ID).append("=").append(clientId)
                .append("&").append(REDIRECT_URI).append("=").append(redirectUri)
                .append("&").append(SCOPE).append("=").append(scope)
                .append("&").append(STATE).append("=").append(domain.getHost());

        return url.toString();
    }
}
