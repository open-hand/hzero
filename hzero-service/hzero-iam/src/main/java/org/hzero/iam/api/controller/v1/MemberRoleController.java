package org.hzero.iam.api.controller.v1;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.api.dto.MemberRoleAssignDTO;
import org.hzero.iam.api.dto.MemberRoleSearchDTO;
import org.hzero.iam.app.service.MemberRoleService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.Client;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.repository.ClientRepository;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.domain.vo.RoleVO;
import org.hzero.iam.domain.vo.UserVO;
import org.hzero.iam.infra.constant.HiamMemberType;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author allen
 * @since bojiangzhou 2019/04/24 调整：查询当前用户[已分配]的角色，及其子孙角色，不区分层级.并将 [分配的角色] 且 [拥有创建角色功能] 的角色标识可作为父级角色.
 */
@Api(tags = SwaggerApiConfig.MEMBER_ROLE)
@RestController("memberRoleController.v1")
@RequestMapping(value = "/hzero/v1")
public class MemberRoleController extends BaseController {

    @Autowired
    private MemberRoleService memberRoleService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClientRepository clientRepository;

    //
    // 成员角色查询
    // ------------------------------------------------------------------------------
    @ApiOperation(value = "子账户 - 分页查询分配给用户的角色")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/member-roles/user-roles/{userId}")
    public ResponseEntity<Page<RoleVO>> listUserRoles(@PathVariable Long organizationId,
                                                      @Encrypt @PathVariable Long userId,
                                                      @Encrypt MemberRoleSearchDTO memberRoleSearchDTO,
                                                      PageRequest pageRequest) {
        return Results.success(roleRepository.selectMemberRoles(userId, HiamMemberType.USER, memberRoleSearchDTO, pageRequest));
    }

    @ApiOperation(value = "子账户 - 查询所有分配给用户的角色")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/member-roles/user-all-roles/{userId}")
    public ResponseEntity<List<RoleVO>> listAllUserRoles(@PathVariable Long organizationId,
                                                      @Encrypt @PathVariable Long userId,
                                                      @Encrypt MemberRoleSearchDTO memberRoleSearchDTO) {
        return Results.success(roleRepository.selectMemberRoles(userId, HiamMemberType.USER, memberRoleSearchDTO));
    }

    @ApiOperation(value = "客户端 - 分页查询分配给客户端的角色")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/member-roles/client-roles/{clientId}")
    public ResponseEntity<Page<RoleVO>> listClientRoles(@PathVariable Long organizationId,
                                                        @Encrypt @PathVariable Long clientId,
                                                        @Encrypt MemberRoleSearchDTO memberRoleSearchDTO,
                                                        PageRequest pageRequest) {
        return Results.success(roleRepository.selectMemberRoles(clientId, HiamMemberType.CLIENT, memberRoleSearchDTO, pageRequest));
    }

    @ApiOperation(value = "客户端 - 查询所有分配给客户端的角色")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/member-roles/client-all-roles/{clientId}")
    public ResponseEntity<List<RoleVO>> listAllClientRoles(@PathVariable Long organizationId,
                                                        @Encrypt @PathVariable Long clientId,
                                                        @Encrypt MemberRoleSearchDTO memberRoleSearchDTO) {
        return Results.success(roleRepository.selectMemberRoles(clientId, HiamMemberType.CLIENT, memberRoleSearchDTO));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "角色管理 - 查询角色已分配的用户")
    @GetMapping("/{organizationId}/member-roles/role-users/{roleId}")
    public ResponseEntity<Page<UserVO>> listRoleMembers(@PathVariable(name = "organizationId") Long organizationId,
                                                        @Encrypt @PathVariable(name = "roleId") Long roleId,
                                                        @Encrypt MemberRoleSearchDTO memberRoleSearchDTO,
                                                        PageRequest pageRequest) {
        return Results.success(userRepository.selectRoleUsers(roleId, memberRoleSearchDTO, pageRequest));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "角色管理 - 查询角色已分配的客户端")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/{organizationId}/member-roles/role-clients/{roleId}")
    public ResponseEntity<Page<Client>> listClientMembers(@PathVariable(name = "organizationId") Long organizationId,
                                                          @Encrypt @PathVariable(name = "roleId") Long roleId,
                                                          String name,
                                                          PageRequest pageRequest) {
        return Results.success(clientRepository.selectRoleClients(roleId, name, pageRequest));
    }

    @ApiOperation(value = "当前用户 - 查询登录用户当前租户下的角色")
    @Permission(permissionLogin = true)
    @GetMapping("/member-roles/self-roles")
    public ResponseEntity<List<RoleVO>> listSelfRoles(@RequestParam(required = false) Boolean notMerge) {
        return Results.success(roleRepository.selectSelfCurrentTenantRoles(notMerge));
    }

    /**
     * 查询当前登录用户默认角色<br/>
     */
    @ApiOperation("当前用户 - 查询当前登录用户默认角色")
    @Permission(permissionLogin = true)
    @GetMapping("/member-roles/current-role")
    public ResponseEntity<RoleVO> getCurrentRole() {
        return Results.success(roleRepository.selectCurrentRole());
    }

    //
    // 分配
    // ------------------------------------------------------------------------------
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "角色分配 - 批量分配成员角色")
    @PostMapping("/{organizationId}/member-roles/batch-assign")
    public ResponseEntity<List<MemberRole>> batchAssignMemberRole(@PathVariable(name = "organizationId") Long organizationId,
                                                                  @RequestBody List<MemberRole> memberRoleList) {
        return Results.success(memberRoleService.batchAssignMemberRole(memberRoleList));
    }

    @Permission(level = ResourceLevel.ORGANIZATION, permissionWithin = true)
    @ApiOperation(value = "角色分配 - 批量分配成员角色(内部调用，不检查角色是否属于当前用户)")
    @PostMapping("/{organizationId}/member-roles/batch-assign-internal")
    public ResponseEntity<List<MemberRole>> batchAssignMemberRoleInternal(@PathVariable(name = "organizationId") Long organizationId,
                                                                          @RequestBody List<MemberRole> memberRoleList) {
        return Results.success(memberRoleService.batchAssignMemberRoleInternal(memberRoleList));
    }

    @Permission(permissionWithin = true)
    @ApiOperation(value = "角色分配 - 批量分配成员角色至租户(内部调用)")
    @PostMapping("/member-roles/assign-to-tenant-level")
    public ResponseEntity<List<MemberRole>> batchAssignMemberRoleOnTenant(@RequestBody List<MemberRoleAssignDTO> memberRoleAssignDTOList) {
        return Results.success(memberRoleService.batchAssignMemberRoleOnTenant(memberRoleAssignDTOList));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "角色管理 - 批量删除成员角色")
    @DeleteMapping("/{organizationId}/member-roles/batch-delete")
    public ResponseEntity<Void> batchDeleteMemberRole(@PathVariable(name = "organizationId") Long organizationId,
                                                @RequestBody List<MemberRole> memberRoleList) {
        if (CollectionUtils.isEmpty(memberRoleList)) {
            return Results.success();
        }
        memberRoleService.batchDeleteMemberRole(organizationId, memberRoleList);
        return Results.success();
    }

    @ApiOperation(value = "角色管理 - 取消分配角色成员")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/{organizationId}/member-roles")
    public ResponseEntity<Void> revokeMemberRoles(@PathVariable Long organizationId,
                                            @RequestBody List<MemberRole> memberRoleList) {
        memberRoleService.batchDeleteMemberRole(organizationId, memberRoleList);
        return Results.success();
    }

}
