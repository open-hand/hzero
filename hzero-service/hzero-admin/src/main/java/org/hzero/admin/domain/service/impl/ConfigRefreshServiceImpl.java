package org.hzero.admin.domain.service.impl;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.hzero.admin.domain.service.ConfigRefreshService;
import org.hzero.admin.domain.vo.ConfigParam;
import org.hzero.common.HZeroService;
import org.hzero.core.endpoint.client.StringHttpTransporter;
import org.hzero.core.endpoint.request.StaticEndpoint;
import org.hzero.core.endpoint.request.StringStaticEndpointHttpRequest;
import org.hzero.core.util.ServiceInstanceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * 配置刷新操作
 *
 * @author wuguokai
 */
@Component
public class ConfigRefreshServiceImpl implements ConfigRefreshService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigRefreshServiceImpl.class);

    private final ExecutorService asyncExecutor = new ScheduledThreadPoolExecutor(4,
            new BasicThreadFactory.Builder().namingPattern("refresh-config-pool-%d").daemon(true).build());

    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private StringHttpTransporter httpTransporter;

    @Async("async-context-refresh-notifier")
    @Override
    public void notifyServiceRefresh(List<ConfigParam> params) {
        for (ConfigParam param : params) {
            notifyServiceRefresh(param);
        }
    }

    @Async("async-context-refresh-notifier")
    @Override
    public void notifyServiceRefresh(ConfigParam param) {

        String service = StringUtils.lowerCase(param.getServiceCode());

        try {
            List<ServiceInstance> instances = discoveryClient.getInstances(service);
            if (instances.isEmpty()) {
                LOGGER.warn("Notify service[serviceCode={}] refresh route failed, this service is not UP", service);
                throw new CommonException("service.fresh.instance.not-up", service);
            }

            // 通知
            for (ServiceInstance noticeInstance : instances) {
                if (param.getServiceVersionCode() != null && param.getServiceVersionCode()
                        .equals(ServiceInstanceUtils.getVersionFromMetadata(noticeInstance))){
                    doNotify(noticeInstance);
                    LOGGER.info("Notify service: {} refresh config success", service);
                } else if (param.getServiceVersionCode() == null){
                    doNotify(noticeInstance);
                    LOGGER.info("Notify service: {} refresh config success", service);
                }
            }

        } catch (Exception e) {
            LOGGER.warn("Notify service: {} refresh config failed", service, e);
        }
    }

    private void doNotify(ServiceInstance noticeInstance){
        try {
            httpTransporter.transport(new StringStaticEndpointHttpRequest(noticeInstance, StaticEndpoint.SERVICE_REFRESH));
        } catch (RestClientException e) {
            LOGGER.error("refresh service config failed [serviceId=" + noticeInstance.getServiceId() + ",metadata={" + noticeInstance.getMetadata().toString() + "}]", e);
        }
    }

    @Async("async-gateway-notifier")
    @Override
    public void notifyGatewayRefresh() {
        notifyGatewayRefresh(Collections.emptyMap());
    }

    @Async("async-gateway-notifier")
    @Override
    public void notifyGatewayRefresh(Map<String, String> tags) {

        //spring-cloud-gateway通过/actuator/gateway/refresh来刷新路由
        String gatewayName = HZeroService.getRealName(HZeroService.Gateway.NAME);
        List<ServiceInstance> instances = discoveryClient.getInstances(gatewayName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        for (ServiceInstance instance : instances) {
            if(ServiceInstanceUtils.matchTags(instance, tags)) {
                try {
                    httpTransporter.transport(new StringStaticEndpointHttpRequest(instance, StaticEndpoint.GATEWAY_ROUTES_REFRESH));
                } catch (RestClientException e) {
                    throw new CommonException("notify gateway refresh routes failed.", e);
                }
            }
        }

    }

    @Override
    public void notifyGatewayRefresh(List<ConfigParam> params) {
        notifyServiceRefresh(params);
    }

}
