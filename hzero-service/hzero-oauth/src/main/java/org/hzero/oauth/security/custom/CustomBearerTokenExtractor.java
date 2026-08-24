package org.hzero.oauth.security.custom;

import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import org.hzero.core.base.TokenConstants;

/**
 * 自定义 BearerTokenExtractor，由于 BearerTokenExtractor 写死了请求头为 Authorization，因此重定义方法，改成取配置的请求头名称
 *
 * @author bojiangzhou
 */
public class CustomBearerTokenExtractor extends BearerTokenExtractor {

    @Override
    protected String extractHeaderToken(HttpServletRequest request) {
        // 根据 HEADER_AUTH 取请求头
        Enumeration<String> headers = request.getHeaders(TokenConstants.HEADER_AUTH);
        while (headers.hasMoreElements()) { // typically there is only one (most servers enforce that)
            String value = headers.nextElement();
            if ((value.toLowerCase().startsWith(OAuth2AccessToken.BEARER_TYPE.toLowerCase()))) {
                String authHeaderValue = value.substring(OAuth2AccessToken.BEARER_TYPE.length()).trim();
                // Add this here for the auth details later. Would be better to change the signature of this method.
                request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE,
                        value.substring(0, OAuth2AccessToken.BEARER_TYPE.length()).trim());
                int commaIndex = authHeaderValue.indexOf(',');
                if (commaIndex > 0) {
                    authHeaderValue = authHeaderValue.substring(0, commaIndex);
                }
                return authHeaderValue;
            }
        }

        return null;
    }

}
