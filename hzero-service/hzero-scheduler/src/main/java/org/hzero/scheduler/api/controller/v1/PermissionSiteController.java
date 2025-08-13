package org.hzero.scheduler.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.scheduler.app.service.ConcPermissionService;
import org.hzero.scheduler.config.SchedulerSwaggerApiConfig;
import org.hzero.scheduler.domain.entity.ConcPermission;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * 并发请求权限 管理 API
 *
 * @author shuangfei.zhu@hand-china.com 2019-01-31 09:47:02
 */
@Api(tags = SchedulerSwaggerApiConfig.PERMISSION_SITE)
@RestController("concPermissionSiteController.v1")
@RequestMapping("/v1/conc-permissions")
public class PermissionSiteController extends BaseController {

    private final ConcPermissionService permissionService;

    @Autowired
    public PermissionSiteController(ConcPermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @ApiOperation(value = "并发请求权限列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{concurrentId}")
    @CustomPageRequest
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户Id", paramType = "query")
    })
    public ResponseEntity<Page<ConcPermission>> pagePermission(@PathVariable @Encrypt Long concurrentId, Long tenantId,
                                                               @ApiIgnore @SortDefault(value = ConcPermission.FIELD_PERMISSION_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(permissionService.selectByConcurrentId(concurrentId, tenantId, false, pageRequest));
    }

    @ApiOperation(value = "创建并发请求权限")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<ConcPermission> createPermission(@RequestBody @Encrypt ConcPermission concPermission) {
        validObject(concPermission);
        return Results.success(permissionService.createPermission(concPermission));
    }

    @ApiOperation(value = "修改并发请求权限")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<ConcPermission> updatePermission(@RequestBody @Encrypt ConcPermission concPermission) {
        SecurityTokenHelper.validToken(concPermission);
        return Results.success(permissionService.updatePermission(concPermission));
    }
}
