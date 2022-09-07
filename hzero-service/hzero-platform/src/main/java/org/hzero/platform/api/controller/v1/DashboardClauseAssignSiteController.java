package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.app.service.DashboardClauseAssignService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.DashboardClauseAssign;
import org.hzero.platform.domain.repository.DashboardClauseAssignRepository;
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
 * 工作台条目分配租户 管理 API
 *
 * @author jun.gao@hand-china.com 2019-01-27 19:10:25
 * @author xiaoyu.zhao@hand-china.com
 */
@Api(tags = PlatformSwaggerApiConfig.DASHBOARD_CLAUSE_ASSIGN_SITE)
@RestController("dashboardClauseAssignSiteController.v1")
@RequestMapping("/v1/dashboard-clause-assign")
public class DashboardClauseAssignSiteController extends BaseController {
    @Autowired
    private DashboardClauseAssignService dashboardClauseAssignService;
    @Autowired
    private DashboardClauseAssignRepository dashboardClauseAssignRepository;

    @ApiOperation(value = "批量保存工作台条目分配租户")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<List<DashboardClauseAssign>> saveDbdClauseAssign(@RequestBody @Encrypt List<DashboardClauseAssign> dashboardClauseAssigns) {
        this.validList(dashboardClauseAssigns);
        return Results.success(dashboardClauseAssignService.saveDbdClauseAssigns(dashboardClauseAssigns));
    }

    @ApiOperation(value = "卡片条目分配租户查询")
    @Permission(level = ResourceLevel.SITE)
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<Page<DashboardClauseAssign>> queryDbdClauseAssign(@RequestParam @Encrypt Long clauseId,
                                                                            @Encrypt DashboardClauseAssign dashboardClauseAssign,
                                                                            @ApiIgnore @SortDefault(value = DashboardClauseAssign.FIELD_CLAUSE_ASSIGN_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        dashboardClauseAssign.setClauseId(clauseId);
        return Results.success(dashboardClauseAssignRepository.queryDbdClauseAssign(dashboardClauseAssign, pageRequest));
    }

    @ApiOperation(value = "批量删除工作台条目分配租户")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity deleteDbdClauseAssign(@RequestBody @Encrypt List<DashboardClauseAssign> dashboardClauseAssigns) {
        SecurityTokenHelper.validToken(dashboardClauseAssigns);
        dashboardClauseAssignService.deleteDbdClauseAssign(dashboardClauseAssigns);
        return Results.success();
    }

}
