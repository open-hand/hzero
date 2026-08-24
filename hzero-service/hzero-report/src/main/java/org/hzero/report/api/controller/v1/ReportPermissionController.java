package org.hzero.report.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.mybatis.helper.UniqueHelper;
import org.hzero.report.app.service.ReportPermissionService;
import org.hzero.report.config.ReportSwaggerApiConfig;
import org.hzero.report.domain.entity.ReportPermission;
import org.hzero.report.domain.repository.ReportPermissionRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * 报表权限 管理 API
 *
 * @author xianzhi.chen@hand-china.com 2018-11-29 10:57:31
 */
@Api(tags = ReportSwaggerApiConfig.REPORT_PERMISSION)
@RestController("reportPermissionController.v1")
@RequestMapping("/v1/{organizationId}/report-permissions")
public class ReportPermissionController extends BaseController {

    @Autowired
    private ReportPermissionService reportPermissionService;
    @Autowired
    private ReportPermissionRepository reportPermissionRepository;

    @ApiOperation(value = "报表权限列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<ReportPermission>> list(@PathVariable Long organizationId, @Encrypt Long reportId,
                                                       @ApiIgnore @SortDefault(value = ReportPermission.FIELD_PERMISSION_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(reportPermissionRepository.pageReportPermission(reportId, organizationId, true, pageRequest));
    }

    @ApiOperation(value = "创建报表权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<ReportPermission> create(@PathVariable Long organizationId,
                                                   @Encrypt @RequestBody ReportPermission reportPermission) {
        reportPermission.setTenantId(organizationId);
        validObject(reportPermission);
        Assert.notNull(reportPermission.getRoleId(), BaseConstants.ErrorCode.NOT_NULL);
        Assert.isTrue(UniqueHelper.valid(reportPermission), BaseConstants.ErrorCode.DATA_EXISTS);
        reportPermissionService.createReportPermission(reportPermission);
        return Results.success(reportPermission);
    }

    @ApiOperation(value = "修改报表权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<ReportPermission> update(@Encrypt @RequestBody ReportPermission reportPermission) {
        Assert.notNull(reportPermission.getRoleId(), BaseConstants.ErrorCode.NOT_NULL);
        SecurityTokenHelper.validTokenIgnoreInsert(reportPermission);
        return Results.success(reportPermissionService.updateReportPermission(reportPermission));
    }

    @ApiOperation(value = "删除报表权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity remove(@Encrypt @RequestBody ReportPermission reportPermission) {
        SecurityTokenHelper.validTokenIgnoreInsert(reportPermission);
        reportPermissionService.removeReportPermission(reportPermission.getPermissionId());
        return Results.success();
    }

}
