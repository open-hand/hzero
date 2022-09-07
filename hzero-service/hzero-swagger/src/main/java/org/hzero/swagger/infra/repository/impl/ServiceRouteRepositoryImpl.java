package org.hzero.swagger.infra.repository.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.swagger.domain.entity.ServiceRoute;
import org.hzero.swagger.domain.repository.ServiceRouteRepository;
import org.hzero.swagger.infra.constant.Versions;
import org.hzero.swagger.infra.mapper.ServiceRouteMapper;

/**
 * ServiceRoute Repository
 *
 * @author bojiangzhou 2018/12/13
 */
@Component
public class ServiceRouteRepositoryImpl extends BaseRepositoryImpl<ServiceRoute> implements ServiceRouteRepository {

    @Autowired
    private ServiceRouteMapper serviceRouteMapper;
    @Autowired
    private DiscoveryClient discoveryClient;

    @Override
    public List<ServiceRoute> getAllRoute() {
        List<ServiceRoute> routes = serviceRouteMapper.selectRoutes(ServiceRoute.EMPTY);
        routes.forEach(r -> r.setServiceCode(r.getServiceCode().toLowerCase()));
        return routes;
    }

    @Override
    public MultiKeyMap getAllRunningInstances() {
        List<ServiceRoute> routeList = getAllRoute();
        List<String> serviceIds = discoveryClient.getServices();
        routeList = routeList.stream().filter(r -> serviceIds.contains(r.getServiceCode())).collect(Collectors.toList());

        MultiKeyMap multiKeyMap = new MultiKeyMap();
        for (ServiceRoute route : routeList) {
            String serviceId = route.getServiceCode();
            String name = route.getName();
            for (ServiceInstance instance : discoveryClient.getInstances(serviceId)) {
                String version = instance.getMetadata().get(Versions.METADATA_VERSION);
                if (StringUtils.isEmpty(version)) {
                    version = Versions.NULL_VERSION;
                }
                if (multiKeyMap.get(name, version) == null) {
                    multiKeyMap.put(name, version, route);
                }
            }
        }

        return multiKeyMap;
    }

}
