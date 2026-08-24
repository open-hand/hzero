package org.hzero.iam.api.controller.v1;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;

import org.hzero.core.base.BaseController;
import org.hzero.core.exception.NotLoginException;
import org.hzero.core.util.Results;
import org.hzero.iam.api.dto.PermissionSetSearchDTO;
import org.hzero.iam.api.dto.RoleDTO;
import org.hzero.iam.api.dto.RolePermissionCheckDTO;
import org.hzero.iam.app.service.RoleService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.Menu;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.RolePermission;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.domain.vo.RoleVO;
import org.hzero.iam.infra.constant.RolePermissionType;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 创建角色、继承角色创建、复制角色创建，查询角色创建树、查询角色继承树
 *
 * @author bojiangzhou 2018/06/19
 * @since 2019/04/24
 * 调整：
 * 1.角色管理页面，查询当前登录用户已分配的角色及其子孙角色；
 * 2.已分配的角色中，拥有角色创建功能的可以作为父级角色创建下级角色
 * 3.只能管理角色为平台超级管理员时可创建租户管理员，其它的角色由上级角色决定
 * @since 2019/04/16 调整：租户层只能维护租户自己的角色，不能维护其它租户的角色
 * @since 2019/01/25 重大调整：角色直接和权限集关联，去掉权限这一层。
 */
