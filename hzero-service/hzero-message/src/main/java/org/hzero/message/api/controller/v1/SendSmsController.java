package org.hzero.message.api.controller.v1;

import org.hzero.boot.message.entity.MessageSender;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.app.service.SmsSendService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * <p>
 * 短信消息发送API
 * </p>
 *
 * @author qingsheng.chen 2018/8/3 星期五 9:46
 */
@Api(tags = MessageSwaggerApiConfig.SMS_MESSAGE)
@RestController("sendSmsController.v1")
@RequestMapping("/v1/{organizationId}/messages/sms")
public class SendSmsController extends BaseController {

    private final SmsSendService smsSendService;

    @Autowired
    public SendSmsController(SmsSendService smsSendService) {
        this.smsSendService = smsSendService;
    }

    @ApiOperation(value = "发送一条短信消息，指定模板和参数")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/template")
    public ResponseEntity<Message> sendMessageWithTemplate(@PathVariable @ApiParam(value = "租户ID", required = true) long organizationId,
                                                           @RequestBody MessageSender messageSender) {
        Assert.isTrue(StringUtils.hasText(messageSender.getMessageCode()), BaseConstants.ErrorCode.DATA_INVALID);
        validObject(messageSender.setTenantId(organizationId), MessageSender.Sms.class);
        return Results.success(smsSendService.sendMessage(messageSender));
    }
}
