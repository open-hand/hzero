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
@Api(tags = ImportSwaggerApiConfig.TEMPLATE_HEADER_SITE)
@RestController("templateHeaderSiteController.v1")
@RequestMapping(value = "/v1/template-headers")
public class TemplateHeaderSiteController extends BaseController {

    private final TemplateHeaderService headerService;

    @Autowired
    public TemplateHeaderSiteController(TemplateHeaderService headerService) {
        this.headerService = headerService;
    }

    @ApiOperation(value = "分页查询模板头信息列表")
    @GetMapping
    @Permission(level = ResourceLevel.SITE)
    @CustomPageRequest
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户ID", paramType = "query"),
            @ApiImplicitParam(name = "templateCode", value = "模板编码", paramType = "query"),
            @ApiImplicitParam(name = "templateName", value = "模板名称", paramType = "query")
    })
    public ResponseEntity<Page<TemplateHeader>> pageTemplateHeader(Long tenantId, String templateCode, String templateName,
                                                                   @ApiIgnore @SortDefault(value = TemplateHeader.FIELD_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(headerService.pageTemplateHeader(templateCode, templateName, tenantId, pageRequest));
    }

    @ApiOperation(value = "模板信息明细")
    @GetMapping(value = "/{templateId}")
    @Permission(level = ResourceLevel.SITE)
    @ProcessLovValue(targetField = {BaseConstants.FIELD_BODY, BaseConstants.FIELD_BODY + ".templateTargetList"})
    public ResponseEntity<TemplateHeader> detailTemplateHeader(@PathVariable @ApiParam(value = "模板id", required = true) @Encrypt Long templateId) {
        return Results.success(headerService.detailTemplateHeader(templateId, null));
    }

    @ApiOperation(value = "创建模板和目标服务信息")
    @PostMapping
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity<TemplateHeader> createTemplateHeader(@RequestBody @Encrypt TemplateHeader templateHeader) {
        validObject(templateHeader);
        if (HimpConstants.TemplateType.SERVER.equals(templateHeader.getTemplateType())) {
            validList(templateHeader.getTemplateTargetList(), TemplateHeader.ServerImport.class);
        }
        return Results.created(headerService.createTemplateHeader(templateHeader));
    }

    @ApiOperation(value = "修改模板和目标服务信息")
    @PutMapping
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity<TemplateHeader> updateTemplateHeader(@RequestBody @Encrypt TemplateHeader templateHeader) {
        validObject(templateHeader);
        if (HimpConstants.TemplateType.SERVER.equals(templateHeader.getTemplateType())) {
            validList(templateHeader.getTemplateTargetList(), TemplateHeader.ServerImport.class);
        }
        SecurityTokenHelper.validTokenIgnoreInsert(templateHeader);
        return Results.success(headerService.updateTemplateHeader(templateHeader));
    }


    @ApiOperation(value = "删除模板信息")
    @DeleteMapping
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity<Void> deleteTemplateHeader(@RequestBody @Encrypt TemplateHeader templateHeader) {
        SecurityTokenHelper.validToken(templateHeader);
        headerService.deleteTemplateHeader(templateHeader.getId());
        return Results.success();
    }

    @ApiOperation(value = "删除模板目标")
    @DeleteMapping("/target")
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity<Void> deleteTemplateTarget(@RequestBody @Encrypt TemplateTarget templateTarget) {
        SecurityTokenHelper.validToken(templateTarget);
        headerService.deleteTemplateTarget(templateTarget.getId());
        return Results.success();
    }
}
