package org.hzero.swagger.app.impl;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.collections.map.MultiKeyMap;
import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;
import org.hzero.swagger.app.SwaggerService;
import org.hzero.swagger.config.SwaggerProperties;
import org.hzero.swagger.domain.entity.ServiceRoute;
import org.hzero.swagger.domain.entity.Swagger;
import org.hzero.swagger.domain.repository.ServiceRouteRepository;
import org.hzero.swagger.domain.repository.SwaggerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.UiConfiguration;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class SwaggerServiceImpl implements SwaggerService {

    @Autowired
    private SwaggerProperties swaggerProperties;
    @Autowired
    private ServiceRouteRepository serviceRouteRepository;
    @Autowired
    private SwaggerRepository swaggerRepository;
    @Autowired
    private RedisHelper redisHelper;

    @Override
    public List<SwaggerResource> getSwaggerResource() {
        List<SwaggerResource> resources = new LinkedList<>();
        MultiKeyMap multiKeyMap = serviceRouteRepository.getAllRunningInstances();
        Set set = multiKeyMap.keySet();
        for (Object key : set) {
            MultiKey multiKey = (MultiKey) key;
            ServiceRoute route = (ServiceRoute) multiKeyMap.get(multiKey);
            if (route.getServiceCode() != null) {
                boolean isSkipService = Arrays.stream(swaggerProperties.getSkipService()).anyMatch(t -> route.getServiceCode().contains(t.trim()));
                if (!isSkipService) {
                    SwaggerResource resource = new SwaggerResource();
                    resource.setName(route.getName() + ":" + route.getServiceCode());
                    resource.setSwaggerVersion("2.0");
                    resource.setLocation("/docs/" + route.getName() + "?version=" + multiKey.getKey(1));
                    resources.add(resource);
                }
            }
        }
        resources = resources.stream().sorted(Comparator.comparing(SwaggerResource::getName)).collect(Collectors.toList());
        return resources;
    }

    @Override
    public UiConfiguration getUiConfiguration() {
        return new UiConfiguration(null);
    }

    @Override
    public SecurityConfiguration getSecurityConfiguration() {
        return new SecurityConfiguration(
                swaggerProperties.getClient(), "unknown", "default",
                "default", "token",
                ApiKeyVehicle.HEADER, "token", ",");
    }

    @Override
    public void updateOrInsertSwagger(String serviceName, String version, String json) {
        Swagger param = new Swagger();
        param.setServiceName(serviceName);
        param.setServiceVersion(version);
        Swagger swagger = swaggerRepository.selectId(param);
        if (swagger != null) {
            // 先删除 再添加
            swaggerRepository.deleteByPrimaryKey(swagger.getId());
        }

        Swagger inert = new Swagger();
        inert.setServiceName(serviceName);
        inert.setServiceVersion(version);
        inert.setValue(json);

        swaggerRepository.insert(inert);
        // 设置服务swagger文档刷新时间
        this.redisHelper.hshPut(String.format("%s:swagger-refresh:service:%s", HZeroService.Swagger.CODE, serviceName),
                version, String.valueOf(System.currentTimeMillis()));
    }
}
