package org.hzero.core.endpoint.request;

import org.hzero.core.endpoint.HttpRequest;
import org.hzero.core.util.ServiceInstanceUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.Map;

/**
 *
 * see ===> {@link MediaType.APPLICATION_JSON_UTF8}
 * requestBody 默认为json格式，且为utf8编码
 *
 * @author XCXCXCXCX
 * @date 2020/4/23 1:57 下午
 */
public class StaticEndpointHttpRequest<T> implements HttpRequest<T> {

    private final HttpHeaders headers = new HttpHeaders();

    {
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
    }

    private ServiceInstance instance;

    private StaticEndpoint endpoint;

    private Map<String, String> requestParams;

    private HttpEntity<byte[]> httpEntity;

    private Class<T> responseClass;

    private boolean enableSsl;

    public StaticEndpointHttpRequest(ServiceInstance instance, StaticEndpoint endpoint, Class<T> responseClass) {
        this(instance, endpoint, null, null, responseClass, false);
    }

    public StaticEndpointHttpRequest(ServiceInstance instance, StaticEndpoint endpoint, Map<String, String> requestParams, Class<T> responseClass) {
        this(instance, endpoint, requestParams, null, responseClass, false);
    }

    public StaticEndpointHttpRequest(ServiceInstance instance, StaticEndpoint endpoint, byte[] requestBody, Class<T> responseClass) {
        this(instance, endpoint, null, requestBody, responseClass, false);
    }

    public StaticEndpointHttpRequest(ServiceInstance instance, StaticEndpoint endpoint, Map<String, String> requestParams, byte[] requestBody, Class<T> responseClass, boolean enableSsl) {
        this.instance = instance;
        this.endpoint = endpoint;
        this.requestParams = requestParams;
        this.httpEntity = new HttpEntity<>(requestBody, headers);
        this.responseClass = responseClass;
        this.enableSsl = enableSsl;
    }

    @Override
    public String getSchema() {
        return enableSsl ? "https://" : "http://";
    }

    @Override
    public String getUrl() {
        return instance.getHost()
                + ":" + (endpoint.isUseManagementPort() ? ServiceInstanceUtils.getManagementPortFromMetadata(instance) : instance.getPort() + ServiceInstanceUtils.getContextFromMetadata(instance))
                + endpoint.getEndpoint()
                + toString(requestParams);
    }

    @Override
    public HttpMethod getMethod() {
        return endpoint.getMethod();
    }

    @Override
    public HttpEntity<byte[]> getHttpEntity() {
        return httpEntity;
    }

    @Override
    public Class<T> getResponseClass() {
        return responseClass;
    }
}
