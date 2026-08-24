package org.hzero.iam.api.controller.v1;

import java.util.List;

import org.hzero.core.util.Results;
import org.hzero.iam.api.dto.*;
import org.hzero.iam.app.service.SecGrpDclService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.Menu;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpAclDashboard;
import org.hzero.iam.domain.entity.SecGrpAclField;
import org.hzero.iam.domain.repository.*;
import org.hzero.iam.domain.service.secgrp.SecGrpAssignService;
import org.hzero.iam.domain.vo.RoleVO;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 安全组分配 API
 *
 * @author bojiangzhou 2020/02/12 代码优化
 * @author jianbo.li
 * @author xingxing.wu
 */
@Api(tags = SwaggerApiConfig.SEC_GRP_ROLE_ASSIGN_SITE)
@RestController("secGrpRoleAssignSiteController.v1")
@RequestMapping("/v1/sec-grp-role-assign")
public class SecGrpRoleAssignSiteController {

    @Autowired
    private SecGrpRepository secGrpRepository;
    @Autowired
    private SecGrpAclRepository aclRepository;
    @Autowired
    private SecGrpAclDashboardRepository dashboardRepository;
    @Autowired
    private SecGrpAclFieldRepository secGrpAclFieldRepository;
    @Autowired
    private SecGrpDclDimRepository secGrpDclDimRepository;
    @Autowired
    private SecGrpDclLineRepository secGrpDclLineRepository;
    @Autowired
    private SecGrpDclService secGrpDclService;
    @Autowired
    private SecGrpAssignService secGrpAssignService;

    //
    // 角色分配安全组
    // ------------------------------------------------------------------------------

