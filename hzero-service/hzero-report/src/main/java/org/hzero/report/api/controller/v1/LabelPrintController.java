package org.hzero.report.api.controller.v1;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.report.app.service.LabelPrintService;
import org.hzero.report.config.ReportSwaggerApiConfig;
import org.hzero.report.domain.entity.LabelPrint;
import org.hzero.report.domain.entity.LabelTemplate;
import org.hzero.report.domain.repository.LabelTemplateRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

/**
 * 标签打印 管理 API
 *
 * @author fanghan.liu@hand-china.com 2019-11-27 10:35:39
 */
@Api(tags = ReportSwaggerApiConfig.LABEL_PRINT)
@RestController("labelPrintController.v1")
@RequestMapping("/v1/{organizationId}/label-prints")
public class LabelPrintController extends BaseController {

    private LabelPrintService labelPrintService;
    private LabelTemplateRepository labelTemplateRepository;

    public LabelPrintController(LabelPrintService labelPrintService, LabelTemplateRepository labelTemplateRepository) {
        this.labelPrintService = labelPrintService;
        this.labelTemplateRepository = labelTemplateRepository;
    }

    @ApiOperation(value = "标签打印布局明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{labelTemplateCode}")
    public ResponseEntity<LabelPrint> detailTemplateLayout(@PathVariable("organizationId") Long organizationId, @PathVariable String labelTemplateCode) {
        return Results.success(labelPrintService.detailLabelPrint(organizationId, labelTemplateCode));
    }

    @ApiOperation(value = "标签打印布局明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<LabelPrint> detailLayout(@PathVariable("organizationId") Long organizationId, @RequestParam String labelTemplateCode) {
        return Results.success(labelPrintService.detailLabelPrint(organizationId, labelTemplateCode));
    }

    @ApiOperation(value = "创建或修改标签打印布局")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<LabelPrint> createOrUpdatePrint(@Encrypt @RequestBody LabelPrint labelPrint, @PathVariable("organizationId") Long organizationId) {
        validObject(labelPrint);
        SecurityTokenHelper.validTokenIgnoreInsert(labelPrint);
        labelPrint.validatePrintSize(labelTemplateRepository, labelPrint.getTenantId());
        labelPrint.setTenantId(organizationId);
        return Results.success(labelPrintService.createOrUpdatePrint(labelPrint));
    }

    @ApiOperation(value = "标签查看元数据")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping(value = {"/meta/{labelTemplateCode}"})
    public ResponseEntity<LabelTemplate> templateMetaData(@PathVariable String labelTemplateCode, final HttpServletRequest request) {
        Map<String, Object> buildInParams = labelPrintService.getBuildInParameters(request.getParameterMap());
        return Results.success(labelPrintService.selectLabel(labelTemplateCode, buildInParams));
    }

    @ApiOperation(value = "标签查看元数据")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/meta")
    public ResponseEntity<LabelTemplate> metaData(@RequestParam String labelTemplateCode, final HttpServletRequest request) {
        Map<String, Object> buildInParams = labelPrintService.getBuildInParameters(request.getParameterMap());
        return Results.success(labelPrintService.selectLabel(labelTemplateCode, buildInParams));
    }

    @ApiOperation(value = "获取模板JSON格式数据")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/view/{labelTemplateCode}")
    public JSONObject previewTemplate(@PathVariable String labelTemplateCode, HttpServletRequest request) {
        final JSONObject data = new JSONObject();
        labelPrintService.getLabelData(labelTemplateCode, data, request);
        return data;
    }

    @ApiOperation(value = "获取模板JSON格式数据")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/view")
    public JSONObject previewTemplateData(@RequestParam String labelTemplateCode, HttpServletRequest request) {
        final JSONObject data = new JSONObject();
        labelPrintService.getLabelData(labelTemplateCode, data, request);
        return data;
    }

    @ApiOperation(value = "预览标签html")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/view/html/{labelTemplateCode}")
    public JSONObject previewLabelHtml(@PathVariable String labelTemplateCode, HttpServletRequest request) {
        return labelPrintService.getLabelHtmlData(labelTemplateCode, request);
    }

    @ApiOperation(value = "预览标签html")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/view/html")
    public JSONObject previewHtml(@RequestParam String labelTemplateCode, HttpServletRequest request) {
        return labelPrintService.getLabelHtmlData(labelTemplateCode, request);
    }

}
