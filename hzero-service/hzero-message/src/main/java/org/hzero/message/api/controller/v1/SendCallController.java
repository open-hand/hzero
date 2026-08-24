package org.hzero.message.api.controller.v1;


import org.hzero.boot.message.entity.MessageSender;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.app.service.CallSendService;
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
 * 语音消息发送API
 * <p>
 * @author shuangfei.zhu@hand-china.com 2020/02/27 13:55
 */
@Api(tags = MessageSwaggerApiConfig.CALL_MESSAGE)
@RestController("sendCallController.v1")
@RequestMapping("/v1/{organizationId}/messages/call")
public class SendCallController extends BaseController {

    private final CallSendService callSendService;

    @Autowired
    public SendCallController(CallSendService callSendService) {
        this.callSendService = callSendService;
    }

    @ApiOperation(value = "发送一条短信消息，指定模板和参数")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/template")
    public ResponseEntity<Message> sendMessageWithTemplate(@PathVariable @ApiParam(value = "租户ID", required = true) long organizationId,
                                                           @RequestBody MessageSender messageSender) {
        Assert.isTrue(StringUtils.hasText(messageSender.getMessageCode()), BaseConstants.ErrorCode.DATA_INVALID);
        validObject(messageSender.setTenantId(organizationId), MessageSender.Call.class);
        return Results.success(callSendService.sendMessage(messageSender));
    }
}
