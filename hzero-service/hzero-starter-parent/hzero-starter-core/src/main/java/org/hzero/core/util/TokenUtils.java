package org.hzero.core.util;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ServerWebExchange;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Supplier;

import static org.hzero.core.base.TokenConstants.ACCESS_TOKENS;
import static org.hzero.core.variable.RequestVariableHolder.*;

/**
 * @author qingsheng.chen@hand-china.com
 */
public class TokenUtils {
    /**
     * @return 获取当前登陆客户端 token
     */
    public static String getToken() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        return getToken(request);
    }

    public static String getToken(HttpServletRequest request) {
        Supplier<String> getAccessToken = () -> {
            String accessToken = request.getHeader(HEADER_AUTH);
            if (StringUtils.hasText(accessToken)) {
                return accessToken;
            }

            for (String token : ACCESS_TOKENS) {
                // 从请求参数中获取
                accessToken = request.getParameter(token);
                if (StringUtils.hasText(accessToken)) {
                    return accessToken;
                }

                // 从请求属性中获取
                accessToken = (String) request.getAttribute(token);
                if (StringUtils.hasText(accessToken)) {
                    return accessToken;
                }
            }

            // 没找到
            return null;
        };

        // 处理结果
        return trimToken(getAccessToken.get());
    }

    public static String getToken(ServerWebExchange exchange) {
        Supplier<String> getAccessToken = () -> {
            HttpHeaders headers = exchange.getRequest().getHeaders();
            String accessToken = headers.getFirst(HEADER_AUTH);
            if (StringUtils.hasText(accessToken)) {
                return accessToken;
            }


            MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
            for (String token : ACCESS_TOKENS) {
                // 从请求参数中获取
                accessToken = queryParams.getFirst(token);
                if (StringUtils.hasText(accessToken)) {
                    return accessToken;
                }

                // 从请求属性中获取
                accessToken = exchange.getAttribute(token);
                if (StringUtils.hasText(accessToken)) {
                    return accessToken;
                }
            }

            // 没有获取到
            return null;
        };

        // 处理结果
        return trimToken(getAccessToken.get());
    }

    /**
     * 获取token，并去掉token的标识前缀和空格
     *
     * @param accessToken token
     * @return 整理后的token
     */
    private static String trimToken(String accessToken) {
        return StringUtils.startsWithIgnoreCase(accessToken, HEADER_BEARER) ?
                accessToken.substring((HEADER_BEARER).length()).trim() : accessToken;
    }
}
