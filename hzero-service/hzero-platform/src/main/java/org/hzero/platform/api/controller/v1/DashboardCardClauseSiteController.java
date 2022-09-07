package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.app.service.DashboardCardClauseService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.DashboardCardClause;
import org.hzero.platform.domain.repository.DashboardCardClauseRepository;
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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 工作台条目卡片分配管理 API
 *
 * @author xiaoyu.zhao@hand-china.com 2019-03-06 17:55:53
 */
@Api(tags = PlatformSwaggerApiConfig.DASHBOARD_CARD_CLAUSE_SITE)
@RestController("dashboardCardClauseSiteController.v1")
@RequestMapping("/v1/dashboard-card-clauses")
public class DashboardCardClauseSiteController extends BaseController {

    @Autowired
    private DashboardCardClauseRepository dashboardCardClauseRepository;
    @Autowired
    private DashboardCardClauseService dashboardCardClauseService;

    @ApiOperation(value = "条目关联卡片列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{clauseId}")
    @CustomPageRequest
    public ResponseEntity<Page<DashboardCardClause>> pageDashboardCardClauseList(@PathVariable("clauseId") @Encrypt Long clauseId,
                                                                                 @Encrypt DashboardCardClause dashboardCardClause,
                                                                                 @ApiIgnore @SortDefault(value = DashboardCardClause.FIELD_CLAUSE_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        dashboardCardClause.setClauseId(clauseId);
        return Results.success(dashboardCardClauseRepository.selectDashboardCardClauseList(dashboardCardClause, pageRequest));
    }

    @ApiOperation(value = "批量删除条目关联卡片")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity removeDashboardCardClause(@RequestBody @Encrypt List<DashboardCardClause> dashboardCardClauses) {
        SecurityTokenHelper.validToken(dashboardCardClauses);
        dashboardCardClauseService.batchDeleteDashboardCardClause(dashboardCardClauses);
        return Results.success();
    }

}
