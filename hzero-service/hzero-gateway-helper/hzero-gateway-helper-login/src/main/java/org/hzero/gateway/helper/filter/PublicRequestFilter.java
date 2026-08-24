package org.hzero.gateway.helper.filter;

import org.hzero.gateway.helper.api.HelperFilter;
import org.hzero.gateway.helper.config.GatewayConfig;
import org.hzero.gateway.helper.entity.CheckState;
import org.hzero.gateway.helper.entity.RequestContext;
import org.springframework.stereotype.Component;


/**
 * 公共接口的权限校验
 */
@Component
public class PublicRequestFilter implements HelperFilter {

    private final GatewayConfig config;

    public PublicRequestFilter(GatewayConfig config) {
        this.config = config;
    }

    @Override
    public int filterOrder() {
        return 30;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        String requestUri = context.request.uri;
        return (requestUri.contains("/" + config.getSkipPrefix() + "/") || requestUri.endsWith("/" + config.getSkipPrefix()));
    }

    @Override
    public boolean run(RequestContext context) {
        context.response.setStatus(CheckState.SUCCESS_PUBLIC_ACCESS);
        context.response.setMessage("Have access to this 'publicAccess' interface, permission: " + context.getPermission());
        return false;
    }

}
