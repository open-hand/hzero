package org.hzero.iam.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.swagger.annotation.Permission;

import org.hzero.core.util.Results;
import org.hzero.iam.api.dto.RolePermissionAssignDTO;
import org.hzero.iam.app.service.IDocumentService;
import org.hzero.iam.app.service.impl.RoleDataFixService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.infra.common.utils.UserUtils;

/**
 * 权限工具接口
 *
 * @author bojiangzhou
 */
@Api(tags = SwaggerApiConfig.TOOL_PERMISSION)
@RequestMapping("/v1/tool/permission")
@RestController("toolPermissionController.v1")
public class ToolPermissionController {

    @Autowired
    private RoleDataFixService roleDataFixService;
    @Autowired
    private IDocumentService documentService;


    @ApiOperation(value = "角色继承树分配权限，不传角色编码则默认处理内置角色")
    @Permission(permissionLogin = true)
    @PostMapping("/assign-inherit-role")
    public ResponseEntity<Void> initRoleOfInheritSubRoleTreePss(RolePermissionAssignDTO rolePermissionAssignDTO) {
        UserUtils.checkSuperAdmin();
        roleDataFixService.initRoleOfInheritSubRoleTreePss(rolePermissionAssignDTO);
        return Results.success();
    }

    @ApiOperation("手动刷新表IAM权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "serviceName", value = "服务名", paramType = "query"),
            @ApiImplicitParam(name = "metaVersion", value = "服务标记版本", paramType = "query"),
            @ApiImplicitParam(name = "cleanPermission", value = "是否清除过期权限", paramType = "query"),
    })
    @Permission(permissionLogin = true)
    @PostMapping(value = "/fresh")
    public ResponseEntity<Void> refresh(@RequestParam("serviceName") String serviceName,
                                        @RequestParam(value = "metaVersion", required = false, defaultValue = IDocumentService.NULL_VERSION) String metaVersion,
                                        @RequestParam(value = "cleanPermission", required = false) Boolean cleanPermission) {
        UserUtils.checkSuperAdmin();
        documentService.refreshPermission(serviceName, metaVersion, cleanPermission);
        return Results.success();
    }

    @ApiOperation("刷新表IAM权限(供内部调用)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "serviceName", value = "服务名", paramType = "query"),
            @ApiImplicitParam(name = "metaVersion", value = "服务标记版本", paramType = "query"),
            @ApiImplicitParam(name = "cleanPermission", value = "是否清除过期权限", paramType = "query"),
    })
    @Permission(permissionWithin = true)
    @PostMapping(value = "/inner/fresh")
    public ResponseEntity<Void> innerRefresh(@RequestParam("serviceName") String serviceName,
                                             @RequestParam(value = "metaVersion", required = false, defaultValue = IDocumentService.NULL_VERSION) String metaVersion,
                                             @RequestParam(value = "cleanPermission", required = false) Boolean cleanPermission) {
        documentService.refreshPermissionAsync(serviceName, metaVersion, cleanPermission);
        return Results.success();
    }

}
