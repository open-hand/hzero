package org.hzero.core.endpoint.client;

import org.hzero.core.common.Configurable;
import org.hzero.core.common.Configurer;
import org.hzero.core.endpoint.HttpRequest;
import org.hzero.core.endpoint.HttpTransporter;
import org.hzero.core.net.RequestHeaderCopyInterceptor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * 基础HttpTransporter
 * @author XCXCXCXCX
 * @date 2020/4/22 10:47 上午
 */
public class BaseHttpTransporter<T> implements HttpTransporter<T>, Configurable<RestTemplate> {

    private final RestTemplate restTemplate = new RestTemplate();
    {
        restTemplate.getInterceptors().add(new RequestHeaderCopyInterceptor());
    }

    @Override
    public T transport(HttpRequest<T> request) {
        try {
            ResponseEntity<T> responseEntity = restTemplate.exchange(request.getSchema() + request.getUrl(), request.getMethod(), request.getHttpEntity(), request.getResponseClass());
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            }
            throw new RestClientException("http transport failed, response code [" + responseEntity.getStatusCode() + "]");
        } catch (RuntimeException e) {
            // 捕获并封装拦截器异常
            throw new RestClientException("http transport failed, cause: " + e.getMessage(), e);
        }
    }

    @Override
    public void configure(Configurer<RestTemplate> configurer) {
        configurer.configure(restTemplate);
    }

}
