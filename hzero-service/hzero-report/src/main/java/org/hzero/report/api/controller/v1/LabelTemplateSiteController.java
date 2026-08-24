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
import org.springframework.beans.factory.annotation.Autowired;
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
@Api(tags = ReportSwaggerApiConfig.LABEL_TEMPLATE_SITE)
@RestController("labelTemplateSiteController.v1")
@RequestMapping("/v1/label-templates")
public class LabelTemplateSiteController extends BaseController {

    @Autowired
    private LabelTemplateRepository labelTemplateRepository;
    @Autowired
    private LabelTemplateService labelTemplateService;

    @ApiOperation(value = "标签模板列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "templateCode", value = "模板编码", paramType = "query"),
            @ApiImplicitParam(name = "templateName", value = "模板名称", paramType = "query"),
            @ApiImplicitParam(name = "datasetName", value = "数据集名称", paramType = "query"),
    })
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<LabelTemplate>> list(Long tenantId,
                                                    String templateCode,
                                                    String templateName,
                                                    String datasetName,
                                                    Integer enabledFlag,
                                                    @ApiIgnore @SortDefault(value = LabelTemplate.FIELD_LABEL_TEMPLATE_ID,
                                                            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(labelTemplateRepository.listLabelTemplate(tenantId, templateCode, templateName, datasetName, enabledFlag, pageRequest));
    }

    @ApiOperation(value = "标签模板明细")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{labelTemplateId}")
    public ResponseEntity<LabelTemplate> detail(@Encrypt @PathVariable Long labelTemplateId) {
        return Results.success(labelTemplateService.detailLabelTemplate(labelTemplateId));
    }

    @ApiOperation(value = "创建标签模板")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<LabelTemplate> create(@Encrypt @RequestBody LabelTemplate labelTemplate) {
        validObject(labelTemplate);
        return Results.success(labelTemplateService.createLabelTemplate(labelTemplate));
    }

    @ApiOperation(value = "修改标签模板")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<LabelTemplate> update(@Encrypt @RequestBody LabelTemplate labelTemplate) {
        validObject(labelTemplate);
        SecurityTokenHelper.validTokenIgnoreInsert(labelTemplate);
        return Results.success(labelTemplateService.updateLabelTemplate(labelTemplate));
    }

    @ApiOperation(value = "删除标签模板")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity remove(@Encrypt @RequestBody LabelTemplate labelTemplate) {
        SecurityTokenHelper.validToken(labelTemplate);
        labelTemplateService.deleteLabelTemplate(labelTemplate);
        return Results.success();
    }

    @ApiOperation(value = "复制标签模板")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/copy")
    public ResponseEntity copy(@RequestParam Long tenantId, @Encrypt @RequestBody LabelTemplate labelTemplate) {
        return Results.success(labelTemplateService.copyLabelTemplate(labelTemplate, tenantId));
    }

}
