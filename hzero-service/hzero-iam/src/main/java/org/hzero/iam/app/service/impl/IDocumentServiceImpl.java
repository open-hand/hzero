package org.hzero.iam.app.service.impl;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import io.choerodon.core.exception.CommonException;

import org.hzero.core.endpoint.client.StringHttpTransporter;
import org.hzero.core.endpoint.request.StaticEndpoint;
import org.hzero.core.endpoint.request.StringStaticEndpointHttpRequest;
import org.hzero.iam.api.dto.RegisterInstancePayload;
import org.hzero.iam.app.service.IDocumentService;
import org.hzero.iam.domain.service.impl.ParseServicePermissionImpl;

/**
 * Swagger 文档服务
 *
 * @author bojiangzhou 2018/12/12
 */
@Service
public class IDocumentServiceImpl implements IDocumentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IDocumentServiceImpl.class);

    private static final String SPLIT = ":";

    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private ParseServicePermissionImpl parseServicePermission;
    @Autowired
    private StringHttpTransporter httpTransporter;

    /**
     * serviceName:version
     * 避免相同的(且无法执行成功的)刷新任务在内存反复执行
     */
    private final Set<String> permissionRefreshTask = new ConcurrentSkipListSet<>();

    @Override
    public void refreshPermission(String serviceName, String metaVersion, Boolean cleanPermission) {
        ServiceInstance instance = getInstanceByNameAndVersion(serviceName, metaVersion);
        if (instance == null) {
            LOGGER.warn("service instance not running, serviceName=[{}], metaVersion=[{}]", serviceName, metaVersion);
            throw new CommonException("hiam.warn.permission.instanceNotRunning");
        }
        parseServicePermission.parser(serviceName, instance, cleanPermission);
    }

    @Async("IamCommonAsyncTaskExecutor")
    @Override
    public void refreshPermissionAsync(String serviceName, String metaVersion, Boolean cleanPermission) {
        String id = buildId(serviceName, metaVersion);
        if (permissionRefreshTask.add(id)) {
            retry(() -> {
                refreshPermission(serviceName, metaVersion, cleanPermission);
                permissionRefreshTask.remove(id);
            }, 5, 3000);
        }
    }

    private String buildId(String serviceName, String metaVersion) {
        return serviceName + SPLIT + metaVersion;
    }

    private void retry(Runnable runnable, int times, long sleep) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(sleep);
                runnable.run();
                return;
            } catch (Throwable e) {
                LOGGER.error("execute failed, continue to retry...", e);
            }
            if (times-- == 0) {
                return;
            }
        }
    }

    private ServiceInstance getInstanceByNameAndVersion(String serviceName, String metaVersion) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        for (ServiceInstance instance : instances) {
            String mdVersion = instance.getMetadata().get(METADATA_VERSION);
            if (StringUtils.isEmpty(mdVersion)) {
                mdVersion = NULL_VERSION;
            }
            if (metaVersion.equals(mdVersion)) {
                return instance;
            }
        }
        if (NULL_VERSION.equalsIgnoreCase(metaVersion)) {
            LOGGER.info("The first service instance is used directly without entering the service version.");
            return instances.stream().findFirst().orElse(null);
        }
        return null;
    }

    private String fetch(ServiceInstance instance) {
        LOGGER.info("service: {} metadata : {}", instance.getServiceId(), instance.getMetadata());
        try {
            return httpTransporter.transport(new StringStaticEndpointHttpRequest(instance, StaticEndpoint.CHOERODON_API_DOCS));
        } catch (RestClientException e) {
            LOGGER.error("fetch error, ex={}", e.getMessage());
            throw new RemoteAccessException("fetch failed, instance: " + instance.getServiceId() + ", ex=" + e.getMessage());
        }
    }

}