    @ApiOperation(value = "角色分配安全组 - 查询角色已分配的安全组")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{roleId}/assigned-sec-grp")
    public ResponseEntity<Page<SecGrp>> listRoleAssignedSecGrp(
            @PathVariable @Encrypt Long roleId,
            @Encrypt SecGrpQueryDTO queryDTO,
            @ApiIgnore @SortDefault(value = SecGrp.FIELD_SEC_GRP_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(secGrpRepository.listRoleAssignedSecGrp(roleId, queryDTO, pageRequest));
    }

    @ApiOperation(value = "角色分配安全组 - 查询角色可分配的安全组")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{roleId}/assignable-sec-grp")
    public ResponseEntity<Page<SecGrp>> listRoleAssignableSecGrp(
            @PathVariable @Encrypt Long roleId,
            @Encrypt SecGrpQueryDTO queryDTO,
            @ApiIgnore @SortDefault(value = SecGrp.FIELD_SEC_GRP_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {

        return Results.success(secGrpRepository.listRoleAssignableSecGrp(roleId, queryDTO, pageRequest));
    }

    @ApiOperation(value = "角色分配安全组 - 给角色分配安全组")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/{roleId}/assign-sec-grp")
    public ResponseEntity<Void> roleAssignSecGrp(
            @PathVariable("roleId") @Encrypt Long roleId,
            @RequestBody @Encrypt List<Long> secGrpIds) {
        secGrpAssignService.roleAssignSecGrp(roleId, secGrpIds);
        return Results.success();
    }

    @ApiOperation(value = "角色分配安全组 - 把角色的安全组取消分配")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping("/{roleId}/recycle-sec-grp")
    public ResponseEntity<Void> roleRecycleSecGrp(
            @PathVariable("roleId") @Encrypt Long roleId,
            @RequestBody @Encrypt List<Long> secGrpIds) {
        secGrpAssignService.roleRecycleSecGrp(roleId, secGrpIds);
        return Results.success();
    }

    @ApiOperation(value = "角色分配安全组 - 角色屏蔽安全组权限")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/{roleId}/{secGrpId}/shield")
    public ResponseEntity<Void> shieldRoleSecGrpAuthority(
            @PathVariable("roleId") @Encrypt Long roleId,
            @PathVariable("secGrpId") @Encrypt Long secGrpId,
            @RequestParam(value = "authorityId") @Encrypt Long authorityId,
            @RequestParam(value = "authorityType") String authorityType) {

        secGrpAssignService.shieldRoleSecGrpAuthority(roleId, secGrpId, authorityId, authorityType);
        return Results.success();
    }

    @ApiOperation(value = "角色分配安全组 - 角色取消屏蔽安全组权限")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/{roleId}/{secGrpId}/cancel-shield")
    public ResponseEntity<Void> cancelShieldRoleSecGrpAuthority(
            @PathVariable("roleId") @Encrypt Long roleId,
            @PathVariable("secGrpId") @Encrypt Long secGrpId,
            @RequestParam(value = "authorityId") @Encrypt Long authorityId,
            @RequestParam(value = "authorityType") String authorityType) {
        secGrpAssignService.cancelShieldRoleSecGrpAuthority(roleId, secGrpId, authorityId, authorityType);
        return Results.success();
    }

    //
    // 角色安全组分配查询权限
    // ------------------------------------------------------------------------------

    @ApiOperation("角色安全组分配查询权限 - 访问权限")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{roleId}/{secGrpId}/acl")
    public ResponseEntity<List<Menu>> listRoleSecGrpAcl(
            @PathVariable @Encrypt Long roleId,
            @PathVariable @Encrypt Long secGrpId) {
        return Results.success(aclRepository.listRoleSecGrpAcl(roleId, secGrpId));
    }

    @ApiOperation("角色安全组分配查询权限 - 工作台配置")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{roleId}/{secGrpId}/dashboard")
    public ResponseEntity<Page<SecGrpAclDashboard>> listRoleSecGrpDashboard(
            @ApiParam("角色ID") @PathVariable @Encrypt Long roleId,
            @ApiParam("安全组ID") @PathVariable @Encrypt Long secGrpId,
            @Encrypt SecGrpAclDashboard queryDTO,
            @ApiIgnore PageRequest pageRequest) {
        return Results.success(dashboardRepository.listSecGrpDashboard(null, secGrpId, queryDTO, pageRequest));
    }

    @ApiOperation("角色安全组分配查询权限 - 权限API")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{roleId}/{secGrpId}/field-api")
    public ResponseEntity<Page<org.hzero.iam.domain.entity.Permission>> listRoleSecGrpApi(
            @ApiParam(value = "角色ID") @PathVariable @Encrypt Long roleId,
            @ApiParam(value = "安全组ID") @PathVariable @Encrypt Long secGrpId,
            @Encrypt SecGrpAclApiDTO queryDTO,
            @ApiIgnore PageRequest pageRequest) {
        return Results.success(secGrpAclFieldRepository.listAssignedSecGrpApi(null, secGrpId, queryDTO, pageRequest));
    }

    @ApiOperation("角色安全组分配查询权限 - 权限字段")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{roleId}/{secGrpId}/field-api/{permissionId}/fields")
    public ResponseEntity<Page<SecGrpAclField>> listRoleSecGrpApiField(
            @ApiParam(value = "角色ID") @PathVariable @Encrypt Long roleId,
            @ApiParam(value = "安全组ID") @PathVariable @Encrypt Long secGrpId,
            @ApiParam(value = "接口ID") @PathVariable @Encrypt Long permissionId,
            @Encrypt SecGrpAclFieldDTO queryDTO,
            @ApiIgnore PageRequest pageRequest) {
        return Results.success(secGrpAclFieldRepository.listRoleAssignedApiField(roleId, secGrpId, permissionId, queryDTO, pageRequest));
    }

    @ApiOperation("角色安全组分配查询权限 - 数据权限维度")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{roleId}/{secGrpId}/dim")
    public ResponseEntity<Page<SecGrpDclDimDTO>> listRoleSecGrpDim(
            @ApiParam("角色ID") @PathVariable @Encrypt Long roleId,
            @ApiParam("安全组ID") @PathVariable @Encrypt Long secGrpId,
            @Encrypt SecGrpDclDimDTO queryDTO, @ApiIgnore PageRequest pageRequest) {
        return Results.success(secGrpDclDimRepository.listSecGrpAssignedDim(null, secGrpId, queryDTO, pageRequest));
    }

    @ApiOperation("角色安全组分配查询权限 - 数据权限")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{roleId}/{secGrpId}/dcl")
    public ResponseEntity<SecGrpDclDTO> listRoleSecGrpDcl(
            @ApiParam(value = "安全组ID") @PathVariable @Encrypt Long secGrpId,
            @ApiParam(value = "角色ID") @PathVariable @Encrypt Long roleId,
            @Encrypt SecGrpDclQueryDTO queryDTO,
            @ApiIgnore PageRequest pageRequest) {
        return Results.success(secGrpDclService.queryRoleSecGrpDclAuthority(roleId, secGrpId, queryDTO, pageRequest));
    }

    //
    // 安全组分配到角色
    // ------------------------------------------------------------------------------

    @ApiOperation("安全组分配角色 - 查询安全组已分配的角色")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{secGrpId}/assigned-role")
    public ResponseEntity<List<RoleSecGrpDTO>> listSecGrpAssignedRole(
            @PathVariable("secGrpId") @Encrypt Long secGrpId,
            @Encrypt RoleSecGrpDTO queryDTO,
            @ApiIgnore PageRequest pageRequest) {

        return Results.success(secGrpRepository.listSecGrpAssignedRole(secGrpId, queryDTO, pageRequest));
    }

    @ApiOperation("安全组分配角色 - 查询安全组可以分配的角色")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{secGrpId}/assignable-role")
    public ResponseEntity<Page<RoleVO>> listSecGrpAssignableRole(
            @PathVariable("secGrpId") @Encrypt Long secGrpId,
            @Encrypt RoleSecGrpDTO queryDTO,
            @ApiIgnore PageRequest pageRequest) {

        return Results.success(secGrpRepository.listSecGrpAssignableRole(secGrpId, queryDTO, pageRequest));
    }


    @ApiOperation("安全组分配到角色 - 安全组分配给角色")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/{secGrpId}/assign-role")
    public ResponseEntity<Void> secGrpAssignRole(
            @PathVariable("secGrpId") @Encrypt Long secGrpId,
            @RequestBody @Encrypt List<Long> roleId) {
        secGrpAssignService.secGrpAssignRole(secGrpId, roleId);
        return Results.success();
    }

    @ApiOperation("安全组分配到角色 - 安全组取消分配角色")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping("/{secGrpId}/recycle-role")
    public ResponseEntity<Void> secGrpRecycleRole(
            @PathVariable("secGrpId") @Encrypt Long secGrpId,
            @RequestBody @Encrypt List<Long> roleId) {
        secGrpAssignService.secGrpRecycleRole(secGrpId, roleId);
        return Results.success();
    }

}
