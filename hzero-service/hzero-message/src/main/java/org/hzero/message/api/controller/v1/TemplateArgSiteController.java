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
@Api(tags = MessageSwaggerApiConfig.TEMPLATE_ARG_SITE)
@RestController("templateArgSiteController.v1")
@RequestMapping("/v1/template-args")
public class TemplateArgSiteController extends BaseController {

    private final TemplateArgRepository templateArgRepository;
    private final TemplateArgService templateArgService;

    @Autowired
    public TemplateArgSiteController(TemplateArgRepository templateArgRepository, TemplateArgService templateArgService) {
        this.templateArgRepository = templateArgRepository;
        this.templateArgService = templateArgService;
    }

    @ApiOperation(value = "查询消息模板参数列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "argName", value = "SQL参数名", paramType = "query")
    })
    @Permission(level = ResourceLevel.SITE)
    @CustomPageRequest
    @GetMapping("/{templateId}")
    public ResponseEntity<Page<TemplateArg>> listTemplateArgs(@Encrypt @PathVariable("templateId") Long templateId,
                                                              String argName,
                                                              @ApiIgnore @SortDefault(value = TemplateArg.FIELD_ARG_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(templateArgService.pageTemplateArgs(templateId, argName, pageRequest));
    }

    @ApiOperation(value = "初始化消息模板参数列表")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/init")
    public ResponseEntity initTemplateArgs(@Encrypt Long templateId) {
        templateArgService.initTemplateArgs(templateId, null);
        return Results.success();
    }

    @ApiOperation(value = "修改消息模板参数")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity updateTemplateArgs(@Encrypt @RequestBody TemplateArg templateArg) {
        return Results.success(templateArgService.updateTemplateArgs(templateArg));
    }

    @ApiOperation(value = "删除消息模板参数")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity deleteTemplateArgs(@Encrypt @RequestBody TemplateArg templateArg) {
        return Results.success(templateArgRepository.deleteByPrimaryKey(templateArg.getArgId()));
    }

}
