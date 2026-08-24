package org.hzero.gateway.helper.config;

import org.hzero.gateway.helper.entity.CommonRoute;
import org.hzero.gateway.helper.resolver.PropertiesResolver;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @author XCXCXCXCX
 * @date 2019/9/3
 * @project hzero-gateway-helper
 */
public final class GatewayPropertiesWrapper {

    private Object properties;

    @Autowired
    private PropertiesResolver resolver;

    public GatewayPropertiesWrapper(Object properties) {
        this.properties = properties;
    }

    public Map<String, CommonRoute> getRoutes(){
        return resolver.resolveRoutes(properties);
    }

}
