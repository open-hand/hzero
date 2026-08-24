package org.hzero.message.api.controller.v1;

import org.hzero.boot.message.entity.MessageSender;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.app.service.WebSendService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * <p>
 * 站内消息发送API
 * </p>
 *
 * @author qingsheng.chen 2018/8/2 星期四 14:10
 */
@Api(tags = MessageSwaggerApiConfig.WEB_MESSAGE)
@RestController("sendWebController.v1")
@RequestMapping("/v1/{organizationId}/messages/web")
public class SendWebController extends BaseController {
    private WebSendService webSendService;

    @Autowired
    public SendWebController(WebSendService webSendService) {
        this.webSendService = webSendService;
    }

    @ApiOperation(value = "发送一条站内消息，指定模板和参数")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<Message> sendMessageWithTemplate(@PathVariable @ApiParam(value = "租户ID", required = true) long organizationId,
                                                           @RequestBody MessageSender messageSender) {
        Assert.notNull(messageSender.getMessageCode(), BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(messageSender.getTenantId(), BaseConstants.ErrorCode.DATA_INVALID);
        validObject(messageSender.setTenantId(organizationId), MessageSender.WebMessage.class);
        return Results.success(webSendService.sendMessage(messageSender.setTenantId(organizationId)));
    }
}
