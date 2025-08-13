package org.hzero.admin.app.service.metric;

import org.apache.commons.beanutils.BeanUtils;
import org.hzero.admin.domain.entity.ApiMonitor;
import org.hzero.admin.domain.repository.ApiMonitorRepository;
import org.hzero.common.HZeroService;
import org.hzero.core.endpoint.HttpTransporter;
import org.hzero.core.endpoint.client.BaseHttpTransporter;
import org.hzero.core.endpoint.request.StaticEndpoint;
import org.hzero.core.endpoint.request.StaticEndpointHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/12/11 10:17 上午
 */
@Service
public class MetricSyncService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricSyncService.class);

    private static final String UNRESOLVED_IP = "unresolved";

    @Autowired
    private ApiMonitorRepository apiMonitorRepository;
    @Autowired
    private DiscoveryClient discoveryClient;

    private HttpTransporter<Map> httpTransporter = new BaseHttpTransporter<>();

    @Transactional(rollbackFor = RuntimeException.class)
    public void sync(){
        apiMonitorRepository.deleteAll();
        List<ApiMonitor> apiMonitors = readFromGateway();
        apiMonitorRepository.batchInsert(apiMonitors);
    }

    private List<ApiMonitor> readFromGateway() {
        List<ServiceInstance> instances = discoveryClient.getInstances(HZeroService.getRealName(HZeroService.Gateway.NAME));

        Map<String, ApiMonitor> apiMonitorMap = new HashMap<>();
        for (ServiceInstance instance : instances){
            Map<String, Object> body = null;
            try {
                body = (Map<String, Object>) httpTransporter.transport(new StaticEndpointHttpRequest<>(instance, StaticEndpoint.GATEWAY_REQUEST_COUNT, Map.class));
            }catch (ClassCastException | RestClientException e){
                LOGGER.error("gateway [host={}] notify failed, ex = {}", instance.getHost(), e.getMessage());
                continue;
            }
            if (CollectionUtils.isEmpty(body)){
                continue;
            }
            Map<String, MetricMap.RequestCount> requestCountMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : body.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                MetricMap.RequestCount requestCount = new MetricMap.RequestCount();
                try {
                    BeanUtils.populate(requestCount, (LinkedHashMap) value);
                }catch (InvocationTargetException | IllegalAccessException e){
                    LOGGER.error("Map transfer RequestCount failed", e);
                }
                requestCountMap.put(key, requestCount);
            }
            if (!CollectionUtils.isEmpty(requestCountMap)){
                for (Map.Entry<String, MetricMap.RequestCount> entry : requestCountMap.entrySet()) {
                    if (entry.getKey().equals(UNRESOLVED_IP)){
                        //skip metric unresolved ip
                        continue;
                    }
                    ApiMonitor apiMonitor = apiMonitorMap.get(entry.getKey());
                    if (apiMonitor == null) {
                        apiMonitorMap.putIfAbsent(entry.getKey(), createApiMonitor(entry.getKey(), entry.getValue()));
                    } else {
                        append(apiMonitor, entry.getValue());
                    }
                }
            }
        }

        return new ArrayList<>(apiMonitorMap.values());
    }

    private ApiMonitor createApiMonitor(String key, MetricMap.RequestCount requestCount){
        ApiMonitor apiMonitor = new ApiMonitor();
        String[] parts = key.split("::");
        Assert.isTrue(parts.length >= 3, "hadm.error.create_api_monitor");
        apiMonitor.setMonitorRuleId(Long.valueOf(parts[0]));
        apiMonitor.setMonitorUrl(parts[1]);
        apiMonitor.setMonitorKey(parts[2]);
        apiMonitor.setMaxStatistics(requestCount.getMaxRequests());
        apiMonitor.setMinStatistics(requestCount.getMinRequests());
        apiMonitor.setAvgFailedStatistics(requestCount.getAvgFailedRequests());
        apiMonitor.setAvgStatistics(requestCount.getAvgRequests());
        apiMonitor.setAvgCount(requestCount.getAvgCount());
        apiMonitor.setSumFailedStatistics(requestCount.getSumFailedRequests());
        apiMonitor.setSumStatistics(requestCount.getSumRequests());
        apiMonitor.setStartDate(new Date(requestCount.getStartTime()));
        apiMonitor.setEndDate(new Date(requestCount.getEndTime()));
        return apiMonitor;
    }

    private void append(ApiMonitor apiMonitor, MetricMap.RequestCount requestCount){
        if (requestCount.getMaxRequests() > apiMonitor.getMaxStatistics()) {
            apiMonitor.setMaxStatistics(requestCount.getMaxRequests());
        }
        if (requestCount.getMinRequests() < apiMonitor.getMinStatistics()) {
            apiMonitor.setMinStatistics(requestCount.getMinRequests());
        }
        apiMonitor.setSumFailedStatistics(apiMonitor.getSumFailedStatistics() + requestCount.getSumFailedRequests());
        apiMonitor.setSumStatistics(apiMonitor.getSumStatistics() + requestCount.getSumRequests());

        Date start = new Date(requestCount.getStartTime());
        Date end = new Date(requestCount.getEndTime());
        if (start.before(apiMonitor.getStartDate())){
            apiMonitor.setStartDate(start);
        }
        if (end.after(apiMonitor.getEndDate())){
            apiMonitor.setEndDate(end);
        }
        apiMonitor.addCountAndRefreshAvg(requestCount.getAvgCount());
    }

}
