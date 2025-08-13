package org.hzero.report.api.controller.v1;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.report.app.service.ReportService;
import org.hzero.report.config.ReportSwaggerApiConfig;
import org.hzero.report.domain.entity.Report;
import org.hzero.report.domain.repository.ReportRepository;
import org.hzero.report.infra.constant.HrptConstants;
import org.hzero.report.infra.constant.HrptMessageConstants;
import org.hzero.report.infra.engine.data.ReportDataSet;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 报表信息 管理 API
 *
 * @author xianzhi.chen@hand-china.com 2018-10-22 16:35:10
 */
@Api(tags = ReportSwaggerApiConfig.REPORT_SITE)
@Slf4j
@RestController("reportSiteController.v1")
@RequestMapping("/v1/reports")
public class ReportSiteController extends BaseController {

    private final ReportService reportService;
    private final ReportRepository reportRepository;

    @Autowired
    public ReportSiteController(ReportService reportService,
                                ReportRepository reportRepository) {
        this.reportService = reportService;
        this.reportRepository = reportRepository;
    }

    @ApiOperation(value = "报表查看汇总数据")
    @Permission(level = ResourceLevel.SITE)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping
    @CustomPageRequest
    @ApiImplicitParams({
            @ApiImplicitParam(name = "reportCode", value = "报表代码", paramType = "query"),
            @ApiImplicitParam(name = "reportName", value = "报表名称", paramType = "query"),
            @ApiImplicitParam(name = "templateTypeCode", value = "报表类型", paramType = "query")
    })
    public ResponseEntity<Page<Report>> list(Report report, @ApiIgnore @SortDefault(value = Report.FIELD_REPORT_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        return Results.success(reportRepository.selectReports(pageRequest, report, userDetails.roleMergeIds(), userDetails.getTenantId()));
    }

    @ApiOperation(value = "报表查看元数据")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping(value = {"/{reportUuid}"})
    public ResponseEntity<Report> detail(@PathVariable String reportUuid, final HttpServletRequest request) {
        Map<String, Object> buildinParams = reportService.getBuildInParameters(request.getParameterMap());
        return Results.success(reportService.selectReport(reportUuid, buildinParams));
    }

    @ApiOperation(value = "获取报表DataSet JSON格式数据")
    @Permission(level = ResourceLevel.SITE)
    @ResponseBody
    @GetMapping(value = "/dataset")
    public ResponseEntity<ReportDataSet> getDataSet(String reportUuid, final HttpServletRequest request) {
        return Results.success(this.reportService.getReportDataSet(reportUuid, request));
    }

    @ApiOperation(value = "获取报表JSON格式数据")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping(value = "/{reportUuid}/data")
    public JSONObject getReportData(@PathVariable("reportUuid") String reportUuid, HttpServletRequest request) {
        JSONObject data = new JSONObject();
        try {
            data = this.reportService.getReportData(reportUuid, data, request);
        } catch (CommonException ex) {
            data.put(HrptConstants.HTML_TABLE, getMessage(ex.getMessage()));
            log.error("报表生成失败", ex);
        } catch (final Exception ex) {
            data.put(HrptConstants.HTML_TABLE, getMessage(HrptMessageConstants.ERROR_REPORT_GENERATE));
            log.error("报表系统出错", ex);
        }
        return data;
    }

    @ApiOperation(value = "创建并发请求")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping(value = "/{reportUuid}/concurrent-request")
    public ResponseEntity<Void> createRequest(@PathVariable("reportUuid") String reportUuid, Long tenantId, final HttpServletRequest request) {
        reportService.createConcRequest(tenantId, reportUuid, request);
        return Results.success();
    }

    @ApiOperation(value = "获取数据集关联的报表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping(value = "/{datasetId}/assign")
    public ResponseEntity<Page<Report>> pageReportByDataSet(@Encrypt @PathVariable Long datasetId, Long tenantId,
                                                            @ApiIgnore @SortDefault(value = Report.FIELD_REPORT_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(reportRepository.pageReportByDataSet(pageRequest, datasetId, tenantId));
    }

    @ApiOperation(value = "获取报表允许的导出类型")
    @Permission(permissionLogin = true)
    @GetMapping(value = "/export-type")
    public ResponseEntity<Map<String, Object>> exportType() {
        return Results.success(reportService.getExportType());
    }
}
