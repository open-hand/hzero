package org.hzero.core.endpoint;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 *
 * Http请求
 * T : http请求body的类型
 *
 * @author XCXCXCXCX
 * @date 2020/4/22 10:48 上午
 */
public interface HttpRequest<T> {

    String getSchema();

    String getUrl();

    HttpMethod getMethod();

    HttpEntity<byte[]> getHttpEntity();

    Class<T> getResponseClass();

    default String toString(Map<String, String> requestParams) {
        if (CollectionUtils.isEmpty(requestParams)) {
            return "";
        }
        StringBuilder builder = new StringBuilder("?");
        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            builder.append("&")
                    .append(entry.getKey())
                    .append("=")
                    .append(entry.getValue());
        }
        return builder.deleteCharAt(1).toString();
    }
}
