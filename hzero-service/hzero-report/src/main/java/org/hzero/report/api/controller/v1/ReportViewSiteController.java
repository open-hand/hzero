package org.hzero.report.api.controller.v1;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.hzero.core.base.BaseController;
import org.hzero.report.app.service.ReportService;
import org.hzero.report.config.ReportSwaggerApiConfig;
import org.hzero.report.infra.constant.HrptConstants;
import org.hzero.report.infra.enums.ReportTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

/**
 * 报表查看
 *
 * @author xianzhi.chen@hand-china.com 2018-10-22 16:35:10
 */
@Api(tags = ReportSwaggerApiConfig.REPORT_VIEW_SITE)
@Slf4j
@Controller("reportViewSiteController.v1")
@RequestMapping("/v1/reports")
public class ReportViewSiteController extends BaseController {

    @Autowired
    private ReportService reportService;

    @ApiOperation(value = "报表查看预览")
    @Permission(level = ResourceLevel.SITE, permissionPublic = true)
    @GetMapping(value = {"/view/{type}/{reportUuid}"})
    public ModelAndView preview(@PathVariable("type") String type, @PathVariable("reportUuid") String reportUuid,
                                HttpServletRequest request) {
        final String typeName = ReportTypeEnum.valueOf2(type).getValue();
        final String viewName = String.format("%s", typeName);
        final ModelAndView modelAndView = new ModelAndView(viewName);
        final JSONObject data = new JSONObject();
        reportService.getReportData(reportUuid, data, request);
        modelAndView.addObject(HrptConstants.REPORT, reportService.getReportBaseInfo(reportUuid));
        modelAndView.addObject(HrptConstants.REPORT_DATA, data);
        return modelAndView;
    }

    @ApiOperation(value = "导出报表文件")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping(value = "/export/{reportUuid}/{outputType}")
    @ResponseBody
    public void exportReportFile(@PathVariable("reportUuid") String reportUuid,
                                 @PathVariable("outputType") String outputType, HttpServletRequest request,
                                 HttpServletResponse response) {
        try {
            reportService.exportReportFile(reportUuid, outputType, request, response);
        } catch (final Exception ex) {
            log.error("导出Excel失败", ex);
        }
    }

    @ApiOperation(value = "获取报表的二进制文件")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping(value = "/byte/{reportUuid}/{outputType}")
    @ResponseBody
    public byte[] getReportFile(@PathVariable("reportUuid") String reportUuid,
                                @PathVariable("outputType") String outputType, HttpServletRequest request,
                                HttpServletResponse response) {
        return reportService.getReportFile(reportUuid, outputType, request, response);
    }
}
