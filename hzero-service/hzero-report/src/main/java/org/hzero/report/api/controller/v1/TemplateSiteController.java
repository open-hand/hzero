package org.hzero.report.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.report.app.service.TemplateService;
import org.hzero.report.config.ReportSwaggerApiConfig;
import org.hzero.report.domain.entity.Template;
import org.hzero.report.domain.repository.TemplateRepository;
import org.hzero.report.infra.constant.HrptMessageConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * 报表模版 管理 API
 *
 * @author xianzhi.chen@hand-china.com 2018-11-26 11:25:17
 */
@Api(tags = ReportSwaggerApiConfig.TEMPLATE_SITE)
@RestController("templateSiteController.v1")
@RequestMapping("/v1/templates")
public class TemplateSiteController extends BaseController {

    @Autowired
    private TemplateService templateService;
    @Autowired
    private TemplateRepository templateRepository;

    @ApiOperation(value = "报表模版列表")
    @Permission(level = ResourceLevel.SITE)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<Template>> list(Template template, @ApiIgnore @SortDefault(value = Template.FIELD_TEMPLATE_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(templateRepository.selectTemplates(pageRequest, template));
    }

    @ApiOperation(value = "报表模版明细")
    @Permission(level = ResourceLevel.SITE)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/{templateId}")
    public ResponseEntity<Template> detail(@Encrypt @PathVariable Long templateId) {
        return Results.success(templateRepository.selectTemplate(templateId));
    }

    @ApiOperation(value = "创建报表模版")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<Template> create(@RequestBody Template template) {
        this.validObject(template);
        if (!templateService.validateTemplateRepeat(template)) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_EXISTS);
        }
        templateRepository.insertSelective(template);
        return Results.success(template);
    }

    @ApiOperation(value = "修改报表模版")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<Template> update(@Encrypt @RequestBody Template template) {
        SecurityTokenHelper.validToken(template);
        templateRepository.updateByPrimaryKeySelective(template);
        return Results.success(template);
    }

    @ApiOperation(value = "删除报表模版")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity remove(@Encrypt @RequestBody Template template) {
        SecurityTokenHelper.validToken(template);
        boolean b = templateService.existReference(template.getTemplateId());
        if (b) {
            throw new CommonException(HrptMessageConstants.ERROR_EXIST_REFERENCE);
        }
        templateService.deleteTemplate(template.getTemplateId());
        return Results.success();
    }

}
