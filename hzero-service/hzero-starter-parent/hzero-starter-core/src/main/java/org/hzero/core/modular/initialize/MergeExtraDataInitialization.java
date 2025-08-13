package org.hzero.core.modular.initialize;

import io.choerodon.core.swagger.ChoerodonRouteData;
import io.choerodon.swagger.swagger.extra.ExtraData;
import io.choerodon.swagger.swagger.extra.ExtraDataInitialization;
import io.choerodon.swagger.swagger.extra.ExtraDataProcessor;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.exception.ServiceStartException;
import org.hzero.core.modular.ModularProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 把服务合并所需的包路径与服务名的映射关系合并到ExtraData
 * @author XCXCXCXCX
 * @date 2019/8/26
 */
public class MergeExtraDataInitialization implements ExtraDataInitialization, EnvironmentAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeExtraDataInitialization.class);

    public static final String MODULAR_PACKAGES_MAP_KEY = "modular_packages_map";

    private final ModularProperties properties;

    /**
     * 包路径与服务名的映射关系
     */
    private final Map<String, String> modularPackagesMap = new HashMap<>(4);

    @Value("${spring.application.name}")
    private String appName;

    private String primary = null;

    private Environment environment;

    @Autowired
    private ExtraDataProcessor extraDataProcessor;

    public MergeExtraDataInitialization(ModularProperties properties) {
        this.properties = properties;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void init(ExtraData ignore) {
        ExtraData extraData = extraDataProcessor.getExtraData();
        Map<String, Object> map = extraData.getData();
        @SuppressWarnings("unchecked")
        List<ChoerodonRouteData> routeDataList = (List<ChoerodonRouteData>) map.get(ExtraData.ZUUL_ROUTE_DATA);
        if(routeDataList.isEmpty() && !properties.isEnable()){
            //skip
            return;
        }else{
            for (ChoerodonRouteData routeData : routeDataList) {
                checkField(routeData);
                String serviceId = environment.resolvePlaceholders(routeData.getServiceId());
                routeData.setServiceId(serviceId);
                if (appName.equals(serviceId)) {
                    primary = serviceId;
                    LOGGER.warn("The [name] and [path] of ChoerodonRouteData should not conflict with existing routing information!");
                }
                if (properties.isEnable()) {
                    String packagesString = routeData.getPackages();
                    String[] packages = packagesString.split(",");
                    for (String pac : packages) {
                        modularPackagesMap.put(pac, serviceId);
                    }
                }
            }
        }
        if(properties.isEnable()){
            if (primary == null) {
                throw new ServiceStartException("No routeData corresponding to the service name was found. [routeDataList=" + routeDataList.toString() + "]", "开启了服务合并后(hzero.modular.enable=true)，需要存在一个路由信息ChoerodonRouteData是自身服务的。\n1.如果未使用服务合并，请关闭配置。\n2.如果使用服务合并，请确保路由信息与服务对应。");
            } else {
                extraData.put(MODULAR_PACKAGES_MAP_KEY, modularPackagesMap);
            }
        }
    }

    private void checkField(ChoerodonRouteData routeData) {
        if (routeData == null) {
            throw new IllegalArgumentException("RouteData is not allowed to be empty!");
        }
        if (StringUtils.isEmpty(routeData.getName())) {
            throw new IllegalArgumentException("The [name] parameter must be specified for RouteData!");
        }
        if (StringUtils.isEmpty(routeData.getPath())) {
            throw new IllegalArgumentException("The [path] parameter must be specified for RouteData!");
        }
        if (StringUtils.isEmpty(routeData.getServiceId())) {
            throw new IllegalArgumentException("The [serviceId] parameter must be specified for RouteData!");
        }
        if (properties.isEnable() && StringUtils.isEmpty(routeData.getPackages())) {
            throw new IllegalArgumentException("The [packages] parameter must be specified for RouteData!");
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
