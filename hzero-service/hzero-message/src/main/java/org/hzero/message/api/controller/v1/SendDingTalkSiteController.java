package org.hzero.message.api.controller.v1;

import org.hzero.boot.message.config.MessageClientProperties;
import org.hzero.boot.message.entity.DingTalkSender;
import org.hzero.boot.message.entity.MessageSender;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.app.service.DingTalkSendService;
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
 * 钉钉发送消息
 *
 * @author zifeng.ding@hand-china.com 2019/11/14 9:14
 */
@Api(tags = MessageSwaggerApiConfig.DING_TALK_MESSAGE_SITE)
@RestController("sendDingTalkSiteController.v1")
@RequestMapping("/v1/messages/dingtalk")
public class SendDingTalkSiteController extends BaseController {

    @Autowired
    private MessageClientProperties messageClientProperties;
    @Autowired
    private DingTalkSendService dingTalkSendService;

    @ApiOperation(value = "发送钉钉消息，指定模板和参数")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<Message> sendDingTalk(@RequestBody DingTalkSender dingTalkSender) {
        dingTalkSender.setTenantId(BaseConstants.DEFAULT_TENANT_ID).setLang(StringUtils.hasText(dingTalkSender.getLang()) ? dingTalkSender.getLang() : messageClientProperties.getDefaultLang());
        Assert.notNull(dingTalkSender.getMessageCode(), BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(dingTalkSender.getAgentId(), BaseConstants.ErrorCode.DATA_INVALID);
        validObject(dingTalkSender, MessageSender.WebMessage.class);
        return Results.success(dingTalkSendService.sendMessage(dingTalkSender));
    }
}
