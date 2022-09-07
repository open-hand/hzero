package org.hzero.platform.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.platform.api.dto.TemplateAssignDTO;
import org.hzero.platform.app.service.TemplateAssignService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.ContentTemplate;
import org.hzero.platform.domain.entity.TemplateAssign;
import org.hzero.platform.domain.repository.TemplateAssignRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 分配模板 管理 API
 *
 * @author xiaoyu.zhao@hand-china.com 2019-07-03 10:57:26
 */
@Api(tags = PlatformSwaggerApiConfig.TEMPLATE_ASSIGN_SITE)
@RestController("templateAssignSiteController.v1")
@RequestMapping("/v1/template-assigns")
public class TemplateAssignSiteController extends BaseController {

    private final TemplateAssignRepository templateAssignRepository;
    private final TemplateAssignService templateAssignService;

    @Autowired
    public TemplateAssignSiteController(TemplateAssignRepository templateAssignRepository,
                    TemplateAssignService templateAssignService) {
        this.templateAssignRepository = templateAssignRepository;
        this.templateAssignService = templateAssignService;
    }

    @ApiOperation(value = "分配模板列表")
    @Permission(level = ResourceLevel.SITE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sourceType", value = "关联模板的来源类型，根据引用模板的功能自行定义", paramType = "query",
                            dataType = "string", required = true),
            @ApiImplicitParam(name = "sourceKey", value = "关联模板的来源KEY", paramType = "query", dataType = "string",
                            required = true),
            @ApiImplicitParam(name = "tenantId", value = "租户Id", paramType = "query", required = true)})
    @GetMapping
    public ResponseEntity<List<TemplateAssignDTO>> listTemplateAssigns(@RequestParam("tenantId") Long tenantId,
                    @RequestParam("sourceType") String sourceType, @RequestParam("sourceKey") @Encrypt String sourceKey) {
        return Results.success(templateAssignRepository.listTemplateAssigns(sourceType, sourceKey, tenantId));
    }

    @ApiOperation(value = "批量分配模板")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/batch-create")
    public ResponseEntity<List<TemplateAssign>> batchCreateTemplateAssigns(
                    @RequestBody @Encrypt List<TemplateAssign> templateAssigns) {
        // 新分配的模板都设置为非默认
        templateAssigns.forEach(templateAssign -> templateAssign.setDefaultFlag(BaseConstants.Flag.NO));
        validList(templateAssigns);
        return Results.success(templateAssignService.batchCreateTemplateAssigns(templateAssigns));
    }

    @ApiOperation(value = "删除分配模板")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity deleteTemplateAssigns(@RequestBody @Encrypt TemplateAssign templateAssign) {
        templateAssignService.deleteTemplateAssign(templateAssign);
        return Results.success();
    }

    @ApiOperation(value = "设置默认分配模板")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping("/default")
    public ResponseEntity<TemplateAssign> defaultTemplateAssign(@RequestParam("templateAssignId") @Encrypt Long templateAssignId) {
        return Results.success(templateAssignService.defaultTemplateAssign(templateAssignId));
    }

    @ApiOperation(value = "批量删除分配模板")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping("/batch-delete")
    public ResponseEntity batchDeleteTemplateAssigns(@RequestBody @Encrypt List<TemplateAssign> templateAssigns) {
        templateAssignService.batchDeleteTemplateAssigns(templateAssigns);
        return Results.success();
    }

    @ApiOperation(value = "查询可分配模板列表")
    @Permission(level = ResourceLevel.SITE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sourceType", value = "关联模板的来源类型，根据引用模板的功能自行定义", paramType = "query",
                    dataType = "string", required = true),
            @ApiImplicitParam(name = "sourceKey", value = "关联模板的来源KEY", paramType = "query", dataType = "string",
                    required = true),
            @ApiImplicitParam(name = "tenantId", value = "来源所属租户Id", paramType = "query", required = true)})
    @GetMapping("/assignable")
    public ResponseEntity<Page<TemplateAssignDTO>> selectAssignableTemplates(
            @Encrypt TemplateAssignDTO templates,
            @ApiIgnore @SortDefault(value = ContentTemplate.FIELD_TEMPLATE_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(templateAssignRepository.selectAssignableTemplates(pageRequest, templates));
    }

}
