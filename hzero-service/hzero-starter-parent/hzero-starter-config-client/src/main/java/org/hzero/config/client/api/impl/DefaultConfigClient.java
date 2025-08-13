package org.hzero.config.client.api.impl;

import com.alibaba.fastjson.JSON;
import io.choerodon.core.exception.CommonException;
import org.hzero.common.HZeroService;
import org.hzero.config.client.api.ConfigClient;
import org.hzero.core.endpoint.client.StringHttpTransporter;
import org.hzero.core.endpoint.request.StaticEndpoint;
import org.hzero.core.endpoint.request.StringStaticEndpointHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * hzero-config的客户端通用封装
 * @author XCXCXCXCX
 * @date 2019/9/26
 * @project hzero-starter-parent
 */
public class DefaultConfigClient implements ConfigClient, EnvironmentAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConfigClient.class);

    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private StringHttpTransporter httpTransporter;

    private String configServiceName;

    /**
     * hzero-config暂不支持环境隔离的配置管理，所以没有用到env字段
     * @param env
     * @param serviceName
     * @param version
     * @param fileType
     * @param content
     */
    @Override
    public void publishConfig(String env, String serviceName, String version, String fileType, String content) {
        List<ServiceInstance> instances = discoveryClient.getInstances(configServiceName);

        Map<String, String> body = new HashMap<>(8);
        body.put("serviceName", serviceName);
        body.put("label", version);
        body.put("fileType", fileType);
        body.put("content", content);
        String bodyJson = JSON.toJSONString(body);

        for (ServiceInstance instance : instances) {
            try {
                httpTransporter.transport(new StringStaticEndpointHttpRequest(instance, StaticEndpoint.CONFIG_SERVICE_PUBLISH, bodyJson.getBytes()));
                return;
            } catch (Throwable e) {
                LOGGER.error("publish config failed", e);
            }
        }
        throw new CommonException("publish config failed!");
    }

    @Override
    public void publishConfigKeyValue(String env, String serviceName, String version, String key, String value) {
        List<ServiceInstance> instances = discoveryClient.getInstances(configServiceName);


        Map<String, String> body = new HashMap<>(8);
        body.put("serviceName", serviceName);
        body.put("label", version);
        body.put("key", key);
        body.put("value", value);
        String bodyJson = JSON.toJSONString(body);

        for (ServiceInstance instance : instances) {
            try {
                httpTransporter.transport(new StringStaticEndpointHttpRequest(instance, StaticEndpoint.CONFIG_SERVICE_PUBLISH_KV, bodyJson.getBytes()));
                return;
            } catch (Throwable e) {
                LOGGER.error("publish config failed", e);
            }
        }
        throw new CommonException("publish config failed!");
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.configServiceName = environment.resolvePlaceholders(HZeroService.Config.NAME);
    }
}
