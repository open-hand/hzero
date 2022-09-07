package org.hzero.report.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.report.app.service.TemplateDtlService;
import org.hzero.report.config.ReportSwaggerApiConfig;
import org.hzero.report.domain.entity.TemplateDtl;
import org.hzero.report.domain.repository.TemplateDtlRepository;
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
 * 报表模板明细 管理 API
 *
 * @author xianzhi.chen@hand-china.com 2018-12-05 08:58:44
 */
@Api(tags = ReportSwaggerApiConfig.REPORT_TEMPLATE_DTL_SITE)
@RestController("templateDtlSiteController.v1")
@RequestMapping("/v1/template-dtls")
public class TemplateDtlSiteController extends BaseController {

    @Autowired
    private TemplateDtlService templateDtlService;
    @Autowired
    private TemplateDtlRepository templateDtlRepository;

    @ApiOperation(value = "报表模板明细列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{templateId}")
    @CustomPageRequest
    public ResponseEntity<Page<TemplateDtl>> list(@Encrypt @PathVariable Long templateId,
                                                  @ApiIgnore @SortDefault(value = TemplateDtl.FIELD_TEMPLATE_DTL_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(templateDtlRepository.selectTemplateDtlsByTemplateId(pageRequest, templateId));
    }

    @ApiOperation(value = "报表模板明细明细")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/detail/{templateDtlId}")
    public ResponseEntity<TemplateDtl> detail(@Encrypt @PathVariable Long templateDtlId) {
        return Results.success(templateDtlRepository.selectTemplateDtl(templateDtlId));
    }

    @ApiOperation(value = "创建报表模板明细")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<TemplateDtl> create(@Encrypt @RequestBody TemplateDtl templateDtl) {
        // 验证数据
        validObject(templateDtl);
        // 验证唯一性
        templateDtl.validateRepeat(templateDtlRepository);
        templateDtlService.insertTemplateDtl(templateDtl);
        return Results.success(templateDtl);
    }

    @ApiOperation(value = "修改报表模板明细")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<TemplateDtl> update(@Encrypt @RequestBody TemplateDtl templateDtl) {
        // 数据防串改验证
        SecurityTokenHelper.validToken(templateDtl);
        // 验证唯一性
        templateDtl.validateRepeat(templateDtlRepository);
        templateDtlService.updateTemplateDtl(templateDtl);
        return Results.success(templateDtl);
    }

    @ApiOperation(value = "删除报表模板明细")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity remove(@Encrypt @RequestBody List<TemplateDtl> templateDtls) {
        SecurityTokenHelper.validToken(templateDtls);
        templateDtlService.deleteTemplateDtl(templateDtls);
        return Results.success();
    }

}
