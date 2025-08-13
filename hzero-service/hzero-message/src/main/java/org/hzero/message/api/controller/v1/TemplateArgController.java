package org.hzero.message.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.app.service.TemplateArgService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.TemplateArg;
import org.hzero.message.domain.repository.TemplateArgRepository;
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

/**
 * 消息模板参数 管理 API
 *
 * @author fanghan.liu@hand-china.com 2019-10-08 16:49:41
 */
@Api(tags = MessageSwaggerApiConfig.TEMPLATE_ARG)
@RestController("templateArgController.v1")
@RequestMapping("/v1/{organizationId}/template-args")
public class TemplateArgController extends BaseController {

    private final TemplateArgRepository templateArgRepository;
    private final TemplateArgService templateArgService;

    @Autowired
    public TemplateArgController(TemplateArgRepository templateArgRepository, TemplateArgService templateArgService) {
        this.templateArgRepository = templateArgRepository;
        this.templateArgService = templateArgService;
    }

    @ApiOperation(value = "查询消息模板参数列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "argName", value = "SQL参数名", paramType = "query")
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @CustomPageRequest
    @GetMapping("/{templateId}")
    public ResponseEntity<Page<TemplateArg>> listTemplateArgs(@PathVariable("organizationId") Long organizationId,
                                                              @Encrypt @PathVariable("templateId") Long templateId,
                                                              String argName,
                                                              @ApiIgnore @SortDefault(value = TemplateArg.FIELD_ARG_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(templateArgService.pageTemplateArgs(templateId, argName, pageRequest));
    }

    @ApiOperation(value = "初始化消息模板参数列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/init")
    public ResponseEntity<Void> initTemplateArgs(@PathVariable("organizationId") Long organizationId,
                                                 @Encrypt Long templateId) {
        templateArgService.initTemplateArgs(templateId, organizationId);
        return Results.success();
    }

    @ApiOperation(value = "修改消息模板参数")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<TemplateArg> updateTemplateArgs(@PathVariable("organizationId") Long organizationId,
                                                          @Encrypt @RequestBody TemplateArg templateArg) {
        validObject(templateArg);
        SecurityTokenHelper.validToken(templateArg);
        return Results.success(templateArgService.updateTemplateArgs(templateArg));
    }

    @ApiOperation(value = "删除消息模板参数")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<Void> deleteTemplateArgs(@PathVariable("organizationId") Long organizationId,
                                                   @Encrypt @RequestBody TemplateArg templateArg) {
        SecurityTokenHelper.validToken(templateArg);
        templateArgRepository.deleteByPrimaryKey(templateArg.getArgId());
        return Results.success();
    }
}
