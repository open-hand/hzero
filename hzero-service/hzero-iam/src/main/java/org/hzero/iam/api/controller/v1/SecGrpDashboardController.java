package org.hzero.iam.api.controller.v1;

import java.util.Collections;
import java.util.List;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.app.service.SecGrpAclDashboardService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.SecGrpAclDashboard;
import org.hzero.iam.domain.repository.SecGrpAclDashboardRepository;
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
import springfox.documentation.annotations.ApiIgnore;

/**
 * 安全组工作台配置 管理 API
 *
 * @author bojiangzhou 2020/02/12 代码优化
 * @author xingxing.wu@hand-china.com 2019-10-28 10:00:59
 */
@Api(tags = SwaggerApiConfig.SEC_GRP_ACL_DASHBOARD)
@RestController("secGrpAclDashboardController.v1")
@RequestMapping("/v1/{organizationId}/sec-grp-acl-dashboards/{secGrpId}")
public class SecGrpDashboardController extends BaseController {

    @Autowired
    private SecGrpAclDashboardService dashboardService;
    @Autowired
    private SecGrpAclDashboardRepository dashboardRepository;

    @ApiOperation(value = "租户层-安全组工作台维护-分页查询安全组工作台配置列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<SecGrpAclDashboard>> listSecGrpDashboard(
            @PathVariable("organizationId") Long tenantId,
            @PathVariable("secGrpId") @Encrypt Long secGrpId,
            @Encrypt SecGrpAclDashboard queryDTO,
            @ApiIgnore PageRequest pageRequest) {
        return Results.success(dashboardRepository.listSecGrpDashboard(tenantId, secGrpId, queryDTO, pageRequest));
    }

    @ApiOperation(value = "租户层-安全组工作台维护-查询安全组可分配的卡片")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/assignable")
    public ResponseEntity<Page<SecGrpAclDashboard>> listAssignableDashboard(
            @PathVariable("organizationId") Long organizationId,
            @PathVariable("secGrpId") @Encrypt Long secGrpId,
            @Encrypt SecGrpAclDashboard queryDTO,
            @ApiIgnore PageRequest pageRequest) {
        queryDTO.setTenantId(organizationId);
        return Results.success(dashboardRepository.listSecGrpAssignableDashboard(secGrpId, queryDTO, pageRequest));
    }

    @ApiOperation(value = "租户层-安全组工作台维护-新增工作台配置列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<Void> create(
            @PathVariable("organizationId") Long tenantId,
            @PathVariable("secGrpId") @Encrypt Long secGrpId,
            @RequestBody @Encrypt List<SecGrpAclDashboard> secGrpDashboards) {
        dashboardService.createSecGrpDashboard(tenantId, secGrpId, secGrpDashboards);
        return Results.success();
    }

    @ApiOperation(value = "租户层-安全组工作台维护-更新工作台配置列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<Void> update(
            @PathVariable("organizationId") Long tenantId,
            @PathVariable("secGrpId") @Encrypt Long secGrpId,
            @RequestBody @Encrypt SecGrpAclDashboard secGrpDashboard) {
        SecurityTokenHelper.validToken(secGrpDashboard);
        dashboardService.updateSecGrpDashboard(tenantId, secGrpId, Collections.singletonList(secGrpDashboard));
        return Results.success();
    }

    @ApiOperation(value = "租户层-安全组工作台维护-批量删除安全组工作台配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<Void> delete(
            @PathVariable("organizationId") Long tenantId,
            @PathVariable("secGrpId") @Encrypt Long secGrpId,
            @RequestBody @Encrypt List<SecGrpAclDashboard> secGrpDashboards) {
        SecurityTokenHelper.validToken(secGrpDashboards);
        dashboardService.removeSecGrpDashboard(tenantId, secGrpId, secGrpDashboards);
        return Results.success();
    }

}
