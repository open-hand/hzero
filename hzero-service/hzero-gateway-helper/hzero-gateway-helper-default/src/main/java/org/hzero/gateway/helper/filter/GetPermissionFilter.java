package org.hzero.gateway.helper.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import org.hzero.core.base.BaseHeaders;
import org.hzero.core.util.ServerRequestUtils;
import org.hzero.gateway.helper.api.HelperFilter;
import org.hzero.gateway.helper.entity.CheckState;
import org.hzero.gateway.helper.entity.PermissionDO;
import org.hzero.gateway.helper.entity.RequestContext;
import org.hzero.gateway.helper.service.PermissionService;
import org.hzero.starter.keyencrypt.core.EncryptContext;
import org.hzero.starter.keyencrypt.core.EncryptType;
import org.hzero.starter.keyencrypt.core.IEncryptionService;

/**
 * 根据接口uri，method和service获取匹配到的权限
 * 匹配不到或者接口类型为内部接口，返回失败，不再向下执行
 */
@Component
public class GetPermissionFilter implements HelperFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPermissionFilter.class);

    private final PermissionService permissionService;
    private final IEncryptionService encryptionService;

    public GetPermissionFilter(PermissionService permissionService, IEncryptionService encryptionService) {
        this.permissionService = permissionService;
        this.encryptionService = encryptionService;
    }

    @Override
    public int filterOrder() {
        return 20;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return true;
    }

    @Override
    public boolean run(RequestContext context) {
        // 设置请求的菜单ID
        context.setMenuId(getMenuId(context));

        String key = context.getRequestKey();
        PermissionDO permission = permissionService.selectPermissionByRequest(key);
        if (permission == null) {
            context.response.setStatus(CheckState.PERMISSION_MISMATCH);
            context.response.setMessage("This request mismatch any permission");
            return false;
        } else if (permission.getWithin()) {
            context.response.setStatus(CheckState.PERMISSION_WITH_IN);
            context.response.setMessage("No access to within interface");
            return false;
        } else {
            context.setPermission(permission);
        }
        return true;
    }

    private Long getMenuId(RequestContext context) {
        Object servletRequest = context.getServletRequest();
        String menuId = ServerRequestUtils.getHeaderValue(servletRequest, BaseHeaders.H_MENU_ID);

        if (StringUtils.isEmpty(menuId)) {
            return null;
        }

        Long id = null;
        try {
            if (encryptionService.isCipher(menuId)) {
                EncryptContext.setEncryptType(EncryptType.ENCRYPT.name());
                menuId = encryptionService.decrypt(menuId, "", context.request.accessToken);
                EncryptContext.clear();
            }
            id = Long.parseLong(menuId);
        } catch (NumberFormatException e) {
            LOGGER.warn("Header of [{}] format error, header value is [{}]", BaseHeaders.H_MENU_ID, menuId);
        }
        return id;
    }

}
