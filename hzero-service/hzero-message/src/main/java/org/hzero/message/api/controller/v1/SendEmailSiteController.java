package org.hzero.message.api.controller.v1;

import org.hzero.boot.message.entity.MessageSender;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.app.service.EmailSendService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
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
 * 邮箱消息发送API
 * </p>
 *
 * @author qingsheng.chen 2018/8/3 星期五 9:46
 */
@Api(tags = MessageSwaggerApiConfig.EMAIL_MESSAGE_SITE)
@RestController("sendEmailSiteController.v1")
@RequestMapping("/v1/messages/email")
public class SendEmailSiteController extends BaseController {
    private EmailSendService emailSendService;

    @Autowired
    public SendEmailSiteController(EmailSendService emailSendService) {
        this.emailSendService = emailSendService;
    }

    @ApiOperation(value = "发送一条邮件消息，指定模板和参数")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<Message> sendMessageWithTemplate(@RequestBody MessageSender messageSender) {
        messageSender.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
        validObject(messageSender, MessageSender.Email.class);
        Assert.isTrue(StringUtils.hasText(messageSender.getMessageCode()), BaseConstants.ErrorCode.DATA_INVALID);
        return Results.success(emailSendService.sendMessage(messageSender));
    }
}
