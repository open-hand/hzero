package org.hzero.core.endpoint.request;

import org.springframework.cloud.client.ServiceInstance;

import java.util.Map;

/**
 * 以String类型返回的StaticEndpointHttpRequest
 *
 * @author XCXCXCXCX
 * @date 2020/4/23 2:03 下午
 */
public class StringStaticEndpointHttpRequest extends StaticEndpointHttpRequest<String> {

    public StringStaticEndpointHttpRequest(ServiceInstance instance, StaticEndpoint endpoint) {
        super(instance, endpoint, String.class);
    }

    public StringStaticEndpointHttpRequest(ServiceInstance instance, StaticEndpoint endpoint, Map<String, String> requestParams) {
        super(instance, endpoint, requestParams, null, String.class, false);
    }

    public StringStaticEndpointHttpRequest(ServiceInstance instance, StaticEndpoint endpoint, byte[] requestBody) {
        super(instance, endpoint, requestBody, String.class);
    }

    public StringStaticEndpointHttpRequest(ServiceInstance instance, StaticEndpoint endpoint, Map<String, String> requestParams, byte[] requestBody, boolean enableSsl) {
        super(instance, endpoint, requestParams, requestBody, String.class, enableSsl);
    }
}
