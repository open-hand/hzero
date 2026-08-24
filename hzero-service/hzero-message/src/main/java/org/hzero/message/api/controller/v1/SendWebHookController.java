package org.hzero.message.api.controller.v1;

import org.hzero.boot.message.entity.WebHookSender;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.app.service.WebHookSendService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * <p>
 * WebHook消息发送API
 * </p>
 *
 * @author qingsheng.chen 2018/8/2 星期四 14:10
 */
@Api(tags = MessageSwaggerApiConfig.SEND_WEBHOOK)
@RestController("sendWebHookController.v1")
@RequestMapping("/v1/{organizationId}/messages/webhook")
public class SendWebHookController extends BaseController {

    private final WebHookSendService webHookSendService;

    @Autowired
    public SendWebHookController(WebHookSendService webHookSendService) {
        this.webHookSendService = webHookSendService;
    }

    @ApiOperation(value = "发送一条WebHook消息，指定模板和参数")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<Message> sendMessageWithTemplate(@PathVariable @ApiParam(value = "租户ID", required = true) long organizationId,
                                                           @RequestBody WebHookSender messageSender) {
        messageSender.setTenantId(organizationId);
        return Results.success(webHookSendService.sendWebHookMessage(messageSender));
    }
}
