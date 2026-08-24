package org.hzero.iam.api.controller.v1;

import java.util.List;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
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
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
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
@Api(tags = SwaggerApiConfig.SEC_GRP_USER_ASSIGN)
@RestController("secGrpUserAssignController.v1")
@RequestMapping("/v1/{organizationId}/sec-grp-user-assign")
public class SecGrpUserAssignController {

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
    // 子账户分配安全组
    // ------------------------------------------------------------------------------

    @ApiOperation("子账户分配安全组 - 查询用户已分配的安全组")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{userId}/assigned-sec-grp")
    public ResponseEntity<Page<SecGrp>> listUserAssignedSecGrp(
            @PathVariable("organizationId") Long tenantId,
            @PathVariable @Encrypt Long userId,
            @Encrypt SecGrpQueryDTO queryDTO,
            @ApiIgnore PageRequest pageRequest) {
        return Results.success(secGrpRepository.listUserAssignedSecGrp(userId, queryDTO, pageRequest));
    }

    @ApiOperation("子账户分配安全组 - 查询用户可分配的安全组")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{userId}/assignable-sec-grp")
    public ResponseEntity<Page<SecGrp>> listUserAssignableSecGrp(
            @PathVariable("organizationId") Long tenantId,
            @PathVariable @Encrypt Long userId,
            @Encrypt SecGrpQueryDTO queryDTO,
            @ApiIgnore PageRequest pageRequest) {
        return Results.success(secGrpRepository.listUserAssignableSecGrp(userId, queryDTO, pageRequest));
    }

    @ApiOperation("子账户分配安全组 - 给子账户分配安全组")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{userId}/assign-sec-grp")
    public ResponseEntity<Void> userAssignedSecGrp(
            @PathVariable @Encrypt Long userId,
            @RequestBody @Encrypt List<Long> secGrpIds) {
        secGrpAssignService.userAssignSecGrp(userId, secGrpIds);
        return Results.success();
    }

    @ApiOperation("子账户分配安全组 - 子账户取消分配安全组")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/{userId}/recycle-sec-grp")
    public ResponseEntity<Void> userRecycleSecGrp(
            @PathVariable @Encrypt Long userId,
            @RequestBody @Encrypt List<Long> secGrpIds) {
        secGrpAssignService.userRecycleSecGrp(userId, secGrpIds);
        return Results.success();
    }

    //
    // 用户安全组分配查询权限
    // ------------------------------------------------------------------------------

    @ApiOperation("用户安全组分配查询权限 - 访问权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{userId}/{secGrpId}/acl")
    public ResponseEntity<List<Menu>> listRoleSecGrpAcl(
            @PathVariable @Encrypt Long userId,
            @PathVariable @Encrypt Long secGrpId) {
        return Results.success(aclRepository.listSecGrpAssignableAcl(null, secGrpId));
    }

    @ApiOperation("用户安全组分配查询权限 - 工作台配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{userId}/{secGrpId}/dashboard")
    public ResponseEntity<Page<SecGrpAclDashboard>> listRoleSecGrpDashboard(
            @ApiParam("角色ID") @PathVariable @Encrypt Long userId,
            @ApiParam("安全组ID") @PathVariable @Encrypt Long secGrpId,
            SecGrpAclDashboard queryDTO,
            @ApiIgnore PageRequest pageRequest) {
        return Results.success(dashboardRepository.listSecGrpDashboard(null, secGrpId, queryDTO, pageRequest));
    }

    @ApiOperation("用户安全组分配查询权限 - 权限API")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{userId}/{secGrpId}/field-api")
    public ResponseEntity<Page<org.hzero.iam.domain.entity.Permission>> listUserSecGrpApi(
            @ApiParam(value = "用户ID") @PathVariable @Encrypt Long userId,
            @PathVariable @Encrypt Long secGrpId,
            @Encrypt SecGrpAclApiDTO secGrpAclApiSearch,
            @ApiIgnore @SortDefault(org.hzero.iam.domain.entity.Permission.FIELD_ID) PageRequest pageRequest) {
        return Results.success(secGrpAclFieldRepository.listAssignedSecGrpApi(null, secGrpId, secGrpAclApiSearch, pageRequest));
    }

    @ApiOperation("用户安全组分配查询权限 - 权限字段")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{userId}/{secGrpId}/field-api/{permissionId}/fields")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<Page<SecGrpAclField>> listUserSecGrpApiField(
            @ApiParam(value = "角色ID") @PathVariable @Encrypt Long userId,
            @ApiParam(value = "安全组ID") @PathVariable @Encrypt Long secGrpId,
            @ApiParam(value = "接口ID") @PathVariable @Encrypt Long permissionId,
            @Encrypt SecGrpAclFieldDTO queryDTO,
            @ApiIgnore PageRequest pageRequest) {
        return Results.success(secGrpAclFieldRepository.listSecGrpApiField(null, secGrpId, permissionId, queryDTO, pageRequest));
    }

    @ApiOperation("用户安全组分配查询权限 - 数据权限维度")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{userId}/{secGrpId}/dim")
    public ResponseEntity<Page<SecGrpDclDimDTO>> listUserSecGrpDim(
            @ApiParam(value = "用户ID") @PathVariable @Encrypt Long userId,
            @ApiParam("安全组ID") @PathVariable @Encrypt Long secGrpId,
            @Encrypt SecGrpDclDimDTO queryDTO,
            @ApiIgnore PageRequest pageRequest) {
        return Results.success(secGrpDclDimRepository.listSecGrpAssignedDim(DetailsHelper.getUserDetails().getTenantId(), secGrpId, queryDTO, pageRequest));
    }

    @ApiOperation("用户安全组分配查询权限 - 数据权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{userId}/{secGrpId}/dcl")
    @CustomPageRequest
    public ResponseEntity<SecGrpDclDTO> listUserSecGrpDcl(
            @ApiParam(value = "用户ID") @PathVariable @Encrypt Long userId,
            @ApiParam(value = "安全组ID") @PathVariable @Encrypt Long secGrpId,
            @Encrypt SecGrpDclQueryDTO queryDTO, @ApiIgnore PageRequest pageRequest) {

        return Results.success(secGrpDclService.queryUserSecGrpDclAuthority(userId, secGrpId, queryDTO, pageRequest));
    }

}
