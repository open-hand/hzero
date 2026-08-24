package org.hzero.admin.domain.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.core.swagger.ChoerodonRouteData;
import org.apache.commons.lang3.StringUtils;
import org.hzero.admin.app.service.ServiceRouteRefreshService;
import org.hzero.admin.config.ConfigProperties;
import org.hzero.admin.domain.entity.HService;
import org.hzero.admin.domain.entity.ServiceRoute;
import org.hzero.admin.domain.entity.ServiceVersion;
import org.hzero.admin.domain.repository.ServiceRepository;
import org.hzero.admin.domain.repository.ServiceRouteRepository;
import org.hzero.admin.domain.repository.ServiceVersionRepository;
import org.hzero.admin.domain.service.ParseRouteService;
import org.hzero.admin.infra.exception.ServiceSkipInitializationException;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.endpoint.client.StringHttpTransporter;
import org.hzero.core.endpoint.request.StaticEndpoint;
import org.hzero.core.endpoint.request.StringStaticEndpointHttpRequest;
import org.hzero.core.redis.RedisHelper;
import org.hzero.register.event.event.InstanceAddedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClientException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.hzero.core.util.ServiceInstanceUtils.METADATA_VERSION;
import static org.hzero.core.util.ServiceInstanceUtils.NULL_VERSION;
import static org.hzero.core.util.ServiceInstanceUtils.getVersionFromMetadata;

/**
 * @author bojiangzhou 2019/01/04
 */
