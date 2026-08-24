package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.app.service.TemplateConfigService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.TemplateConfig;
import org.hzero.platform.domain.repository.TemplateConfigRepository;
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
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 模板配置 管理 API
 *
 * @author xiaoyu.zhao@hand-china.com 2019-06-28 10:42:49
 */
@Api(tags = PlatformSwaggerApiConfig.TEMPLATE_CONFIG)
@RestController("templateConfigController.v1")
@RequestMapping("/v1/{organizationId}/template-configs")
public class TemplateConfigController extends BaseController {

    @Autowired
    private TemplateConfigRepository templateConfigRepository;
    @Autowired
    private TemplateConfigService templateConfigService;

    @ApiOperation(value = "模板配置列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiImplicitParams({@ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path"),
            @ApiImplicitParam(name = "templateAssignId", value = "模板分配Id", paramType = "path"),
            @ApiImplicitParam(name = "configCode", value = "配置编码", paramType = "query"),
            @ApiImplicitParam(name = "configTypeCode", value = "配置类型编码", paramType = "query"),
            @ApiImplicitParam(name = "remark", value = "备注信息", paramType = "query")})
    @GetMapping("/{templateAssignId}")
    public ResponseEntity<Page<TemplateConfig>> pageTemplateConfigs(
                    @PathVariable("organizationId") Long tenantId,
                    @PathVariable("templateAssignId") @Encrypt Long templateAssignId,
                    @Encrypt TemplateConfig templateConfig,
                    @ApiIgnore @SortDefault(value = TemplateConfig.FIELD_ORDER_SEQ, direction = Sort.Direction.ASC) PageRequest pageRequest) {
        return Results.success(templateConfigRepository.selectTemplateConfigs(templateConfig.setTenantId(tenantId)
                        .setTemplateAssignId(templateAssignId), pageRequest));
    }

    @ApiOperation(value = "模板配置明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("details/{configId}")
    public ResponseEntity<TemplateConfig> selectTemplateConfigDetails(@PathVariable @Encrypt Long configId) {
        return Results.success(templateConfigRepository.selectTemplateConfigDetails(configId));
    }

    @ApiOperation(value = "创建模板配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{templateAssignId}")
    public ResponseEntity<TemplateConfig> createTemplateConfig(@RequestBody @Encrypt TemplateConfig templateConfig,
                    @PathVariable("templateAssignId") @Encrypt Long templateAssignId,
                    @PathVariable("organizationId") Long tenantId) {
        templateConfig.setTenantId(tenantId).setTemplateAssignId(templateAssignId);
        validObject(templateConfig);
        return Results.success(templateConfigService.createTemplateConfig(templateConfig));
    }

    @ApiOperation(value = "修改模板配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/{templateAssignId}")
    public ResponseEntity<TemplateConfig> updateTemplateConfig(@RequestBody @Encrypt TemplateConfig templateConfig,
                    @PathVariable("templateAssignId") @Encrypt Long templateAssignId) {
        SecurityTokenHelper.validToken(templateConfig);
        templateConfig.setTemplateAssignId(templateAssignId);
        validObject(templateConfig);
        return Results.success(templateConfigService.updateTemplateConfig(templateConfig));
    }

    @ApiOperation(value = "批量删除模板配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/batch-delete")
    public ResponseEntity removeTemplateConfig(@RequestBody @Encrypt List<TemplateConfig> templateConfigs) {
        SecurityTokenHelper.validToken(templateConfigs);
        templateConfigService.removeTemplateConfig(templateConfigs);
        return Results.success();
    }

}
