package org.hzero.report.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.report.app.service.ReportService;
import org.hzero.report.config.ReportSwaggerApiConfig;
import org.hzero.report.domain.entity.Report;
import org.hzero.report.domain.repository.ReportRepository;
import org.hzero.report.infra.constant.HrptConstants;
import org.hzero.report.infra.engine.data.MetaDataColumn;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDate;
import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * 报表设计器管理API
 *
 * @author xianzhi.chen@hand-china.com 2018年10月24日下午1:44:16
 */
@Api(tags = ReportSwaggerApiConfig.REPORT_DESIGNER)
@RestController("reportDesignerController.v1")
@RequestMapping("/v1/{organizationId}/reports/designer")
public class ReportDesignerController extends BaseController {

    private final ReportService reportService;
    private final ReportRepository reportRepository;

    @Autowired
    public ReportDesignerController(ReportService reportService,
                                    ReportRepository reportRepository) {
        this.reportService = reportService;
        this.reportRepository = reportRepository;
    }

    @ApiOperation(value = "报表信息列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping
    @CustomPageRequest
    @ApiImplicitParams({
            @ApiImplicitParam(name = "reportCode", value = "报表编码", paramType = "query"),
            @ApiImplicitParam(name = "reportName", value = "报表名称", paramType = "query"),
            @ApiImplicitParam(name = "reportTypeCode", value = "报表类型", paramType = "query")
    })
    public ResponseEntity<Page<Report>> list(@PathVariable Long organizationId, String reportCode, String reportName, String reportTypeCode,
                                             @ApiIgnore @SortDefault(value = Report.FIELD_REPORT_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(reportRepository.selectTenantReportDesigners(pageRequest, reportCode, reportName, reportTypeCode, organizationId, LocalDate.now()));
    }

    @ApiOperation(value = "报表信息明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/{reportId}")
    public ResponseEntity<Report> detail(@PathVariable Long organizationId, @Encrypt @PathVariable Long reportId) {
        return Results.success(reportRepository.selectReportDesigner(reportId, organizationId));
    }

    @ApiOperation(value = "复制报表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @PostMapping("/{reportId}/copy")
    public ResponseEntity<Void> copyReport(@PathVariable Long organizationId, @Encrypt @PathVariable Long reportId) {
        reportService.copyReport(reportId, organizationId);
        return Results.success();
    }

    @ApiOperation(value = "创建报表信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<Report> create(@PathVariable Long organizationId, @RequestBody Report report) {
        report.setTenantId(organizationId);
        this.validObject(report);
        reportService.insertReport(report);
        return Results.success(report);
    }

    @ApiOperation(value = "修改报表信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<Report> update(@PathVariable Long organizationId, @Encrypt @RequestBody Report report) {
        report.setTenantId(organizationId);
        this.validObject(report);
        SecurityTokenHelper.validToken(report);
        reportService.updateReport(report);
        return Results.success(report);
    }

    @ApiOperation(value = "删除报表信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<Void> remove(@Encrypt @RequestBody Report report) {
        SecurityTokenHelper.validToken(report);
        reportService.deleteReport(report.getReportId());
        return Results.success();
    }

    @ApiOperation(value = "初始化报表列")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/init-column")
    public ResponseEntity<List<MetaDataColumn>> getMetaDataColumn(@Encrypt Long reportId, @Encrypt @RequestParam Long datasetId) {
        return Results.success(reportService.initMetaDataColumns(reportId, datasetId));
    }

    @ApiOperation(value = "获取报表元数据列结构")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping(value = "/metacolumn")
    public MetaDataColumn getMetaColumn() {
        final MetaDataColumn column = new MetaDataColumn();
        column.setName("expr");
        column.setType(HrptConstants.ColumnType.COMPUTED);
        column.setDataType(HrptConstants.ColumnDataType.DECIMAL);
        column.setWidth(50);
        return column;
    }
}
