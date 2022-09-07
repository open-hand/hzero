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
 * @date 2020/4/22 10:50 上午
 */
public class EndpointRequest<T> implements HttpRequest<T> {

    private static final String ACTUATOR_CONTEXT_PATH = "/actuator";

    private final HttpHeaders headers = new HttpHeaders();

    {
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
    }

    private ServiceInstance instance;

    private EndpointKey endpointKey;

    private HttpMethod httpMethod;

    private Map<String, String> requestParams;

    private HttpEntity<byte[]> httpEntity;

    private Class<T> responseClass;

    public EndpointRequest(ServiceInstance instance,
                           EndpointKey endpointKey,
                           HttpMethod httpMethod,
                           Class<T> responseClass) {
        this(instance, endpointKey, httpMethod, null, null, responseClass);
    }

    public EndpointRequest(ServiceInstance instance,
                           EndpointKey endpointKey,
                           HttpMethod httpMethod,
                           Map<String, String> requestParams,
                           Class<T> responseClass) {
        this(instance, endpointKey, httpMethod, requestParams, null, responseClass);
    }

    public EndpointRequest(ServiceInstance instance,
                           EndpointKey endpointKey,
                           HttpMethod httpMethod,
                           byte[] requestBody,
                           Class<T> responseClass) {
        this(instance, endpointKey, httpMethod, null, requestBody, responseClass);
    }

    public EndpointRequest(ServiceInstance instance,
                           EndpointKey endpointKey,
                           HttpMethod httpMethod,
                           Map<String, String> requestParams,
                           byte[] requestBody,
                           Class<T> responseClass) {
        this.instance = instance;
        this.endpointKey = endpointKey;
        this.httpMethod = httpMethod;
        this.requestParams = requestParams;
        this.httpEntity = new HttpEntity<>(requestBody, headers);
        this.responseClass = responseClass;
    }

    @Override
    public String getSchema() {
        return "http://";
    }

    @Override
    public String getUrl() {
        return instance.getHost() + ":"
                + ServiceInstanceUtils.getManagementPortFromMetadata(instance)
                + ACTUATOR_CONTEXT_PATH
                + ServiceInstanceUtils.getValueFromMetadata(instance, endpointKey.getKey())
                + toString(requestParams);
    }

    @Override
    public HttpMethod getMethod() {
        return httpMethod;
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
