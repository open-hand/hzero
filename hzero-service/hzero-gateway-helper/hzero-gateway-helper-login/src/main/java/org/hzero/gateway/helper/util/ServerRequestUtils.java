package org.hzero.gateway.helper.util;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;

/**
 * @author XCXCXCXCX
 * @date 2019/9/4
 * @project hzero-gateway-helper
 */
public final class ServerRequestUtils {

    public static String resolveHeader(ServerHttpRequest request, String key){
        HttpHeaders headers = request.getHeaders();
        List<String> values = headers.get(key);
        if(values == null){
            return null;
        }
        return values.stream()
                .filter(value -> value != null && !value.isEmpty())
                .findFirst()
                .orElseGet(() -> null);
    }

    public static String resolveParam(ServerHttpRequest request, String key){
        MultiValueMap<String, String> params = request.getQueryParams();
        List<String> values = params.get(key);
        if(values == null){
            return null;
        }
        return values.stream()
                .filter(value -> value != null && !value.isEmpty())
                .findFirst()
                .orElseGet(() -> null);
    }

    public static String getHeaderValue(Object request, String key) {
        String value = null;
        if (request instanceof HttpServletRequest) {
            value = ((HttpServletRequest) request).getHeader(key);
            if (StringUtils.isBlank(value)) {
                value = ((HttpServletRequest) request).getParameter(key);
            }
        } else if (request instanceof ServerHttpRequest) {
            value = ServerRequestUtils.resolveHeader((ServerHttpRequest) request, key);
            if (StringUtils.isBlank(value)) {
                value = ServerRequestUtils.resolveParam((ServerHttpRequest) request, key);
            }
        }
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return value;
    }

}
