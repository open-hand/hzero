package org.hzero.iam.api.controller.v1;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.app.service.PermissionService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.repository.PermissionRepository;
import org.hzero.iam.domain.vo.PermissionVO;
import org.hzero.mybatis.helper.SecurityTokenHelper;

/**
 * API管理(租户级)
 *
 * @author bojiangzhou
 */
@Api(tags = SwaggerApiConfig.PERMISSION)
@RestController("permissionController.v1")
@RequestMapping(value = "/hzero/v1/{organizationId}/permissions")
public class PermissionController extends BaseController {

    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private PermissionService permissionService;

    /**
     * @deprecated 历史接口，先保留
     */
    @Deprecated
    @ApiOperation(value = "分页查询权限明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/page")
    public ResponseEntity<Page<PermissionVO>> pagePermission(@PathVariable Long organizationId,
                                                             String condition, String level, PageRequest pageRequest) {
        return Results.success(permissionRepository.pagePermission(condition, level, pageRequest));
    }

    /**
     * @deprecated 历史接口，先保留
     */
    @Deprecated
    @ApiOperation("通过编码查询接口权限，如果用户没有权限访问接口查询不到")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/code")
    public ResponseEntity<PermissionVO> queryPermissionByCode(@PathVariable Long organizationId,
                                                              @RequestParam String permissionCode, @RequestParam(required = false) String level) {
        return Results.success(permissionRepository.queryPermissionByCode(permissionCode, level));
    }

    @ApiOperation(value = "分页查询API")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<PermissionVO>> pageApi(@PathVariable Long organizationId,
                                                      PermissionVO params, PageRequest pageRequest) {
        return Results.success(permissionService.pageApis(params, pageRequest));
    }

    @ApiOperation("批量删除api")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<Void> batchDeleteApi(@PathVariable Long organizationId,
                                               @RequestBody List<org.hzero.iam.domain.entity.Permission> permissions) {
        SecurityTokenHelper.validToken(permissions);
        permissionService.deleteApis(permissions);
        return Results.success();
    }

    @ApiOperation("批量修改API")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<List<org.hzero.iam.domain.entity.Permission>> updatePermission(@PathVariable Long organizationId,
                                                                                         @RequestBody List<org.hzero.iam.domain.entity.Permission> permissions) {
        SecurityTokenHelper.validToken(permissions);
        validList(permissions);
        permissionService.updateApis(permissions);
        return Results.success(permissions);
    }

    @ApiOperation("修改API")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/update")
    public ResponseEntity<org.hzero.iam.domain.entity.Permission> updateOnePermission(@PathVariable Long organizationId,
                                                                                      @RequestBody org.hzero.iam.domain.entity.Permission permission) {
        SecurityTokenHelper.validToken(permission);
        validObject(permission);
        permissionService.updateApi(permission);
        return Results.success(permission);
    }

}
