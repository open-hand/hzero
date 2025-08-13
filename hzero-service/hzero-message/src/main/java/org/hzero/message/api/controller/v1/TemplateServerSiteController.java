package org.hzero.message.api.controller.v1;


import io.swagger.annotations.*;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.app.service.TemplateServerService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.TemplateServer;
import org.hzero.message.domain.entity.TemplateServerLine;
import org.hzero.mybatis.helper.SecurityTokenHelper;
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
 * 消息模板账户关联 管理 API
 *
 * @author zhiying.dong@hand-china.com 2018-09-07 11:09:29
 */
@Api(tags = MessageSwaggerApiConfig.TEMPLATE_SERVER_SITE)
@RestController("templateServerSiteController.v1")
@RequestMapping("/v1/template-servers")
public class TemplateServerSiteController extends BaseController {

    private final TemplateServerService templateServerService;

    @Autowired
    public TemplateServerSiteController(TemplateServerService templateServerService) {
        this.templateServerService = templateServerService;
    }

    @ApiOperation("消息模板账户关联：查询消息模板")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    @CustomPageRequest
    @ProcessLovValue(targetField = {"body.serverList", BaseConstants.FIELD_BODY})
    public ResponseEntity<Page<TemplateServer>> pageTemplate(@RequestParam(required = false) @ApiParam("租户ID") Long tenantId,
                                                             @RequestParam(required = false) @ApiParam("消息代码") String messageCode,
                                                             @RequestParam(required = false) @ApiParam("消息名称") String messageName,
                                                             @ApiIgnore @SortDefault(value = TemplateServer.FIELD_TEMP_SERVER_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(templateServerService.pageTemplateServer(tenantId, messageCode, messageName, false, pageRequest));
    }

    @ApiOperation("消息模板账户关联：查询消息模板关联的服务")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/line/{tempServerId}")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @CustomPageRequest
    public ResponseEntity<TemplateServer> getTemplateServer(@Encrypt @PathVariable @ApiParam("消息模板ID") long tempServerId) {
        return Results.success(templateServerService.getTemplateServer(null, tempServerId));
    }

    @ApiOperation("消息模板账户关联：查询模板行")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/detail/line")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<List<TemplateServerLine>> getTemplateLines(@RequestParam("tenantId") Long tenantId,
                                                                     @RequestParam("messageCode") String messageCode) {
        return Results.success(templateServerService.listTemplateServerLine(tenantId, messageCode));
    }

    @ApiOperation("消息模板账户关联：查询消息模板关联的服务明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户id", paramType = "path"),
            @ApiImplicitParam(name = "messageCode", value = "消息模板关联编码", paramType = "path")
    })
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/detail")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<TemplateServer> getTemplateServerDetail(@RequestParam("tenantId") Long tenantId,
                                                                  @RequestParam("messageCode") String messageCode) {
        return Results.success(templateServerService.getTemplateServer(tenantId, messageCode));
    }

    @ApiOperation("新增消息模板账户关联")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<TemplateServer> createTemplateServer(@RequestBody TemplateServer templateServer) {
        validObject(templateServer);
        templateServerService.createTemplateServer(templateServer);
        return Results.success(templateServer);
    }

    @ApiOperation(value = "修改消息模板账户")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping("/{tempServerId}")
    public ResponseEntity<TemplateServer> updateTemplateServer(@Encrypt @PathVariable @ApiParam("模板账户关联ID") long tempServerId,
                                                               @Encrypt @RequestBody TemplateServer templateServer) {
        SecurityTokenHelper.validTokenIgnoreInsert(templateServer);
        validObject(templateServer);
        templateServerService.updateTemplateServer(templateServer.setTempServerId(tempServerId));
        return Results.success(templateServer);
    }

    @ApiOperation(value = "删除消息模板账户")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping("/{tempServerId}")
    public ResponseEntity<Void> deleteTemplateServer(@Encrypt @PathVariable @ApiParam("模板账户关联ID") long tempServerId, @Encrypt @RequestBody TemplateServer templateServer) {
        SecurityTokenHelper.validToken(templateServer.setTempServerId(tempServerId));
        templateServerService.deleteTemplateServer(templateServer);
        return Results.success();
    }

    @ApiOperation(value = "删除消息模板账户行")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping("/line/{tempServerLineId}")
    public ResponseEntity<Void> deleteTemplateServerLine(@Encrypt @PathVariable @ApiParam("模板账户关联行ID") long tempServerLineId, @Encrypt @RequestBody TemplateServerLine templateServerLine) {
        templateServerService.deleteTemplateServerLine(tempServerLineId, templateServerLine);
        return Results.success();
    }
}
