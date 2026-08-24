package org.hzero.imported.api.controller.v1;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.imported.app.service.TemplateHeaderService;
import org.hzero.imported.config.ImportSwaggerApiConfig;
import org.hzero.imported.domain.entity.TemplateHeader;
import org.hzero.imported.domain.entity.TemplateTarget;
import org.hzero.imported.infra.constant.HimpConstants;
import org.hzero.mybatis.helper.SecurityTokenHelper;
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
import io.swagger.annotations.*;


/**
 * 模板头管理接口
 *
 * @author shuangfei.zhu@hand-china.com 2018-12-7 16:42:53
 */
@Api(tags = ImportSwaggerApiConfig.TEMPLATE_HEADER)
@RestController("templateHeaderController.v1")
@RequestMapping(value = "/v1/{organizationId}/template-headers")
public class TemplateHeaderController extends BaseController {

    private final TemplateHeaderService headerService;

    @Autowired
    public TemplateHeaderController(TemplateHeaderService headerService) {
        this.headerService = headerService;
    }

    @ApiOperation(value = "分页查询模板头信息列表")
    @GetMapping
    @Permission(level = ResourceLevel.ORGANIZATION)
    @CustomPageRequest
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户Id", paramType = "path", required = true),
            @ApiImplicitParam(name = "templateCode", value = "模板编码", paramType = "query"),
            @ApiImplicitParam(name = "templateName", value = "模板名称", paramType = "query")
    })
    public ResponseEntity<Page<TemplateHeader>> pageTemplateHeader(@PathVariable Long organizationId, String templateCode, String templateName,
                                                                   @ApiIgnore @SortDefault(value = TemplateHeader.FIELD_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(headerService.pageTemplateHeader(templateCode, templateName, organizationId, pageRequest));
    }

    @ApiOperation(value = "模板信息明细")
    @GetMapping(value = "/{templateId}")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = {BaseConstants.FIELD_BODY, BaseConstants.FIELD_BODY + ".templateTargetList"})
    public ResponseEntity<TemplateHeader> detailTemplateHeader(@PathVariable Long organizationId,
                                                               @PathVariable @ApiParam(value = "模板id", required = true) @Encrypt Long templateId) {
        return Results.success(headerService.detailTemplateHeader(templateId, organizationId));
    }

    @ApiOperation(value = "创建模板和目标服务信息")
    @PostMapping
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<TemplateHeader> createTemplateHeader(@PathVariable @ApiParam(value = "租户Id", required = true) Long organizationId,
                                                               @RequestBody @Encrypt TemplateHeader templateHeader) {
        validObject(templateHeader.setTenantId(organizationId));
        if (HimpConstants.TemplateType.SERVER.equals(templateHeader.getTemplateType())) {
            validList(templateHeader.getTemplateTargetList(), TemplateHeader.ServerImport.class);
        }
        return Results.created(headerService.createTemplateHeader(templateHeader));
    }

    @ApiOperation(value = "修改模板和目标服务信息")
    @PutMapping
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<TemplateHeader> updateTemplateHeader(@PathVariable @ApiParam(value = "租户Id", required = true) Long organizationId,
                                                               @RequestBody @Encrypt TemplateHeader templateHeader) {
        validObject(templateHeader.setTenantId(organizationId));
        if (HimpConstants.TemplateType.SERVER.equals(templateHeader.getTemplateType())) {
            validList(templateHeader.getTemplateTargetList(), TemplateHeader.ServerImport.class);
        }
        SecurityTokenHelper.validTokenIgnoreInsert(templateHeader);
        return Results.success(headerService.updateTemplateHeader(templateHeader));
    }


    @ApiOperation(value = "删除模板信息")
    @DeleteMapping
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<Void> deleteTemplateHeader(@PathVariable @ApiParam(value = "租户Id", required = true) Long organizationId,
                                                     @RequestBody @Encrypt TemplateHeader templateHeader) {
        SecurityTokenHelper.validToken(templateHeader.setTenantId(organizationId));
        headerService.deleteTemplateHeader(templateHeader.getId());
        return Results.success();
    }

    @ApiOperation(value = "删除模板目标")
    @DeleteMapping("/target")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<Void> deleteTemplateTarget(@RequestBody @Encrypt TemplateTarget templateTarget) {
        SecurityTokenHelper.validToken(templateTarget);
        headerService.deleteTemplateTarget(templateTarget.getId());
        return Results.success();
    }
}
