package org.hzero.report.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.report.app.service.LabelTemplateService;
import org.hzero.report.config.ReportSwaggerApiConfig;
import org.hzero.report.domain.entity.LabelTemplate;
import org.hzero.report.domain.repository.LabelTemplateRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * 标签模板 管理 API
 *
 * @author fanghan.liu@hand-china.com 2019-11-27 10:35:39
 */
@Api(tags = ReportSwaggerApiConfig.LABEL_TEMPLATE)
@RestController("labelTemplateController.v1")
@RequestMapping("/v1/{organizationId}/label-templates")
public class LabelTemplateController extends BaseController {

    private LabelTemplateRepository labelTemplateRepository;
    private LabelTemplateService labelTemplateService;

    public LabelTemplateController(LabelTemplateRepository labelTemplateRepository, LabelTemplateService labelTemplateService) {
        this.labelTemplateRepository = labelTemplateRepository;
        this.labelTemplateService = labelTemplateService;
    }

    @ApiOperation(value = "标签模板列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "templateCode", value = "模板编码", paramType = "query"),
            @ApiImplicitParam(name = "templateName", value = "模板名称", paramType = "query"),
            @ApiImplicitParam(name = "datasetName", value = "数据集名称", paramType = "query"),
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<LabelTemplate>> list(@PathVariable("organizationId") Long organizationId,
                                                    String templateCode,
                                                    String templateName,
                                                    String datasetName,
                                                    Integer enabledFlag,
                                                    @ApiIgnore @SortDefault(value = LabelTemplate.FIELD_LABEL_TEMPLATE_ID,
                                                            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(labelTemplateRepository.listTenantLabelTemplate(organizationId, templateCode, templateName, datasetName, enabledFlag, pageRequest));
    }

    @ApiOperation(value = "标签模板明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{labelTemplateId}")
    public ResponseEntity<LabelTemplate> detail(@Encrypt @PathVariable Long labelTemplateId) {
        return Results.success(labelTemplateService.detailLabelTemplate(labelTemplateId));
    }

    @ApiOperation(value = "创建标签模板")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<LabelTemplate> create(@PathVariable("organizationId") Long organizationId, @Encrypt @RequestBody LabelTemplate labelTemplate) {
        labelTemplate.setTenantId(organizationId);
        validObject(labelTemplate);
        return Results.success(labelTemplateService.createLabelTemplate(labelTemplate));
    }

    @ApiOperation(value = "修改标签模板")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<LabelTemplate> update(@PathVariable("organizationId") Long organizationId, @Encrypt @RequestBody LabelTemplate labelTemplate) {
        labelTemplate.setTenantId(organizationId);
        validObject(labelTemplate);
        SecurityTokenHelper.validTokenIgnoreInsert(labelTemplate);
        return Results.success(labelTemplateService.updateLabelTemplate(labelTemplate));
    }

    @ApiOperation(value = "删除标签模板")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity remove(@PathVariable("organizationId") Long organizationId, @Encrypt @RequestBody LabelTemplate labelTemplate) {
        labelTemplate.setTenantId(organizationId);
        SecurityTokenHelper.validToken(labelTemplate);
        labelTemplateService.deleteLabelTemplate(labelTemplate);
        return Results.success();
    }

    @ApiOperation(value = "复制标签模板")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/copy")
    public ResponseEntity copy(@PathVariable("organizationId") Long organizationId, @Encrypt @RequestBody LabelTemplate labelTemplate) {
        return Results.success(labelTemplateService.copyLabelTemplate(labelTemplate, organizationId));
    }

}
