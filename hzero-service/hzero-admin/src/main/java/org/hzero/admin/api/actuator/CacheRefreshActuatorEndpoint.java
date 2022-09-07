package org.hzero.admin.api.actuator;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import org.hzero.admin.config.ConfigProperties;
import org.hzero.core.endpoint.client.StringHttpTransporter;
import org.hzero.core.endpoint.request.StaticEndpoint;
import org.hzero.core.endpoint.request.StaticEndpointHttpRequest;
import org.hzero.core.util.AsyncTask;
import org.hzero.core.util.CommonExecutor;
import org.hzero.core.util.Results;

/**
 * 缓存刷新接口
 *
 * @author bojiangzhou
 */
@Endpoint(id = "refresh-cache")
public class CacheRefreshActuatorEndpoint {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DiscoveryClient discoveryClient;
    private final ConfigProperties configProperties;
    private final StringHttpTransporter httpTransporter;

    static final String STATUS_NOT_FOUND = "NOT_FOUND";
    static final String STATUS_ERROR = "ERROR";
    static final String STATUS_SUCCESS = "SUCCESS";

    public CacheRefreshActuatorEndpoint(DiscoveryClient discoveryClient,
                                        ConfigProperties configProperties) {
        this.discoveryClient = discoveryClient;
        this.configProperties = configProperties;
        this.httpTransporter = new StringHttpTransporter();
        httpTransporter.configure(restTemplate -> {
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setConnectTimeout(3000);
            requestFactory.setReadTimeout(120000);
            restTemplate.setRequestFactory(requestFactory);
        });
    }

    /**
     * POST 接口调用，且类型必须为 application/json，传入 json 格式参数
     * <pre>
     * {
     * 	"services": "hzero-platform,hzero-iam",
     * 	"token": "xxx"
     * }
     * </pre>
     *
     * @param services 服务名，必输可为空，多个用逗号隔开
     * @param token    访问令牌，必输
     * @return 刷新结果
     */
    @WriteOperation
    public ResponseEntity<Map<String, Object>> refreshServiceCache(String services, String token) {
        Map<String, Object> result = new HashMap<>();

        if (!checkToken(token)) {
            result.put("RequestError", "Your token is error");
            return Results.success(result);
        }

        List<String> refreshServiceSuccessList = new ArrayList<>();
        List<String> refreshServiceErrorList = new ArrayList<>();
        List<String> instanceNotFoundList = new ArrayList<>();

        Set<String> refreshServiceSet = new HashSet<>();
        if (StringUtils.isNoneBlank(services)) {
            refreshServiceSet.addAll(Arrays.asList(services.split(",")));
        } else {
            String[] configuredServices = configProperties.getRefreshCacheServices();
            if (configuredServices != null) {
                refreshServiceSet.addAll(Arrays.asList(configuredServices));
            }
        }

        if (refreshServiceSet.size() > 0) {
            List<AsyncTask<String>> taskList = toAsyncTask(refreshServiceSet);
            List<String> resultList = CommonExecutor.batchExecuteAsync(taskList, "RefreshServiceCache");
            for (String s : resultList) {
                String[] arr = s.split("#");
                String serviceName = arr[0];
                String status = arr[1];
                if (STATUS_NOT_FOUND.equals(status)) {
                    instanceNotFoundList.add(serviceName);
                } else if (STATUS_ERROR.equals(status)) {
                    refreshServiceErrorList.add(serviceName);
                } else if (STATUS_SUCCESS.equals(status)) {
                    refreshServiceSuccessList.add(serviceName);
                }
            }
        }

        result.put("refreshServiceSuccessList", refreshServiceSuccessList);
        result.put("refreshServiceErrorList", refreshServiceErrorList);
        result.put("instanceNotFoundList", instanceNotFoundList);

        return Results.success(result);
    }

    private boolean checkToken(String token) {
        return StringUtils.equals(configProperties.getManagementToken(), token);
    }

    private List<AsyncTask<String>> toAsyncTask(Set<String> services) {
        return services.stream().map(serviceName -> new AsyncTask<String>() {

            @Override
            public String taskName() {
                return "RefreshCache#" + serviceName;
            }

            @Override
            public String doExecute() {
                ServiceInstance instance = getServiceInstance(serviceName);
                String status;
                if (instance == null) {
                    status = STATUS_NOT_FOUND;
                } else {
                    try {
                        httpTransporter.transport(new StaticEndpointHttpRequest<>(instance, StaticEndpoint.REFRESH_SERVICE_CACHE, String.class));
                        status = STATUS_SUCCESS;
                    } catch (Exception e) {
                        logger.warn("Refresh service cache error", e);
                        status = STATUS_ERROR;
                    }
                }

                return serviceName + "#" + status;
            }
        }).collect(Collectors.toList());
    }

    private ServiceInstance getServiceInstance(String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        if (CollectionUtils.isNotEmpty(instances)) {
            return instances.get(RandomUtils.nextInt(0, instances.size()));
        }
        return null;
    }

    static class RefreshDTO {
        private String services;
        private String token;

        public String getServices() {
            return services;
        }

        public void setServices(String services) {
            this.services = services;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

}
