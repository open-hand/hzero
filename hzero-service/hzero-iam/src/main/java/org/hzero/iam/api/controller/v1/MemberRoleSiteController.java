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

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
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
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.constant.HiamMemberType;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author allen
 */
@Api(tags = SwaggerApiConfig.MEMBER_ROLE_SITE)
@RestController("memberRoleSiteController.v1")
@RequestMapping(value = "/hzero/v1/member-roles")
public class MemberRoleSiteController extends BaseController {

    @Autowired
    private MemberRoleService memberRoleService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClientRepository clientRepository;


    @ApiOperation(value = "子账户 - 分页查询分配给用户的角色")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/user-roles/{userId}")
    public ResponseEntity<Page<RoleVO>> listUserRoles(@Encrypt @PathVariable Long userId,
                                                      MemberRoleSearchDTO memberRoleSearchDTO,
                                                      PageRequest pageRequest) {
        return Results.success(roleRepository.selectMemberRoles(userId, HiamMemberType.USER, memberRoleSearchDTO, pageRequest));
    }

    @ApiOperation(value = "子账户 - 查询所有分配给用户的角色")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/user-all-roles/{userId}")
    public ResponseEntity<List<RoleVO>> listAllUserRoles(@Encrypt @PathVariable Long userId,
                                                      MemberRoleSearchDTO memberRoleSearchDTO) {
        return Results.success(roleRepository.selectMemberRoles(userId, HiamMemberType.USER, memberRoleSearchDTO));
    }

    @ApiOperation(value = "客户端 - 分页查询分配给客户端的角色")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/client-roles/{clientId}")
    public ResponseEntity<Page<RoleVO>> listClientRoles(@Encrypt @PathVariable Long clientId,
                                                        MemberRoleSearchDTO memberRoleSearchDTO,
                                                        PageRequest pageRequest) {
        return Results.success(roleRepository.selectMemberRoles(clientId, HiamMemberType.CLIENT, memberRoleSearchDTO, pageRequest));
    }

    @ApiOperation(value = "客户端 - 查询所有分配给客户端的角色")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/client-all-roles/{clientId}")
    public ResponseEntity<List<RoleVO>> listAllClientRoles(@Encrypt @PathVariable Long clientId,
                                                        MemberRoleSearchDTO memberRoleSearchDTO) {
        return Results.success(roleRepository.selectMemberRoles(clientId, HiamMemberType.CLIENT, memberRoleSearchDTO));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "角色管理 - 查询角色已分配的用户")
    @GetMapping("/role-users/{roleId}")
    public ResponseEntity<Page<UserVO>> listRoleMembers(@Encrypt @PathVariable(name = "roleId") Long roleId,
                                                        @Encrypt MemberRoleSearchDTO memberRoleSearchDTO,
                                                        PageRequest pageRequest) {
        return Results.success(userRepository.selectRoleUsers(roleId, memberRoleSearchDTO, pageRequest));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "角色管理 - 查询角色已分配的客户端")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/role-clients/{roleId}")
    public ResponseEntity<Page<Client>> listClientMembers(@Encrypt @PathVariable(name = "roleId") Long roleId,
                                                          String name,
                                                          PageRequest pageRequest) {
        return Results.success(clientRepository.selectRoleClients(roleId, name, pageRequest));
    }

    //
    // 分配
    // ------------------------------------------------------------------------------
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "角色分配 - 批量分配成员角色")
    @PostMapping("/batch-assign")
    public ResponseEntity<List<MemberRole>> batchAssignMemberRole(@RequestBody List<MemberRole> memberRoleList) {
        return Results.success(memberRoleService.batchAssignMemberRole(memberRoleList));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "角色管理 - 批量删除成员角色")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<Void> batchDeleteMemberRole(@RequestBody List<MemberRole> memberRoleList) {
        memberRoleService.batchDeleteMemberRole(Constants.SITE_TENANT_ID, memberRoleList);
        return Results.success();
    }

    @ApiOperation(value = "角色管理 - 取消分配角色成员")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity<Void> revokeMemberRoles(@RequestBody List<MemberRole> memberRoleList) {
        memberRoleService.batchDeleteMemberRole(Constants.SITE_TENANT_ID, memberRoleList);
        return Results.success();
    }

}
