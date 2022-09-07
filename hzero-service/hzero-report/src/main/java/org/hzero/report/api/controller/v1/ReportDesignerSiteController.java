package org.hzero.report.api.controller.v1;

import io.swagger.annotations.Api;
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
import org.hzero.report.domain.service.IUreportService;
import org.hzero.report.infra.constant.HrptConstants;
import org.hzero.report.infra.engine.data.MetaDataColumn;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
@Api(tags = ReportSwaggerApiConfig.REPORT_DESIGNER_SITE)
@RestController("reportDesignerSiteController.v1")
@RequestMapping("/v1/reports/designer")
public class ReportDesignerSiteController extends BaseController {

    private final ReportService reportService;
    private final IUreportService ureportService;
    private final ReportRepository reportRepository;

    @Autowired
    public ReportDesignerSiteController(ReportService reportService,
                                        IUreportService ureportService,
                                        ReportRepository reportRepository) {
        this.reportService = reportService;
        this.ureportService = ureportService;
        this.reportRepository = reportRepository;
    }

    @ApiOperation(value = "报表信息列表")
    @Permission(level = ResourceLevel.SITE)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<Report>> list(Report report, @ApiIgnore @SortDefault(value = Report.FIELD_REPORT_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(reportRepository.selectReportDesigners(pageRequest, report));
    }

    @ApiOperation(value = "报表信息明细")
    @Permission(level = ResourceLevel.SITE)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/{reportId}")
    public ResponseEntity<Report> detail(@Encrypt @PathVariable Long reportId) {
        return Results.success(reportRepository.selectReportDesigner(reportId, null));
    }

    @ApiOperation(value = "创建报表信息")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<Report> create(@RequestBody Report report) {
        this.validObject(report);
        reportService.insertReport(report);
        return Results.success(report);
    }

    @ApiOperation(value = "修改报表信息")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<Report> update(@Encrypt @RequestBody Report report) {
        this.validObject(report);
        SecurityTokenHelper.validToken(report);
        reportService.updateReport(report);
        return Results.success(report);
    }

    @ApiOperation(value = "删除报表信息")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity<Void> remove(@Encrypt @RequestBody Report report) {
        SecurityTokenHelper.validToken(report);
        reportService.deleteReport(report.getReportId());
        return Results.success();
    }

    @ApiOperation(value = "初始化报表列")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/init-column")
    public ResponseEntity<List<MetaDataColumn>> getMetaDataColumn(@Encrypt Long reportId, @Encrypt @RequestParam Long datasetId) {
        return Results.success(reportService.initMetaDataColumns(reportId, datasetId));
    }

    @ApiOperation(value = "获取报表元数据列结构")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping(value = "/metacolumn")
    public MetaDataColumn getMetaColumn() {
        final MetaDataColumn column = new MetaDataColumn();
        column.setName("expr");
        column.setType(HrptConstants.ColumnType.COMPUTED);
        column.setDataType(HrptConstants.ColumnDataType.DECIMAL);
        column.setWidth(50);
        return column;
    }

    @ApiOperation(value = "编辑ureport报表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/ureport")
    public ResponseEntity<Void> designUreport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ureportService.designer(request, response);
        return Results.success();
    }
}
