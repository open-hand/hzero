package org.hzero.core.util;

import static org.hzero.core.variable.RequestVariableHolder.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author qingsheng.chen@hand-china.com
 */
public class TokenUtils {
    private static final String TOKEN_PREFIX = HEADER_BEARER + " ";

    /**
     * @return 获取当前登陆客户端 token
     */
    public static String getToken() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String accessToken = requestAttributes.getRequest().getHeader(HEADER_AUTH);
        if (!StringUtils.hasText(accessToken)) {
            accessToken = requestAttributes.getRequest().getParameter(ACCESS_TOKEN);
        }
        if (StringUtils.startsWithIgnoreCase(accessToken, TOKEN_PREFIX)) {
            accessToken = accessToken.substring(TOKEN_PREFIX.length());
        }
        return accessToken;
    }

    public static String getToken(HttpServletRequest request) {
        String accessToken = request.getHeader(HEADER_AUTH);
        if (!StringUtils.hasText(accessToken)) {
            accessToken = request.getParameter(ACCESS_TOKEN);
        }
        if (!StringUtils.hasText(accessToken)) {
            accessToken = (String) request.getAttribute(ACCESS_TOKEN);
        }
        if (StringUtils.startsWithIgnoreCase(accessToken, TOKEN_PREFIX)) {
            accessToken = accessToken.substring(TOKEN_PREFIX.length());
        }
        return accessToken;
    }

    public static String getToken(ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String accessToken = headers.getFirst(HEADER_AUTH);
        if (!StringUtils.hasText(accessToken)) {
            MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
            accessToken = queryParams.getFirst(ACCESS_TOKEN);
        }
        if (!StringUtils.hasText(accessToken)) {
            accessToken = exchange.getAttribute(ACCESS_TOKEN);
        }
        if (StringUtils.startsWithIgnoreCase(accessToken, TOKEN_PREFIX)) {
            accessToken = accessToken.substring((TOKEN_PREFIX + " ").length());
        }
        return accessToken;
    }
}
