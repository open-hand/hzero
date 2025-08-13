package org.hzero.gateway.helper.resolver;

import org.hzero.gateway.helper.entity.CommonRoute;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.cloud.netflix.zuul.RoutesRefreshedEvent;
import org.springframework.cloud.netflix.zuul.filters.CompositeRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author XCXCXCXCX
 * @date 2019/9/3
 * @project hzero-gateway-helper
 */
public class ZuulPropertiesResolver implements PropertiesResolver<ZuulProperties>, ApplicationListener<ApplicationEvent> {

    private final CompositeRouteLocator locator;

    public ZuulPropertiesResolver(Object locator) {
        this.locator = (CompositeRouteLocator) locator;
        this.locator.refresh();
    }

    @Override
    public Map<String, CommonRoute> resolveRoutes(ZuulProperties properties) {
        Map<String, ZuulProperties.ZuulRoute> zuulRouteMap = properties.getRoutes();
        if(!CollectionUtils.isEmpty(zuulRouteMap)){
            Map<String, CommonRoute> returnVal = new HashMap<>(32);
            Set<Map.Entry<String, ZuulProperties.ZuulRoute>> entrySet = zuulRouteMap.entrySet();
            entrySet
                    .forEach(entry -> {
                        CommonRoute route = new CommonRoute();
                        BeanUtils.copyProperties(entry.getValue(), route);
                        returnVal.put(entry.getKey(), route);
                    });
            return returnVal;
        }
        return Collections.emptyMap();
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if(event instanceof RefreshEvent || event instanceof RoutesRefreshedEvent){
            locator.refresh();
        }
    }
}
