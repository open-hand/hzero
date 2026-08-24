package org.hzero.core.endpoint.request;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.HttpMethod;

import java.util.Map;

/**
 * 以String类型返回的EndpointRequest
 *
 * @author XCXCXCXCX
 * @date 2020/4/22 4:00 下午
 */
public class StringEndpointRequest extends EndpointRequest<String> {

    public StringEndpointRequest(ServiceInstance instance, EndpointKey endpointKey, HttpMethod httpMethod) {
        super(instance, endpointKey, httpMethod, String.class);
    }

    public StringEndpointRequest(ServiceInstance instance, EndpointKey endpointKey, HttpMethod httpMethod, Map<String, String> requestParams) {
        super(instance, endpointKey, httpMethod, requestParams, null, String.class);
    }

    public StringEndpointRequest(ServiceInstance instance, EndpointKey endpointKey, HttpMethod httpMethod, byte[] requestBody) {
        super(instance, endpointKey, httpMethod, null, requestBody, String.class);
    }

    public StringEndpointRequest(ServiceInstance instance, EndpointKey endpointKey, HttpMethod httpMethod, Map<String, String> requestParams, byte[] requestBody) {
        super(instance, endpointKey, httpMethod, requestParams, requestBody, String.class);
    }

}
