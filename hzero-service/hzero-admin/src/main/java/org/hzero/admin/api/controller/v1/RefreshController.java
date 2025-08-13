package org.hzero.admin.api.controller.v1;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.hzero.admin.api.dto.RoleDTO;
import org.hzero.admin.app.service.ServiceConfigRefreshService;
import org.hzero.admin.app.service.ServiceConfigService;
import org.hzero.admin.app.service.ServiceRouteRefreshService;
import org.hzero.admin.app.service.ServiceRouteService;
import org.hzero.admin.config.SwaggerApiConfig;
import org.hzero.admin.domain.service.ConfigRefreshService;
import org.hzero.admin.infra.feign.PermissionRefreshService;
import org.hzero.admin.infra.feign.SwaggerRefreshService;
import org.hzero.common.HZeroConstant;
import org.hzero.core.exception.NotLoginException;
import org.hzero.core.util.ResponseUtils;
import org.hzero.core.util.Results;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * @author XCXCXCXCX
 * @date 2019/9/12
 * @project hzero-admin
 */
@Api(tags = {SwaggerApiConfig.REFRESH})
@RestController("refreshController.v1")
@RequestMapping("/v1/refresh")
public class RefreshController {

    @Autowired
    private PermissionRefreshService permissionRefreshService;

    @Autowired
    private SwaggerRefreshService swaggerRefreshService;

    @Autowired
    private ServiceRouteRefreshService routeRefreshService;

    @Autowired
    private ServiceConfigRefreshService configRefreshService;

    @ApiOperation(value = "刷新权限")
    @Permission(permissionLogin = true)
    @PostMapping(value = "/permission")
    public ResponseEntity permission(@RequestParam(value = "serviceName") String serviceName,
                                     @RequestParam(value = "version", required = false) String version,
                                     @RequestParam(value = "cleanPermission", defaultValue = "false") Boolean cleanPermission) {
        checkSuperAdmin();
        permissionRefreshService.refresh(serviceName, version, cleanPermission);
        return Results.success();
    }

    @ApiOperation(value = "通知网关（刷新网关配置）")
    @Permission(permissionLogin = true)
    @PostMapping(value = "/notify-gateway")
    public ResponseEntity notifyGateway() {
        checkSuperAdmin();
        routeRefreshService.notifyGateway();
        return Results.success();
    }

    @ApiOperation(value = "拉取路由并通知网关（刷新网关配置）")
    @Permission(permissionLogin = true)
    @PostMapping(value = "/route")
    public ResponseEntity route(@RequestParam("serviceName") String serviceName,
                                @RequestParam(value = "version", required = false) String version) {
        checkSuperAdmin();
        routeRefreshService.refreshRouteAndNotifyGateway(serviceName, version);
        return Results.success();
    }

    @ApiOperation(value = "初始化路由扩展配置并通知网关（用于禁用网关的限流、熔断等配置）")
    @Permission(permissionLogin = true)
    @PostMapping(value = "/route/extend-config-init")
    public ResponseEntity extendConfigInit(@RequestParam("routeIds") List<Long> routeIds) {
        checkSuperAdmin();
        routeRefreshService.initRouteExtendConfigAndNotifyGateway(routeIds);
        return Results.success();
    }

    @ApiOperation(value = "刷新swagger文档信息")
    @Permission(permissionLogin = true)
    @PostMapping(value = "/swagger")
    public ResponseEntity swagger(@RequestParam("serviceName") String serviceName,
                                  @RequestParam("version") String version) {

        checkSuperAdmin();
        swaggerRefreshService.refresh(serviceName, version);
        return Results.success();
    }

    @ApiOperation(value = "刷新配置")
    @Permission(permissionLogin = true)
    @PostMapping(value = "/config")
    public ResponseEntity config(@RequestParam(value = "serviceName", required = false) String serviceName,
                                 @RequestParam(value = "version", required = false) String version) {
        checkSuperAdmin();
        configRefreshService.refresh(serviceName, version);
        return Results.success();
    }

    private void checkSuperAdmin() {
        CustomUserDetails self = Optional.ofNullable(DetailsHelper.getUserDetails()).orElseThrow(NotLoginException::new);
        RoleDTO role = ResponseUtils.getResponse(permissionRefreshService.getRole(self.getTenantId(), self.getRoleId()), RoleDTO.class);
        if (!StringUtils.equalsAny(role.getCode(), HZeroConstant.RoleCode.SITE, HZeroConstant.RoleCode.TENANT)) {
            throw new CommonException("hadm.warn.operation.onlySupperAdminAllowed");
        }
    }

}