@Api(tags = SwaggerApiConfig.ROLE)
@RestController("roleController.v1")
@RequestMapping("/hzero/v1")
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleRepository roleRepository;

    //
    // 角色查询
    // ------------------------------------------------------------------------------
    @ApiOperation("角色查询 - 查询当前用户自己的角色")
    @Permission(permissionLogin = true)
    @GetMapping("/{organizationId}/roles/self/roles")
    public ResponseEntity<List<Role>> listSelfRole(@PathVariable("organizationId") Long organizationId) {
        return Results.success(roleService.selectUserRole(organizationId, Optional.ofNullable(DetailsHelper.getUserDetails())
                .orElseThrow(NotLoginException::new)
                .getUserId()));
    }

    @ApiOperation("角色管理 - 分页查询当前用户可管理的所有(子孙)角色")
    @Permission(permissionLogin = true)
    @GetMapping("/roles/self/manageable-roles")
    public ResponseEntity<Page<RoleVO>> listSelfManageableRoles(@Encrypt RoleVO params,
                                                                PageRequest pageRequest) {
        return Results.success(roleService.selectSelfManageableRoles(params, pageRequest));
    }

    @ApiOperation("角色管理 - 树形查询当前用户可管理的所有(子孙)角色")
    @Permission(permissionLogin = true)
    @GetMapping("/roles/self/manageable-roles/tree")
    public ResponseEntity<List<RoleDTO>> listTreeSelfManageableRoles(RoleVO params) {
        return Results.success(roleService.listTreeList(params));
    }

    @ApiOperation("角色管理 - 通过父级节点查可见孩子节点")
    @Permission(permissionLogin = true)
    @GetMapping("/roles/self/manageable-roles/tree/sub")
    public ResponseEntity<Page<RoleDTO>> treeSelfManageableRoles(@Encrypt RoleVO params, PageRequest pageRequest) {
        return Results.success(roleRepository.selectUserManageableRoleTree(pageRequest, params));
    }

    @ApiOperation("角色管理 - 查询当前用户的管理角色")
    @Permission(permissionLogin = true)
    @GetMapping("/roles/self/admin-roles")
    public ResponseEntity<List<RoleVO>> listSelfAdminRoles(RoleVO params,
                                                           PageRequest pageRequest) {
        return Results.success(roleRepository.selectUserAdminRoles(params, pageRequest));
    }

    @ApiOperation("角色管理 - 查询当前用户分配的角色")
    @Permission(permissionLogin = true)
    @GetMapping("/roles/self/assigned-roles")
    public ResponseEntity<Page<RoleVO>> listSelfAssignedRoles(RoleVO params, PageRequest pageRequest) {
        return Results.success(roleRepository.selectSelfAssignedRoles(params, pageRequest));
    }

    @ApiOperation("用户分配角色 - 分页查询当前用户可管理的角色，通过 excludeUserIds 传入当前分配用户，通过 excludeRoleIds 传入排除的角色")
    @Permission(permissionLogin = true)
    @GetMapping("/roles/self/user-assignable-roles")
    public ResponseEntity<Page<RoleVO>> listUserAssignableRoles(@Encrypt RoleVO params,
                                                                PageRequest pageRequest) {
        return Results.success(roleRepository.selectSelfAssignableRoles(params, pageRequest));
    }

    @ApiOperation("角色管理 - 通过角色ID查询角色详细信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/roles/{roleId}")
    public ResponseEntity<RoleVO> queryRoleDetails(@PathVariable("organizationId") Long organizationId,
                                                   @Encrypt @PathVariable Long roleId) {
        return Results.success(roleService.selectRoleDetails(roleId));
    }

    @ApiOperation("角色查询 - 分页查询租户下的角色")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/roles/paging")
    public ResponseEntity<Page<RoleVO>> paging(@PathVariable("organizationId") Long tenantId,
                                               RoleVO params,
                                               PageRequest pageRequest) {
        params.setTenantId(tenantId);
        return Results.success(roleRepository.selectSimpleRolesWithTenant(params, pageRequest));
    }

    //
    // 角色管理
    // ------------------------------------------------------------------------------

    @ApiOperation(value = "角色管理 - 直接创建角色")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{organizationId}/roles")
    public ResponseEntity<Role> createRole(@PathVariable("organizationId") Long tenantId,
                                           @RequestBody Role role) {
        if (role.getTenantId() == null) {
            role.setTenantId(tenantId);
        }
        validObject(role);
        roleService.createRole(role, false, false);
        return Results.success();
    }

    @ApiOperation(value = "角色管理 - 继承并创建角色")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{organizationId}/roles/inherit")
    public ResponseEntity<Role> createInheritedRole(@PathVariable("organizationId") Long tenantId,
                                                    @RequestBody Role role) {
        if (role.getTenantId() == null) {
            role.setTenantId(tenantId);
        }
        validObject(role);
        role = roleService.createRole(role, true, false);

        return Results.success(role);
    }

    @ApiOperation(value = "角色管理 - 复制并创建角色")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{organizationId}/roles/copy")
    public ResponseEntity<Role> createDuplicateRole(@PathVariable("organizationId") Long tenantId,
                                                    @RequestBody Role role) {
        if (role.getTenantId() == null) {
            role.setTenantId(tenantId);
        }
        validObject(role);
        role = roleService.createRole(role, false, true);
        return Results.success(role);
    }

    @ApiOperation(value = "角色管理 - 修改角色")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/{organizationId}/roles")
    public ResponseEntity<Role> updateRole(@PathVariable("organizationId") Long tenantId,
                                           @RequestBody Role role) {
        if (role.getTenantId() == null) {
            role.setTenantId(tenantId);
        }
        validObject(role);
        SecurityTokenHelper.validToken(role, false);
        return Results.success(roleService.updateRole(role));
    }

    /**
     * 启用角色
     */
    @ApiOperation(value = "角色管理 - 启用角色")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping(value = "/{organizationId}/roles/enable")
    public ResponseEntity<Void> enable(@PathVariable("organizationId") Long tenantId,
                                       @RequestBody Role role) {
        if (role.getTenantId() == null) {
            role.setTenantId(tenantId);
        }
        SecurityTokenHelper.validToken(role);
        roleService.enableRole(role.getId());
        return Results.success();
    }

    /**
     * 禁用角色
     */
    @ApiOperation(value = "角色管理 - 禁用角色")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping(value = "/{organizationId}/roles/disable")
    public ResponseEntity<Void> disable(@PathVariable("organizationId") Long tenantId,
                                        @RequestBody Role role) {
        if (role.getTenantId() == null) {
            role.setTenantId(tenantId);
        }
        SecurityTokenHelper.validToken(role);
        roleService.disableRole(role.getId());
        return Results.success();
    }

    //
    // 角色权限分配
    // ------------------------------------------------------------------------------

    @ApiOperation(value = "权限分配 - 查询角色可分配的权限集树")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping(value = "/{organizationId}/roles/{roleId}/permission-set-tree")
    public ResponseEntity<List<Menu>> listPermissionSetTree(@PathVariable(value = "organizationId") Long tenantId,
                                                            @Encrypt @PathVariable Long roleId,
                                                            @ModelAttribute PermissionSetSearchDTO permissionSetParam) {
        return Results.success(roleRepository.selectRolePermissionSetTree(roleId, permissionSetParam));
    }


    @ApiOperation(value = "权限分配 - 分配角色权限集")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping(value = "/{organizationId}/roles/{roleId}/permission-sets/assign")
    public ResponseEntity<Void> assignRolePermissionSets(@PathVariable(value = "organizationId") Long tenantId,
                                                         @Encrypt @PathVariable(value = "roleId") Long roleId,
                                                         @RequestBody @Encrypt Set<Long> permissionSetIds) {
        roleService.assignRolePermissionSets(roleId, permissionSetIds, RolePermissionType.PS.name());
        return Results.success();
    }

    @ApiOperation(value = "权限分配 - 回收角色权限集")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping(value = "/{organizationId}/roles/{roleId}/permission-sets/recycle")
    public ResponseEntity<Void> recycleRolePermissionSets(@PathVariable(value = "organizationId") Long tenantId,
                                                          @Encrypt @PathVariable(value = "roleId") Long roleId,
                                                          @Encrypt @RequestBody Set<Long> permissionSetIds) {
        roleService.recycleRolePermissionSets(roleId, permissionSetIds, RolePermissionType.PS.name());
        return Results.success();
    }


    //
    // 角色权限分配(内部接口调用)
    // ------------------------------------------------------------------------------

    @ApiOperation(value = "权限检查 - 检查特定类型的权限集是否在特定检查范围内可用 (内部调用)")
    @Permission(permissionWithin = true)
    @GetMapping(value = "/roles/permissions/check")
    public ResponseEntity<Boolean> checkPermission(RolePermissionCheckDTO rolePermissionCheckDTO) {
        return Results.success(roleRepository.checkPermission(rolePermissionCheckDTO));
    }

    @ApiOperation(value = "权限分配 - 查询角色已分配分配的权限，需传入roleId，type，可传入 permissionSetIds 来查询包含的权限 (内部调用)")
    @Permission(permissionWithin = true)
    @GetMapping(value = "/roles/permissions/all")
    public ResponseEntity<List<RolePermission>> listRolePermissions(RolePermission params) {
        return Results.success(roleRepository.selectRolePermissions(params));
    }

    @ApiOperation(value = "权限分配 - 给角色挂权限(内部调用)")
    @Permission(permissionWithin = true)
    @PostMapping(value = "/roles/permissions/assign-direct")
    public ResponseEntity<Void> directAssignRolePermission(@RequestBody RolePermission rolePermission) {
        roleService.directAssignRolePermission(rolePermission);
        return Results.success();
    }


    @ApiOperation(value = "权限分配 - 给角色分配权限(内部调用)")
    @Permission(permissionWithin = true)
    @PutMapping(value = "/roles/{roleId}/permissions/assign")
    public ResponseEntity<Void> assignRolePermissions(@Encrypt @PathVariable(value = "roleId") Long roleId,
                                                      @RequestParam(value = "type") String type,
                                                      @RequestBody @Encrypt Set<Long> permissionIds) {
        roleService.assignRolePermissionSets(roleId, permissionIds, type);
        return Results.success();
    }

    @ApiOperation(value = "权限分配 - 回收角色权限(内部调用)")
    @Permission(permissionWithin = true)
    @PutMapping(value = "/roles/{roleId}/permissions/recycle")
    public ResponseEntity<Void> recycleRolePermissions(@Encrypt @PathVariable(value = "roleId") Long roleId,
                                                       @RequestParam(value = "type") String type,
                                                       @RequestBody @Encrypt Set<Long> permissionIds) {
        roleService.recycleRolePermissionSets(roleId, permissionIds, type);
        return Results.success();
    }

    @ApiOperation("查询租户管理员角色列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/roles/tenant-admin")
    public ResponseEntity<List<Role>> listTenantAdmin(@PathVariable long organizationId) {
        return Results.success(roleService.listTenantAdmin(organizationId));
    }

    @ApiOperation("角色管理 - 查询角色的父级角色")
    @Permission(permissionLogin = true)
    @GetMapping("/roles/admin-role/{roleId}")
    @Deprecated
    public ResponseEntity<RoleVO> queryAdminRoleSimple(@Encrypt @PathVariable Long roleId) {
        return Results.success(roleRepository.selectAdminRole(roleId));
    }

}
