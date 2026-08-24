package io.choerodon.resource.filter;

import java.util.Enumeration;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import org.hzero.core.variable.RequestVariableHolder;

/**
 * @author dongfan117@gmail.com
 */
public class JwtTokenExtractor implements TokenExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenExtractor.class);

    @Override
    public Authentication extract(HttpServletRequest request) {
        String tokenValue = JwtTokenExtractor.extractToken(request);
        if (tokenValue != null) {
            return new PreAuthenticatedAuthenticationToken(tokenValue, "");
        } else {
            return null;
        }
    }

    public static String extractToken(HttpServletRequest request) {
        String token = JwtTokenExtractor.extractHeaderToken(request);
        if (token == null) {
            LOGGER.debug("Token not found in headers. Trying request parameters.");
        }

        return token;
    }

    private static String extractHeaderToken(HttpServletRequest request) {
        Enumeration<?> headers = request.getHeaders(RequestVariableHolder.HEADER_JWT);

        String value = null;
        do {
            if (!headers.hasMoreElements()) {
                break;
            }

            value = (String) headers.nextElement();
        } while (!value.toLowerCase().startsWith(RequestVariableHolder.HEADER_BEARER.toLowerCase()));

        if (value == null) {
            value = (String) request.getAttribute(RequestVariableHolder.HEADER_JWT);
        }

        if (StringUtils.isBlank(value)) {
            return null;
        }

        String authHeaderValue = value.substring(RequestVariableHolder.HEADER_BEARER.length()).trim();
        request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE, value.substring(0, RequestVariableHolder.HEADER_BEARER.length()).trim());
        int commaIndex = authHeaderValue.indexOf(44);
        if (commaIndex > 0) {
            authHeaderValue = authHeaderValue.substring(0, commaIndex);
        }

        return authHeaderValue;
    }

    public static String extractToken(ServerHttpRequest request) {
        String token = JwtTokenExtractor.extractHeaderToken(request);
        if (token == null) {
            LOGGER.debug("Token not found in headers and request parameters.");
        }

        return token;
    }

    private static String extractHeaderToken(ServerHttpRequest request) {
        String returnVal = null;
        //headers
        List<String> values = request.getHeaders().get(RequestVariableHolder.HEADER_JWT);
        if (values != null){
            for (String value : values){
                if (value != null && value.toLowerCase().startsWith(RequestVariableHolder.HEADER_BEARER.toLowerCase())){
                    returnVal = value;
                    break;
                }
            }
        }

        if (returnVal == null){
            //query params
            values = request.getQueryParams().get(RequestVariableHolder.HEADER_JWT);
            if (values != null){
                for (String value : values){
                    if (value != null && value.toLowerCase().startsWith(RequestVariableHolder.HEADER_BEARER.toLowerCase())){
                        returnVal = value;
                        break;
                    }
                }
            }
        }

        if (StringUtils.isBlank(returnVal)) {
            return null;
        }

        String authHeaderValue = returnVal.substring(RequestVariableHolder.HEADER_BEARER.length()).trim();
        int commaIndex = authHeaderValue.indexOf(44);
        if (commaIndex > 0) {
            authHeaderValue = authHeaderValue.substring(0, commaIndex);
        }

        return authHeaderValue;
    }
}