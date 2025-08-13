package org.hzero.platform.api.controller.v1;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.app.service.DashboardClauseService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.DashboardClause;
import org.hzero.platform.domain.repository.DashboardClauseRepository;
import org.hzero.platform.infra.constant.Constants;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 工作台条目配置 管理 API
 *
 * @author jun.gao@hand-china.com 2019-01-27 19:10:25
 * @author xiaoyu.zhao@hand-china.com
 */
@Api(tags = PlatformSwaggerApiConfig.DASHBOARD_CLAUSE)
@RestController("dashboardClauseController.v1")
@RequestMapping("/v1/{organizationId}/dashboard-clause")
public class DashboardClauseController extends BaseController {

    @Autowired
    private DashboardClauseRepository dashboardClauseRepository;

    @Autowired
    private DashboardClauseService dashboardClauseService;

    @ApiOperation(value = "卡片条目配置列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<Page<DashboardClause>> queryDashboardClause(@PathVariable("organizationId") Long tenantId,
                    DashboardClause clause,
                    @ApiIgnore @SortDefault(value = DashboardClause.FIELD_CLAUSE_CODE) PageRequest pageRequest) {
        // op仅查询平台级卡片条目
        // 2019-04-12 [IMP] 添加租户限制
        clause.setTenantId(tenantId);
        clause.setDataTenantLevel(Constants.SITE_LEVEL_UPPER_CASE);
        Page<DashboardClause> dashboardClauses = dashboardClauseRepository.queryDashboardClause(clause, pageRequest);
        return Results.success(dashboardClauses);
    }

    @ApiOperation(value = "新增卡片条目配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<DashboardClause> saveDashboardClause(@PathVariable("organizationId") Long tenantId,
                    @RequestBody DashboardClause dashboardClause) {
        // op版本直接设置条目层级为平台级
        dashboardClause.setTenantId(tenantId);
        dashboardClause.setDataTenantLevel(Constants.SITE_LEVEL_UPPER_CASE);
        validObject(dashboardClause);
        return Results.success(dashboardClauseService.saveDashboardClause(dashboardClause));
    }

    @ApiOperation(value = "修改卡片条目配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<DashboardClause> updateDashboardClause(@PathVariable("organizationId") Long tenantId,
                    @RequestBody @Encrypt DashboardClause dashboardClause) {
        SecurityTokenHelper.validToken(dashboardClause, false);
        dashboardClause.setTenantId(tenantId);
        validObject(dashboardClause);
        return Results.success(dashboardClauseService.updateDashboardClause(dashboardClause));
    }

    @ApiOperation(value = "查询卡片条目明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/details/{clauseId}")
    public ResponseEntity<DashboardClause> queryDashboardClauseDetails(@PathVariable("clauseId") @Encrypt Long clauseId) {
        return Results.success(dashboardClauseRepository.queryDashboardClauseDetails(clauseId));
    }

}
