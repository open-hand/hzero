package org.hzero.iam.api.controller.v1;

import java.util.List;
import java.util.Set;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.api.dto.PermissionSetSearchDTO;
import org.hzero.iam.app.service.RoleService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.Menu;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.domain.vo.RoleVO;
import org.hzero.iam.infra.constant.RolePermissionType;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 创建角色、继承角色创建、复制角色创建，查询角色创建树、查询角色继承树
 *
 * @since 2019/01/25 重大调整：角色直接和权限集关联，去掉权限这一层。
 * @author bojiangzhou 2018/06/19
 * @see RoleController
 */
@Api(tags = SwaggerApiConfig.ROLE_SITE)
@RestController("hiamRoleSiteController.v1")
@RequestMapping("/hzero/v1/roles")
public class RoleSiteController extends BaseController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleRepository roleRepository;

    //
    // 角色查询
    // ------------------------------------------------------------------------------

    @ApiOperation("角色管理 - 通过角色ID查询角色详细信息")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{roleId}")
    public ResponseEntity<RoleVO> queryRoleDetails(@Encrypt @PathVariable Long roleId) {
        return Results.success(roleRepository.selectRoleDetails(roleId));
    }

    @ApiOperation("角色查询 - 分页查询租户下的角色")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/paging")
    public ResponseEntity<Page<RoleVO>> paging(RoleVO params,
                                               PageRequest pageRequest) {
        return Results.success(roleRepository.selectSimpleRolesWithTenant(params, pageRequest));
    }

    //
    // 角色管理
    // ------------------------------------------------------------------------------

    @ApiOperation(value = "角色管理 - 直接创建角色")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping()
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        validObject(role);
        roleService.createRole(role, false, false);
        return Results.success();
    }

    @ApiOperation(value = "角色管理 - 继承并创建角色")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/inherit")
    public ResponseEntity<Role> createInheritedRole(@RequestBody Role role) {
        validObject(role);
        role = roleService.createRole(role, true, false);

        return Results.success(role);
    }

    @ApiOperation(value = "角色管理 - 复制并创建角色")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/copy")
    public ResponseEntity<Role> createDuplicateRole(@RequestBody Role role) {
        validObject(role);
        role = roleService.createRole(role, false, true);
        return Results.success(role);
    }

    @ApiOperation(value = "角色管理 - 修改角色")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<Role> updateRole(@RequestBody Role role) {
        validObject(role);
        SecurityTokenHelper.validToken(role, false);
        return Results.success(roleService.updateRole(role));
    }

    /**
     * 启用角色
     */
    @ApiOperation(value = "角色管理 - 启用角色")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping(value = "/enable")
    public ResponseEntity<Void> enable(@RequestBody Role role) {
        SecurityTokenHelper.validToken(role);
        roleService.enableRole(role.getId());
        return Results.success();
    }

    /**
     * 禁用角色
     */
    @ApiOperation(value = "角色管理 - 禁用角色")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping(value = "/disable")
    public ResponseEntity<Void> disable(@RequestBody Role role) {
        SecurityTokenHelper.validToken(role);
        roleService.disableRole(role.getId());
        return Results.success();
    }

    //
    // 角色权限分配
    // ------------------------------------------------------------------------------

    @ApiOperation(value = "权限分配 - 查询角色可分配的权限集树")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping(value = "/{roleId}/permission-set-tree")
    public ResponseEntity<List<Menu>> listPermissionSetTree(@Encrypt @PathVariable Long roleId,
                                                            @ModelAttribute PermissionSetSearchDTO permissionSetParam) {
        return Results.success(roleRepository.selectRolePermissionSetTree(roleId, permissionSetParam));
    }


    @ApiOperation(value = "权限分配 - 分配角色权限集")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping(value = "/{roleId}/permission-sets/assign")
    public ResponseEntity<Void> assignRolePermissionSets(@Encrypt @PathVariable Long roleId,
                                                   @RequestBody @Encrypt Set<Long> permissionSetIds) {
        roleService.assignRolePermissionSets(roleId, permissionSetIds, RolePermissionType.PS.name());
        return Results.success();
    }

    @ApiOperation(value = "权限分配 - 回收角色权限集")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping(value = "/{roleId}/permission-sets/recycle")
    public ResponseEntity<Void> recycleRolePermissionSets(@Encrypt @PathVariable Long roleId,
                                                    @RequestBody @Encrypt Set<Long> permissionSetIds) {
        roleService.recycleRolePermissionSets(roleId, permissionSetIds, RolePermissionType.PS.name());
        return Results.success();
    }

    @ApiOperation("查询租户管理员角色列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/roles/tenant-admin")
    public ResponseEntity<List<Role>> listTenantAdmin() {
        return Results.success(roleService.listTenantAdmin(null));
    }
}
