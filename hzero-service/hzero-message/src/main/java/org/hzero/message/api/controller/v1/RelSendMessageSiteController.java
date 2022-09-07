package org.hzero.message.api.controller.v1;

import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.boot.message.config.MessageClientProperties;
import org.hzero.boot.message.entity.AllSender;
import org.hzero.boot.message.entity.DingTalkSender;
import org.hzero.boot.message.entity.MessageSender;
import org.hzero.boot.message.entity.WeChatSender;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.app.service.RelSendMessageService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

/**
 * <p>
 * 关联消息发送
 * </p>
 *
 * @author qingsheng.chen 2018/10/7 星期日 15:41
 */
@Api(tags = MessageSwaggerApiConfig.REL_MESSAGE_SITE)
@RestController("relSendSiteController.v1")
@RequestMapping("/v1/message/relevance")
public class RelSendMessageSiteController extends BaseController {

    private final RelSendMessageService relSendMessageService;
    private final MessageClientProperties messageClientProperties;

    @Autowired
    public RelSendMessageSiteController(RelSendMessageService relSendMessageService,
                                        MessageClientProperties messageClientProperties) {
        this.relSendMessageService = relSendMessageService;
        this.messageClientProperties = messageClientProperties;
    }

    @ApiOperation(value = "关联发送消息")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<Map<String, Integer>> sendMessage(@RequestBody @Encrypt MessageSender messageSender) {
        if (messageSender.getTenantId() == null) {
            messageSender.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
        }
        validObject(messageSender);
        return Results.success(relSendMessageService.relSendMessage(messageSender));
    }

    @ApiOperation(value = "关联发送消息，邮件/短信/站内信/微信")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/all")
    public ResponseEntity<Map<String, Integer>> sendAllMessage(@RequestBody @Encrypt AllSender sender) {
        Map<String, Integer> result = new HashMap<>(16);
        MessageSender messageSender = sender.getMessageSender();
        WeChatSender weChatSender = sender.getWeChatSender();
        DingTalkSender dingTalkSender = sender.getDingTalkSender();
        if (messageSender != null) {
            if (messageSender.getTenantId() == null) {
                messageSender.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
            }
            validObject(messageSender);
            result.putAll(relSendMessageService.relSendMessage(messageSender));
        }
        if (weChatSender != null) {
            if (weChatSender.getTenantId() == null) {
                weChatSender.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
            }
            validObject(weChatSender);
            weChatSender.setLang(StringUtils.hasText(weChatSender.getLang()) ? weChatSender.getLang() : messageClientProperties.getDefaultLang());
            result.putAll(relSendMessageService.relSendMessage(weChatSender));
        }
        if (dingTalkSender != null) {
            if (dingTalkSender.getTenantId() == null) {
                dingTalkSender.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
            }
            validObject(dingTalkSender);
            dingTalkSender.setLang(StringUtils.hasText(dingTalkSender.getLang()) ? dingTalkSender.getLang() : messageClientProperties.getDefaultLang());
            result.putAll(relSendMessageService.relSendMessage(dingTalkSender));
        }
        return Results.success(result);
    }
}