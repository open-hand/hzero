package org.hzero.message.api.controller.v1;

import org.hzero.boot.message.entity.WebHookSender;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.app.service.WebHookSendService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 站内消息发送API
 * </p>
 *
 * @author qingsheng.chen 2018/8/2 星期四 14:10
 */
@Api(tags = MessageSwaggerApiConfig.SEND_WEBHOOK_SITE)
@RestController("sendWebHookSiteController.v1")
@RequestMapping("/v1/messages/webhook")
public class SendWebHookSiteController extends BaseController {
    private WebHookSendService webHookSendService;

    @Autowired
    public SendWebHookSiteController(WebHookSendService webHookSendService) {
        this.webHookSendService = webHookSendService;
    }

    @ApiOperation(value = "发送一条WebHook消息，指定模板和参数")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<Message> sendMessageWithTemplate(@RequestBody WebHookSender messageSender) {
        messageSender.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
        return Results.success(webHookSendService.sendWebHookMessage(messageSender));
    }
}
