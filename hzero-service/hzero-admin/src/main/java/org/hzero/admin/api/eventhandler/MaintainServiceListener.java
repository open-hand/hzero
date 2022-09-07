package org.hzero.admin.api.eventhandler;

import com.alibaba.fastjson.JSON;
import io.choerodon.core.exception.ExceptionResponse;
import org.hzero.admin.domain.repository.MaintainServiceRepository;
import org.hzero.core.endpoint.client.StringHttpTransporter;
import org.hzero.core.endpoint.request.EndpointKey;
import org.hzero.core.endpoint.request.StringEndpointRequest;
import org.hzero.register.event.event.InstanceAddedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;

import java.util.Map;
import java.util.Properties;

/**
 * @author XCXCXCXCX
 * @date 2020/6/2 9:47 上午
 */
@Component
public class MaintainServiceListener implements ApplicationListener<InstanceAddedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaintainServiceListener.class);

    @Autowired
    private StringHttpTransporter httpTransporter;

    @Autowired
    @Qualifier("redisMaintainServiceRepository")
    private MaintainServiceRepository maintainServiceRepository;

    public void registerListener(String serviceName, Properties properties) {
        maintainServiceRepository.put(serviceName, properties);
    }

    public void unregisterListener(String serviceName) {
        maintainServiceRepository.remove(serviceName);
    }

    @Override
    public void onApplicationEvent(InstanceAddedEvent event) {
        String serviceName = event.getServiceName();
        Properties properties = maintainServiceRepository.getConfig(serviceName);
        if (properties != null) {
            onListen(event.getServiceInstance(), properties);
        }
    }

    private void onListen(ServiceInstance instance, Properties properties) {
        String response = httpTransporter.transport(new StringEndpointRequest(instance, EndpointKey.MAINTAIN_ENDPOINT_KEY, HttpMethod.PUT, mapToBytes(properties)));
        /**
         * 解析响应信息
         */
        if (!StringUtils.isEmpty(response)) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseMap = JSON.parseObject(response, Map.class);
                if (responseMap.containsKey(ExceptionResponse.FILED_FAILED) && (Boolean) responseMap.get(ExceptionResponse.FILED_FAILED)) {
                    throw new RestClientException((String) responseMap.get(ExceptionResponse.FILED_MESSAGE));
                }
            } catch (Throwable e) {
                if (e instanceof RestClientException) {
                    throw e;
                }
                throw new RuntimeException("ResponseBody parse failed", e);
            }
        }
    }

    private byte[] mapToBytes(Properties properties) {
        return JSON.toJSONBytes(properties);
    }

}
