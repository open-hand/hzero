package org.hzero.admin.infra.repository.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.lang3.StringUtils;
import org.hzero.admin.domain.entity.ServiceRoute;
import org.hzero.admin.domain.repository.ServiceRouteRepository;
import org.hzero.admin.infra.mapper.ServiceRouteMapper;
import org.hzero.admin.infra.util.VersionUtil;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;

/**
 * 服务路由配置 资源库实现
 *
 * @author zhiying.dong@hand-china.com 2018-12-07 14:45:53
 */
@Component
public class ServiceRouteRepositoryImpl extends BaseRepositoryImpl<ServiceRoute> implements ServiceRouteRepository {
    /**
     * 日志打印对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRouteRepositoryImpl.class);

    @Autowired
    private ServiceRouteMapper serviceRouteMapper;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Override
    public List<ServiceRoute> selectDefaultRoutes(ServiceRoute serviceRoute) {
        return serviceRouteMapper.selectDefaultRoutes(serviceRoute);
    }

    @Override
    public ServiceRoute selectRouteDetails(Long serviceRouteId) {
        return serviceRouteMapper.selectRouteDetails(serviceRouteId);
    }

    @Override
    public List<ServiceRoute> selectRouteDetails(String serviceCode) {
        return select(new ServiceRoute().setServiceCode(serviceCode));
    }

    @Override
    public MultiKeyMap<String, List<ServiceRoute>> getAllRunningInstances() {
        MultiKeyMap<String, List<ServiceRoute>> runningInstances = new MultiKeyMap<>();
        List<String> serviceCodes = this.discoveryClient.getServices();

        if (CollectionUtils.isEmpty(serviceCodes)) {
            return runningInstances;
        }

        // 查询服务的路由
        Map<String, List<ServiceRoute>> serviceRouteMap = Optional
                .ofNullable(this.selectByCondition(Condition.builder(ServiceRoute.class)
                        .andWhere(Sqls.custom()
                                .andIn(ServiceRoute.FIELD_SERVICE_CODE, serviceCodes)
                        ).build())).orElse(Collections.emptyList())
                .stream().collect(groupingBy(ServiceRoute::getServiceCode));
        if (MapUtils.isEmpty(serviceRouteMap)) {
            return runningInstances;
        }

        String version;
        List<ServiceRoute> serviceRoutes;
        for (String serviceCode : serviceCodes) {
            for (ServiceInstance instance : this.discoveryClient.getInstances(serviceCode)) {
                LOGGER.info("instance is {}", instance);
                version = instance.getMetadata().get(VersionUtil.METADATA_VERSION);
                if (StringUtils.isBlank(version)) {
                    version = VersionUtil.NULL_VERSION;
                }
                if (!runningInstances.containsKey(serviceCode, version)) {
                    serviceRoutes = serviceRouteMap.get(serviceCode);
                    if (CollectionUtils.isEmpty(serviceRoutes)) {
                        continue;
                    }
                    runningInstances.put(serviceCode, version, serviceRoutes);
                }
            }
        }

        return runningInstances;
    }
}
