package org.hzero.gateway.helper.filter;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import org.hzero.gateway.helper.api.HelperFilter;
import org.hzero.gateway.helper.config.GatewayHelperProperties;
import org.hzero.gateway.helper.entity.CheckState;
import org.hzero.gateway.helper.entity.RequestContext;
import org.hzero.gateway.helper.infra.mapper.PermissionPlusMapper;


/**
 * 校验菜单是否分配了API
 */
@Component
public class MenuPermissionFilter implements HelperFilter {

    private GatewayHelperProperties gatewayHelperProperties;
    private PermissionPlusMapper permissionPlusMapper;

    public MenuPermissionFilter(GatewayHelperProperties gatewayHelperProperties,
                                PermissionPlusMapper permissionPlusMapper) {
        this.gatewayHelperProperties = gatewayHelperProperties;
        this.permissionPlusMapper = permissionPlusMapper;
    }

    @Override
    public int filterOrder() {
        return 75;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return gatewayHelperProperties.getFilter().getMenuPermission().isEnabled();
    }

    @Override
    public boolean run(RequestContext context) {
        Long menuId = context.getMenuId();
        if (menuId == null) {
            return true;
        }

        String permissionCode = null;
        if (StringUtils.isEmpty(context.getLovCode())) {
            permissionCode = context.getPermission().getCode();
        } else {
            permissionCode = context.getLovCode();
        }
        int count = permissionPlusMapper.countMenuPermission(menuId, permissionCode);
        if (count == 0) {
            context.response.setStatus(CheckState.PERMISSION_MENU_MISMATCH);
            context.response.setMessage("Menu mismatch permission, menuId = [" + menuId + "], permissionCode = [" + permissionCode + "]");

            return false;
        }

        return true;
    }



}
