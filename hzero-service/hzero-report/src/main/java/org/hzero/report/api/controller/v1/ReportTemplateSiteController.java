package org.hzero.report.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.report.app.service.ReportTemplateService;
import org.hzero.report.config.ReportSwaggerApiConfig;
import org.hzero.report.domain.entity.ReportTemplate;
import org.hzero.report.domain.entity.Template;
import org.hzero.report.domain.repository.ReportTemplateRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * 报表模板关系 管理 API
 *
 * @author xianzhi.chen@hand-china.com 2018-11-26 10:07:24
 */
@Api(tags = ReportSwaggerApiConfig.REPORT_TEMPLATE_SITE)
@RestController("reportTemplateSiteController.v1")
@RequestMapping("/v1/report-templates")
public class ReportTemplateSiteController extends BaseController {

    private final ReportTemplateService reportTemplateService;
    private final ReportTemplateRepository reportTemplateRepository;

    @Autowired
    public ReportTemplateSiteController(ReportTemplateService reportTemplateService,
                                        ReportTemplateRepository reportTemplateRepository) {
        this.reportTemplateService = reportTemplateService;
        this.reportTemplateRepository = reportTemplateRepository;
    }

    @ApiOperation(value = "报表模板关系列表")
    @Permission(level = ResourceLevel.SITE)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/{reportId}")
    @CustomPageRequest
    public ResponseEntity<Page<ReportTemplate>> list(@Encrypt @PathVariable Long reportId,
                                                     @ApiIgnore @SortDefault(value = ReportTemplate.FIELD_REPORT_TEMPLATE_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(reportTemplateRepository.selectReportTemplatesByReportId(pageRequest, reportId));
    }

    @ApiOperation(value = "报表模板查询")
    @Permission(level = ResourceLevel.SITE)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping
    public ResponseEntity<Page<ReportTemplate>> detail(@Encrypt ReportTemplate reportTemplate, @ApiIgnore @SortDefault(value = Template.FIELD_TEMPLATE_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(reportTemplateRepository.selectReportTemplate(pageRequest, reportTemplate));
    }

    @ApiOperation(value = "创建报表模板关系")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<List<ReportTemplate>> create(@Encrypt @RequestBody List<ReportTemplate> reportTemplates) {
        reportTemplateService.insertReportTemplate(reportTemplates);
        return Results.success(reportTemplates);
    }

    @ApiOperation(value = "删除报表模板关系")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity<ReportTemplate> remove(@Encrypt @RequestBody ReportTemplate reportTemplate) {
        SecurityTokenHelper.validToken(reportTemplate);
        reportTemplateRepository.deleteByPrimaryKey(reportTemplate.getReportTemplateId());
        return Results.success();
    }

    @ApiOperation(value = "更新报表模板")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<Void> defaultTemplate(@Encrypt @RequestBody ReportTemplate reportTemplate) {
        SecurityTokenHelper.validToken(reportTemplate);
        reportTemplateService.defaultTemplate(reportTemplate);
        return Results.success();
    }

}
