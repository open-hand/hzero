package org.hzero.gateway.helper.filter;

import org.springframework.stereotype.Component;

import org.hzero.gateway.helper.api.HelperFilter;
import org.hzero.gateway.helper.entity.CheckState;
import org.hzero.gateway.helper.entity.RequestContext;

/**
 * 超级管理员的权限校验
 */
@Component
public class AdminUserPermissionFilter implements HelperFilter {

    @Override
    public int filterOrder() {
        return 70;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return context.getCustomUserDetails().getAdmin() != null
                && context.getCustomUserDetails().getAdmin();
    }

    @Override
    public boolean run(RequestContext context) {
        context.response.setStatus(CheckState.SUCCESS_ADMIN);
        context.response.setMessage("Admin user have access to the interface, username: "
                + context.getCustomUserDetails().getUsername());
        return false;
    }
}
