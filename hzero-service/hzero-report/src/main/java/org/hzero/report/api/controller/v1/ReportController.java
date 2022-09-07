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
@Api(tags = ReportSwaggerApiConfig.REPORT)
@Slf4j
@RestController("reportController.v1")
@RequestMapping("/v1/{organizationId}/reports")
public class ReportController extends BaseController {

    private final ReportService reportService;
    private final ReportRepository reportRepository;

    @Autowired
    public ReportController(ReportService reportService,
                            ReportRepository reportRepository) {
        this.reportService = reportService;
        this.reportRepository = reportRepository;
    }

    @ApiOperation(value = "报表查看汇总数据")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping
    @CustomPageRequest
    @ApiImplicitParams({
            @ApiImplicitParam(name = "reportCode", value = "报表代码", paramType = "query"),
            @ApiImplicitParam(name = "reportName", value = "报表名称", paramType = "query"),
            @ApiImplicitParam(name = "templateTypeCode", value = "报表类型", paramType = "query")
    })
    public ResponseEntity<Page<Report>> list(@PathVariable Long organizationId, Report report,
                                             @ApiIgnore @SortDefault(value = Report.FIELD_REPORT_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        report.setTenantId(organizationId);
        return Results.success(reportRepository.selectReports(pageRequest, report, DetailsHelper.getUserDetails().roleMergeIds(), organizationId));
    }

    @ApiOperation(value = "报表查看元数据")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping(value = {"/{reportUuid}"})
    public ResponseEntity<Report> detail(@PathVariable String reportUuid, @PathVariable Long organizationId, final HttpServletRequest request) {
        Map<String, Object> buildInParams = reportService.getBuildInParameters(request.getParameterMap());
        return Results.success(reportService.selectReport(reportUuid, buildInParams));
    }

    @ApiOperation(value = "获取报表DataSet JSON格式数据")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ResponseBody
    @GetMapping(value = "/dataset")
    public ResponseEntity<ReportDataSet> getDataSet(String reportUuid, final HttpServletRequest request) {
        return Results.success(this.reportService.getReportDataSet(reportUuid, request));
    }

    @ApiOperation(value = "获取报表JSON格式数据")
    @Permission(level = ResourceLevel.ORGANIZATION)
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
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping(value = "/{reportUuid}/concurrent-request")
    public ResponseEntity<Void> createRequest(@PathVariable Long organizationId, @PathVariable("reportUuid") String reportUuid,
                                              final HttpServletRequest request) {
        reportService.createConcRequest(organizationId, reportUuid, request);
        return Results.success();
    }

    @ApiOperation(value = "获取数据集关联的报表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping(value = "/{datasetId}/assign")
    public ResponseEntity<Page<Report>> pageReportByDataSet(@Encrypt @PathVariable Long datasetId, @PathVariable Long organizationId,
                                                            @ApiIgnore @SortDefault(value = Report.FIELD_REPORT_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(reportRepository.pageReportByDataSet(pageRequest, datasetId, organizationId));
    }
}