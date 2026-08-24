package org.hzero.gateway.helper.filter;

import org.apache.commons.lang3.StringUtils;
import org.hzero.gateway.helper.api.HelperFilter;
import org.hzero.gateway.helper.entity.CheckState;
import org.hzero.gateway.helper.entity.RequestContext;
import org.springframework.stereotype.Component;

/**
 * loginAccess请求的权限校验
 *
 * @author bojiangzhou Mark: 针对SQL类型的LOV请求，需校验权限
 */
@Component
public class LoginAccessRequestFilter implements HelperFilter {

    @Override
    public int filterOrder() {
        return 60;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return true;
    }

    @Override
    public boolean run(RequestContext context) {
        // lov 访问需验证权限
        if (StringUtils.isNotEmpty(context.getLovCode())) {
            return true;
        }
        if (context.getCustomUserDetails() != null) {
            context.response.setStatus(CheckState.SUCCESS_LOGIN_ACCESS);
            context.response.setMessage("Have access to this 'loginAccess' interface, permission: " + context.getPermission());
            return false;
        }
        return true;
    }

}
