package org.hzero.platform.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.platform.api.dto.commontemplate.*;
import org.hzero.platform.app.service.CommonTemplateService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.CommonTemplate;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import static org.hzero.core.base.BaseConstants.FIELD_BODY;

/**
 * 通用模板 管理 API
 *
 * @author bo.he02@hand-china.com 2020-08-04 09:49:14
 */
@Api(tags = PlatformSwaggerApiConfig.COMMON_TEMPLATE_SITE)
@RestController("commonTemplateSiteController.v1")
@RequestMapping("/v1/common-templates")
public class CommonTemplateSiteController extends BaseController {
    /**
     * 通用模板应用服务对象
     */
    private final CommonTemplateService commonTemplateService;

    @Autowired
    public CommonTemplateSiteController(CommonTemplateService commonTemplateService) {
        this.commonTemplateService = commonTemplateService;
    }

    @ApiOperation(value = "通用模板列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    @ProcessLovValue(targetField = FIELD_BODY)
    public ResponseEntity<Page<CommonTemplateDTO>> list(CommonTemplateQueryDTO queryDTO,
                                                        @ApiIgnore @SortDefault(value = CommonTemplate.FIELD_TEMPLATE_ID,
                                                                direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(this.commonTemplateService.list(queryDTO, pageRequest));
    }

    @ApiOperation(value = "通用模板明细")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{templateId}")
    @ProcessLovValue(targetField = FIELD_BODY)
    public ResponseEntity<CommonTemplateDTO> detail(@PathVariable @Encrypt Long templateId) {
        return Results.success(this.commonTemplateService.detail(null, templateId));
    }

    @ApiOperation(value = "创建通用模板")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    @ProcessLovValue(targetField = FIELD_BODY)
    public ResponseEntity<CommonTemplateDTO> create(@RequestBody CommonTemplateCreationDTO creationDTO) {
        return Results.success(this.commonTemplateService.creation(creationDTO));
    }

    @ApiOperation(value = "修改通用模板")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping("/{templateId}")
    @ProcessLovValue(targetField = FIELD_BODY)
    public ResponseEntity<CommonTemplateDTO> update(@PathVariable @Encrypt Long templateId,
                                                    @RequestBody CommonTemplateUpdateDTO updateDTO) {
        return Results.success(this.commonTemplateService.update(null, templateId, updateDTO));
    }

    @ApiOperation(value = "渲染模板")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping(value = "/render")
    public ResponseEntity<RenderResult> render(@RequestBody RenderParameterDTO parameter) {
        parameter.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
        return Results.success(this.commonTemplateService.render(parameter.getTenantId(), parameter.getTemplateCode(),
                parameter.getLang(), parameter.getArgs()));
    }
}