@Component
public class ParseRouteServiceImpl implements ParseRouteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParseRouteServiceImpl.class);
    private static final String ROUTE_MAP_KEY = "hadm:routes";

    private Queue<FunctionX> taskQueue = new LinkedBlockingQueue<>();

    private ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1);

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private ServiceRouteRepository routeRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private ServiceRouteRefreshService routeRefreshService;
    @Autowired
    private ConfigProperties configProperties;
    @Autowired
    private ServiceVersionRepository serviceVersionRepository;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private StringHttpTransporter httpTransporter;

    private FunctionX pendingTask;

    private int pendingTimes = 0;

    @PostConstruct
    @Override
    public void init() {
        httpTransporter.configure(restTemplate -> {
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setConnectTimeout(3000);
            requestFactory.setReadTimeout(6000);
            restTemplate.setRequestFactory(requestFactory);
        });
        scheduler.scheduleAtFixedRate(() -> {
            boolean isGet = false;
            FunctionX task = taskQueue.poll();
            while (task != null) {
                pendingTask = task;
                isGet = true;
                task = taskQueue.poll();
            }
            if (pendingTask != null) {
                pendingTimes++;
            } else {
                pendingTimes = 0;
            }
            if (pendingTimes >= configProperties.getRoute().getRefreshCheckMaxPendingTimes()) {
                LOGGER.debug("pending route timeout and do notify...");
                pendingTask.apply();
                pendingTask = null;
                pendingTimes = 0;
                return;
            }
            if (pendingTask != null && !isGet) {
                LOGGER.debug("pending route is ready to notify and do notify...");
                pendingTask.apply();
                pendingTask = null;
                pendingTimes = 0;
            }
        }, configProperties.getRoute().getRefreshCheckPeriod(), configProperties.getRoute().getRefreshCheckPeriod(), TimeUnit.SECONDS);
    }

    @Override
    public void parser(InstanceAddedEvent event) {
        parser(event.getServiceName(), event.getServiceInstance());
    }

    private List<ChoerodonRouteData> parser(String serviceName, ServiceInstance instance) {
        try {
            // 跳过刷新路由的服务
            if (Arrays.stream(configProperties.getRoute().getSkipParseServices()).anyMatch(serviceName::contains)) {
                return null;
            }
            // 刷新服务版本信息
            String metaVersion = getVersionFromMetadata(instance);
            refreshServiceVersion(serviceName, metaVersion);

            // 刷新路由信息
            String json = fetchSwaggerJsonByIp(serviceName, metaVersion, instance);
            if (!StringUtils.isEmpty(serviceName) && !StringUtils.isEmpty(json)) {
                //刷新路由
                return refreshRoute(serviceName, json);
            }
        } catch (Exception e) {
            LOGGER.error("refresh service route error. [serviceName={}], ex={}", serviceName, e.getMessage());
            throw new CommonException("refresh service route error. serviceName=" + serviceName);
        }
        return null;
    }

    @Override
    public void parser(String serviceName, String version) {
        Assert.notNull(serviceName, "serviceName should not be null");
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        for (ServiceInstance instance : instances) {
            String v = getVersionFromMetadata(instance);
            if (version == null || version.equals(v)) {
                List<ChoerodonRouteData> routeData = parser(serviceName, instance);
                if (routeData == null) {
                    throw new ServiceSkipInitializationException("serviceName=" + serviceName + ",version=" + version);
                }
                checkRoute(routeData);
                return;
            }
        }
        throw new CommonException("未找到该服务");
    }

    /**
     * 检验路由是否正常，并将不正常的可能原因封装成异常信息返回
     * @throws CommonException
     */
    private void checkRoute(List<ChoerodonRouteData> routeData) throws CommonException {
        int maxCheckTimes = 10;
        int times = 0;
        boolean success = false;
        try {
            while(!Thread.currentThread().isInterrupted() && ++times < maxCheckTimes) {
                if (doCheckRoute(routeData)) {
                    success = true;
                    break;
                }
                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            throw new CommonException("检查路由失败，线程被中断!", e);
        }
        if (!success) {
            throw new CommonException("刷新路由失败，网关未应用该路由，网关繁忙请稍后再试，或直接尝试重启网关！");
        }
    }

    private boolean doCheckRoute(List<ChoerodonRouteData> routeData) {
        //检查环境的网关中是否存在该路由
        String gatewayName = HZeroService.getRealName(HZeroService.Gateway.NAME);
        List<ServiceInstance> instances = discoveryClient.getInstances(gatewayName);
        for (ServiceInstance instance : instances) {
            String response = null;
            try {
                response = httpTransporter.transport(new StringStaticEndpointHttpRequest(instance, StaticEndpoint.GATEWAY_ROUTES_QUERY));
            } catch (RestClientException e) {
                throw new CommonException("notify gateway refresh routes failed.", e);
            }
            for (ChoerodonRouteData route : routeData) {
                String routeId = route.getName();
                if (response == null || !response.contains("\"route_id\":\""+ routeId + "\"")) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 刷新服务版本信息
     *
     * @param serviceName 服务名
     * @param metaVersion 元数据版本
     */
    private void refreshServiceVersion(String serviceName, String metaVersion) {
        HService service = serviceRepository.selectOne(new HService().setServiceCode(serviceName));
        if (service == null) {
            service = new HService();
            service.setServiceCode(serviceName);
            service.setServiceName(serviceName);
            serviceRepository.insertSelective(service);
        }
        Long serviceId = service.getServiceId();
        ServiceVersion serviceVersion = serviceVersionRepository.selectOne(new ServiceVersion().setServiceId(serviceId).setMetaVersion(metaVersion));
        if (serviceVersion == null) {
            // 新建
            serviceVersion = new ServiceVersion()
                    .setMetaVersion(metaVersion)
                    .setServiceId(serviceId)
                    .setVersionNumber(metaVersion)
                    .setReleaseDate(new Date());
            serviceVersionRepository.insertSelective(serviceVersion);
        } else {
            // 更新, 以获取更新时间
            serviceVersionRepository.updateByPrimaryKey(serviceVersion);
        }
    }

    private String fetchSwaggerJsonByIp(String serviceName, String version, ServiceInstance instance) {
        if(instance == null){
            instance = getServiceInstance(serviceName, version);
        }
        return fetchSwaggerJsonByIp(instance);
    }

    private String fetchSwaggerJsonByIp(ServiceInstance instance) {
        LOGGER.info("service: {} metadata : {}", instance.getServiceId(), instance.getMetadata());
        try {
            return httpTransporter.transport(new StringStaticEndpointHttpRequest(instance, StaticEndpoint.CHOERODON_API_DOCS));
        } catch (RestClientException e) {
            LOGGER.error("fetch error, ex={}", e.getMessage());
            throw new RemoteAccessException("fetch failed, instance: " + instance.getServiceId() + ", ex=" + e.getMessage());
        }
    }

    private ServiceInstance getServiceInstance(String serviceName, String version) {
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceName);
        for (ServiceInstance serviceInstance : serviceInstances){
            Map<String, String> metadata = serviceInstance.getMetadata();
            String metaVersion = metadata == null ?
                    NULL_VERSION : (metadata.get(METADATA_VERSION) == null ?
                    NULL_VERSION : metadata.get(METADATA_VERSION));
            if(metaVersion.equals(version)){
                return serviceInstance;
            }
        }
        throw new IllegalStateException("can't find the service [serviceName=" + serviceName + ",version=" + version + "] in service discovery");
    }

    private List<ChoerodonRouteData> refreshRoute(String serviceName, String swaggerJson) {
        List<ChoerodonRouteData> dataList = null;
        try {
            dataList = resolveChoerodonRouteData(swaggerJson);
        } catch (IOException e) {
            LOGGER.error("resolve ChoerodonRouteData failed [swaggerJson=" + swaggerJson + "].", e);
        }
        if (dataList == null || dataList.isEmpty()) {
            LOGGER.warn("cant't parse service[{}] route data, check if config ExtraDataManager.", serviceName);
            throw new CommonException("hadm.error.choerodon_data_not_found");
        }
        LOGGER.info("pre refresh route, route data={}", dataList);

        executeRefreshRoute(dataList, serviceName);
        return dataList;
    }

    private void refreshRouteMapInCache(List<ChoerodonRouteData> dataList, String serviceName) {
        for (ChoerodonRouteData routeData : dataList){
            BoundHashOperations<String, String, String> hashOperations = redisHelper.getRedisTemplate().boundHashOps(ROUTE_MAP_KEY);
            if (DetailsHelper.getUserDetails() != null && DetailsHelper.getUserDetails().getUserId() != null) {
                hashOperations.put(resolveServiceCode(routeData.getPath()), serviceName);
            } else {
                hashOperations.putIfAbsent(resolveServiceCode(routeData.getPath()), serviceName);
            }
        }
    }

    private String resolveServiceCode(String path) {
        return path.replaceAll("/\\*\\*", "").replaceAll("/", "");
    }

    private List<ChoerodonRouteData> resolveChoerodonRouteData(String swaggerJson) throws IOException {
        JsonNode node = mapper.readTree(swaggerJson);
        JsonNode extraData = node.get("extraData");
        if (extraData == null) {
            return Collections.emptyList();
        }
        JsonNode data = extraData.get("data");
        if (data == null) {
            return Collections.emptyList();
        }
        JsonNode routes = data.get("choerodon_route");
        if (routes == null) {
            return Collections.emptyList();
        }
        List<ChoerodonRouteData> returnVal = new ArrayList<>();
        try {
            ChoerodonRouteData ChoerodonRouteData = mapper.readValue(routes.toString(), io.choerodon.core.swagger.ChoerodonRouteData.class);
            returnVal.add(ChoerodonRouteData);
            return returnVal;
        } catch (IOException e) {
            LOGGER.info("resolve ChoerodonRouteData failed because of version upgrade.");
        }
        for (int i = 0; i < routes.size(); i++) {
            JsonNode jsonNode = routes.get(i);
            returnVal.add(mapper.readValue(jsonNode.toString(), ChoerodonRouteData.class));
        }
        return returnVal;
    }

    private void executeRefreshRoute(final List<ChoerodonRouteData> dataList, String serviceName) {
        for (ChoerodonRouteData data : dataList) {
            data.setServiceId(serviceName);
            executeRefreshRoute(data);
        }
        //更新缓存中的路由关系
        refreshRouteMapInCache(dataList, serviceName);
    }

    private void executeRefreshRoute(final ChoerodonRouteData data) {
        ServiceRoute route = new ServiceRoute();
        setRoute(data, route);
        setRouteService(route);

        ServiceRoute param1 = new ServiceRoute();
        param1.setName(route.getName());
        ServiceRoute self1 = routeRepository.selectOne(param1);

        ServiceRoute param2 = new ServiceRoute();
        param2.setPath(route.getPath());
        ServiceRoute self2 = routeRepository.selectOne(param2);

        if (self1 == null && self2 == null){
            LOGGER.info("create service route: {}", route);
            routeRepository.insertSelective(route);
        } else {
            StringBuilder cause = new StringBuilder("\n");
            if (self1 != null){
                cause.append("route name [").append(route.getName()).append("] exists!\n");
            }
            if (self2 != null){
                cause.append("route path [").append(route.getPath()).append("] exists!\n");
            }
            LOGGER.info("route conflict, try to modify it on the interface, cause: {}", cause.toString());
        }

        taskQueue.offer(() -> routeRefreshService.notifyGateway());
    }

    private void setRoute(ChoerodonRouteData choerodonRouteData, ServiceRoute route) {
        route.setName(choerodonRouteData.getName());
        route.setPath(choerodonRouteData.getPath());
        route.setServiceCode(choerodonRouteData.getServiceId());
        route.setSensitiveHeaders(choerodonRouteData.getSensitiveHeaders());
        route.setStripPrefix(choerodonRouteData.getStripPrefix() != null && choerodonRouteData.getStripPrefix() ? BaseConstants.Flag.YES : BaseConstants.Flag.NO);
        route.setUrl(choerodonRouteData.getUrl());
    }

    private void setRouteService(ServiceRoute route) {
        HService queryService = new HService();
        queryService.setServiceCode(route.getServiceCode());
        queryService = serviceRepository.selectOne(queryService);
        if (queryService != null) {
            route.setServiceId(queryService.getServiceId());
        } else {
            HService insert = new HService();
            insert.setServiceCode(route.getServiceCode());
            insert.setServiceName(route.getServiceCode());
            serviceRepository.insertSelective(insert);
            route.setServiceId(insert.getServiceId());

            LOGGER.info("create service: {}", route.getServiceCode());
        }
    }

}

@FunctionalInterface
interface FunctionX {

    void apply();

}
