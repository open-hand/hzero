package org.hzero.iam.api.controller.v1;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;

import org.hzero.core.util.Results;
import org.hzero.iam.api.dto.TenantPermissionDTO;
import org.hzero.iam.app.service.PermissionService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.repository.PermissionRepository;
import org.hzero.iam.domain.vo.PermissionVO;
import org.hzero.mybatis.helper.SecurityTokenHelper;

/**
 * API管理(平台级)
 *
 * @author KAIBING.JIANG@HAND-CHINA.COM 2019/12/3
 */
@Api(tags = SwaggerApiConfig.PERMISSION_SITE)
@RestController("permissionSiteController.v1")
@RequestMapping(value = "/hzero/v1/permissions")
public class PermissionSiteController extends BaseController {

    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private PermissionService permissionService;

    /**
     * @deprecated 历史接口，先保留
     */
    @Deprecated
    @ApiOperation(value = "分页查询权限明细")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/page")
    public ResponseEntity<Page<PermissionVO>> pagePermission(String condition, String level, PageRequest pageRequest) {
        return Results.success(permissionRepository.pagePermission(condition, level, pageRequest));
    }

    /**
     * @deprecated 历史接口，先保留
     */
    @Deprecated
    @ApiOperation("通过编码查询接口权限，如果用户没有权限访问接口查询不到")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/code")
    public ResponseEntity<PermissionVO> queryPermissionByCode(@RequestParam String permissionCode, @RequestParam(required = false) String level) {
        return Results.success(permissionRepository.queryPermissionByCode(permissionCode, level));
    }

    @ApiOperation(value = "分页查询API")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<Page<PermissionVO>> pageApi(PermissionVO params, PageRequest pageRequest) {
        return Results.success(permissionService.pageApis(params, pageRequest));
    }

    @ApiOperation("批量删除api")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity<Void> batchDeleteApi(@RequestBody List<org.hzero.iam.domain.entity.Permission> permissions) {
        SecurityTokenHelper.validToken(permissions);
        permissionService.deleteApis(permissions);
        return Results.success();
    }

    @ApiOperation("批量修改API")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<List<org.hzero.iam.domain.entity.Permission>> updatePermission(@RequestBody List<org.hzero.iam.domain.entity.Permission> permissions) {
        SecurityTokenHelper.validToken(permissions);
        validList(permissions);
        permissionService.updateApis(permissions);
        return Results.success(permissions);
    }

    @ApiOperation("分配权限给多个租户")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/assign")
    public ResponseEntity<Void> assignPermission(@RequestBody TenantPermissionDTO tenantPermission) {
        permissionService.assignTenantApis(tenantPermission.getTenantIds(), tenantPermission.getPermissions());
        return Results.success();
    }

    @ApiOperation("分页查询租户API")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/tenant")
    public ResponseEntity<Page<PermissionVO>> pageTenantApi(PermissionVO params, PageRequest pageRequest) {
        return Results.success(permissionService.pageTenantApis(params, pageRequest));
    }

    @ApiOperation("分页查询租户可分配的API")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/assignable/{tenantId}")
    public ResponseEntity<Page<PermissionVO>> pageTenantAssignableApi(@PathVariable Long tenantId, PermissionVO params, PageRequest pageRequest) {
        params.setTenantId(tenantId);
        return Results.success(permissionRepository.pageTenantAssignableApis(params, pageRequest));
    }

    @ApiOperation("回收租户的API权限")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping("/revoke")
    public ResponseEntity<Void> revokePermission(@RequestBody List<org.hzero.iam.domain.entity.Permission> permissions) {
        SecurityTokenHelper.validToken(permissions);
        permissionService.deleteTenantApis(permissions);
        return Results.success();
    }

    @ApiOperation("修改API")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping("/update")
    public ResponseEntity<org.hzero.iam.domain.entity.Permission> updateOnePermission(@RequestBody org.hzero.iam.domain.entity.Permission permission) {
        SecurityTokenHelper.validToken(permission);
        validObject(permission);
        permissionService.updateApi(permission);
        return Results.success(permission);
    }

}
