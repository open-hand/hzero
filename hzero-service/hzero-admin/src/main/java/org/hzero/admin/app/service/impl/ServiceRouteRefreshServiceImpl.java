package org.hzero.admin.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hzero.admin.app.service.ServiceRouteRefreshService;
import org.hzero.admin.domain.entity.ServiceRoute;
import org.hzero.admin.domain.repository.ServiceRouteRepository;
import org.hzero.admin.domain.service.ConfigRefreshService;
import org.hzero.admin.domain.service.ParseRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2020/2/21 4:20 下午
 */
@Service
public class ServiceRouteRefreshServiceImpl implements ServiceRouteRefreshService {

    @Lazy
    @Autowired
    private ConfigRefreshService configRefreshService;
    @Lazy
    @Autowired
    private ParseRouteService parseRouteService;
    @Lazy
    @Autowired
    private ServiceRouteRepository serviceRouteRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void notifyGateway() {
        configRefreshService.notifyGatewayRefresh();
    }

    @Override
    public void refreshRouteAndNotifyGateway(String serviceName, String version) {
        parseRouteService.parser(serviceName, version);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void initRouteExtendConfigAndNotifyGateway(List<Long> routeIds) {
        String routesString = routeIds.toString();
        if(routesString.length() <= 2){
            return;
        }
        List<ServiceRoute> serviceRoutes = serviceRouteRepository.selectByIds(routesString.substring(1, routesString.length() - 1));
        serviceRouteRepository.batchUpdateByPrimaryKey(buildServiceRouteList(serviceRoutes));
        configRefreshService.notifyGatewayRefresh();
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void removeRouteExtendConfigAndNotifyGateway(String fieldName, List<Long> routeIds) {
        String routesString = routeIds.toString();
        if(routesString.length() <= 2){
            return;
        }
        List<ServiceRoute> serviceRoutes = serviceRouteRepository.selectByIds(routesString.substring(1, routesString.length() - 1));
        serviceRouteRepository.batchUpdateByPrimaryKey(buildServiceRouteList(fieldName, serviceRoutes));
        configRefreshService.notifyGatewayRefresh();
    }

    private List<ServiceRoute> buildServiceRouteList(String filterName, List<ServiceRoute> serviceRoutes) {
        return serviceRoutes.stream()
                .peek(route -> {
                    try {
                        String origin = route.getExtendConfigMap();
                        if (origin == null){
                            return;
                        }
                        Map<String, Object> map = mapper.readValue(origin, Map.class);
                        List<String> filters = (List<String>) map.get("filters");
                        for (String filter : filters) {
                            if (filter.contains(filterName)) {
                                filters.remove(filter);
                                break;
                            }
                        }
                        route.setExtendConfigMap(mapper.writeValueAsString(map));
                    } catch (IOException e) {
                        route.setExtendConfigMap(null);
                    }
                })
                .collect(Collectors.toList());
    }

    private List<ServiceRoute> buildServiceRouteList(List<ServiceRoute> serviceRoutes) {
        return serviceRoutes.stream()
                .peek(route -> route.setExtendConfigMap(null))
                .collect(Collectors.toList());
    }

}
