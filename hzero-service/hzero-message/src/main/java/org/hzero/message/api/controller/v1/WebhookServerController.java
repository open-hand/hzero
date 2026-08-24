package org.hzero.message.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.app.service.WebhookServerService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.WebhookServer;
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

/**
 * webhook配置 管理 API
 *
 * @author xiaoyu.zhao@hand-china.com 2020-04-26 19:57:46
 */
@Api(tags = MessageSwaggerApiConfig.WEBHOOK_MESSAGE)
@RestController("webhookServerController.v1")
@RequestMapping("/v1/{organizationId}/webhook-servers")
public class WebhookServerController extends BaseController {

    @Autowired
    private WebhookServerService webhookServerService;

    @ApiOperation(value = "webhook配置列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<WebhookServer>> pageWebHookList(@PathVariable("organizationId") Long tenantId,
                                                               WebhookServer webhookServer, @ApiIgnore @SortDefault(value = WebhookServer.FIELD_SERVER_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(webhookServerService.pageWebHookList(pageRequest, webhookServer, tenantId, true));
    }

    @ApiOperation(value = "webhook配置明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{serverId}")
    public ResponseEntity<WebhookServer> getWebHookDetails(@PathVariable("organizationId") Long organizationId,
                                                           @Encrypt @PathVariable("serverId") Long serverId) {
        return Results.success(webhookServerService.getWebHookDetails(organizationId, serverId));
    }

    @ApiOperation(value = "创建webhook配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<WebhookServer> createWebHook(@PathVariable("organizationId") Long organizationId,
                                                       @RequestBody WebhookServer webhookServer) {
        return Results.success(webhookServerService.createWebHook(organizationId, webhookServer));
    }

    @ApiOperation(value = "修改webhook配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<WebhookServer> updateWebHook(@PathVariable("organizationId") Long organizationId,
                                                       @Encrypt @RequestBody WebhookServer webhookServer) {
        return Results.success(webhookServerService.updateWebHook(organizationId, webhookServer));
    }

    @ApiOperation(value = "删除webhook配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity removeWebHook(@PathVariable("organizationId") Long organizationId,
                                        @Encrypt @RequestBody WebhookServer webhookServer) {
        webhookServerService.removeWebHook(webhookServer);
        return Results.success();
    }

}
