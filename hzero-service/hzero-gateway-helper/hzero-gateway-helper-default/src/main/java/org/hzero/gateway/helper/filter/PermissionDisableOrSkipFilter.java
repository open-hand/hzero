package org.hzero.gateway.helper.filter;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import org.hzero.gateway.helper.api.HelperFilter;
import org.hzero.gateway.helper.config.GatewayHelperProperties;
import org.hzero.gateway.helper.entity.CheckState;
import org.hzero.gateway.helper.entity.RequestContext;

/**
 * 是否启用权限校验和跳过权限校验路径的过滤器
 */
@Component
public class PermissionDisableOrSkipFilter implements HelperFilter {

    private final AntPathMatcher matcher = new AntPathMatcher();

    private GatewayHelperProperties gatewayHelperProperties;

    public PermissionDisableOrSkipFilter(GatewayHelperProperties gatewayHelperProperties) {
        this.gatewayHelperProperties = gatewayHelperProperties;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return true;
    }

    @Override
    public boolean run(RequestContext context) {
        if (!gatewayHelperProperties.getPermission().getEnabled()) {
            context.response.setStatus(CheckState.SUCCESS_PERMISSION_DISABLED);
            context.response.setMessage("Permission check disabled");
            return false;
        }

        GatewayHelperProperties.Permission permission = gatewayHelperProperties.getPermission();
        if (permission.getInternalPaths().stream().anyMatch(t -> matcher.match(t, context.request.uri))) {
            context.response.setStatus(CheckState.PERMISSION_WITH_IN);
            context.response.setMessage("No access to within interface");
            return false;
        }

        if (permission.getSkipPaths().stream().anyMatch(t -> matcher.match(t, context.request.uri))) {
            context.response.setStatus(CheckState.SUCCESS_SKIP_PATH);
            context.response.setMessage("This request match skipPath, skipPaths: " +
                    gatewayHelperProperties.getPermission().getSkipPaths());
            return false;
        }
        return true;
    }
}
