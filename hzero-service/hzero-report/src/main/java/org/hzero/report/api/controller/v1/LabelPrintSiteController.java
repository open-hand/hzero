package org.hzero.report.api.controller.v1;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.report.app.service.LabelPrintService;
import org.hzero.report.config.ReportSwaggerApiConfig;
import org.hzero.report.domain.entity.LabelPrint;
import org.hzero.report.domain.entity.LabelTemplate;
import org.hzero.report.domain.repository.LabelTemplateRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 标签打印 管理 API
 *
 * @author fanghan.liu@hand-china.com 2019-11-27 10:35:39
 */
@Api(tags = ReportSwaggerApiConfig.LABEL_PRINT_SITE)
@RestController("labelPrintSiteController.v1")
@RequestMapping("/v1/label-prints")
public class LabelPrintSiteController extends BaseController {

    @Autowired
    private LabelPrintService labelPrintService;
    @Autowired
    private LabelTemplateRepository labelTemplateRepository;

    @ApiOperation(value = "标签打印布局明细")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{labelTemplateCode}")
    public ResponseEntity<LabelPrint> detailTemplateLayout(@RequestParam("organizationId") Long organizationId, @PathVariable String labelTemplateCode) {
        return Results.success(labelPrintService.detailLabelPrint(organizationId, labelTemplateCode));
    }

    @ApiOperation(value = "标签打印布局明细")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<LabelPrint> detailLayout(@RequestParam("labelTenantId") Long labelTenantId, @RequestParam String labelTemplateCode) {
        return Results.success(labelPrintService.detailLabelPrint(labelTenantId, labelTemplateCode));
    }

    @ApiOperation(value = "创建或修改标签打印布局")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<LabelPrint> createOrUpdatePrint(@Encrypt @RequestBody LabelPrint labelPrint) {
        validObject(labelPrint);
        SecurityTokenHelper.validTokenIgnoreInsert(labelPrint);
        labelPrint.validatePrintSize(labelTemplateRepository, labelPrint.getTenantId());
        return Results.success(labelPrintService.createOrUpdatePrint(labelPrint));
    }

    @ApiOperation(value = "标签查看元数据")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping(value = {"/meta/{labelTemplateCode}"})
    public ResponseEntity<LabelTemplate> templateMetaData(@PathVariable String labelTemplateCode, final HttpServletRequest request) {
        Map<String, Object> buildInParams = labelPrintService.getBuildInParameters(request.getParameterMap());
        return Results.success(labelPrintService.selectLabel(labelTemplateCode, buildInParams));
    }

    @ApiOperation(value = "标签查看元数据")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping(value = {"/meta"})
    public ResponseEntity<LabelTemplate> metaData(@RequestParam("labelTenantId") Long labelTenantId, @RequestParam String labelTemplateCode, final HttpServletRequest request) {
        Map<String, Object> buildInParams = labelPrintService.getBuildInParameters(request.getParameterMap());
        return Results.success(labelPrintService.selectLabel(labelTenantId, labelTemplateCode, buildInParams));
    }

    @ApiOperation(value = "获取模板JSON格式数据")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/view/{labelTemplateCode}")
    public JSONObject previewTemplate(@PathVariable String labelTemplateCode, HttpServletRequest request) {
        final JSONObject data = new JSONObject();
        labelPrintService.getLabelData(labelTemplateCode, data, request);
        return data;
    }

    @ApiOperation(value = "获取模板JSON格式数据")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/view")
    public JSONObject previewTemplateData(@RequestParam("labelTenantId") Long labelTenantId, @RequestParam String labelTemplateCode, HttpServletRequest request) {
        final JSONObject data = new JSONObject();
        labelPrintService.getLabelData(labelTenantId, labelTemplateCode, data, request);
        return data;
    }

    @ApiOperation(value = "预览标签html")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/view/html/{labelTemplateCode}")
    public JSONObject previewLabelHtml(@PathVariable String labelTemplateCode, HttpServletRequest request) {
        return labelPrintService.getLabelHtmlData(labelTemplateCode, request);
    }

    @ApiOperation(value = "预览标签html")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/view/html")
    public JSONObject previewHtml(@RequestParam("labelTenantId") Long labelTenantId, @RequestParam String labelTemplateCode, HttpServletRequest request) {
        return labelPrintService.getLabelHtmlData(labelTenantId, labelTemplateCode, request);
    }

}
