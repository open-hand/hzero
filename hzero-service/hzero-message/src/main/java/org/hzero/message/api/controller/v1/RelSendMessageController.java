package org.hzero.message.api.controller.v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.boot.message.config.MessageClientProperties;
import org.hzero.boot.message.entity.AllSender;
import org.hzero.boot.message.entity.DingTalkSender;
import org.hzero.boot.message.entity.MessageSender;
import org.hzero.boot.message.entity.WeChatSender;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.app.service.RelSendMessageService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.Message;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

/**
 * <p>
 * 关联消息发送
 * </p>
 *
 * @author qingsheng.chen 2018/10/7 星期日 15:41
 */
@Api(tags = MessageSwaggerApiConfig.REL_MESSAGE)
@RestController("relSendController.v1")
@RequestMapping("/v1/{organizationId}/message/relevance")
public class RelSendMessageController extends BaseController {

    private final RelSendMessageService relSendMessageService;
    private final MessageClientProperties messageClientProperties;

    @Autowired
    public RelSendMessageController(RelSendMessageService relSendMessageService,
                                    MessageClientProperties messageClientProperties) {
        this.relSendMessageService = relSendMessageService;
        this.messageClientProperties = messageClientProperties;
    }

    @ApiOperation(value = "关联发送消息，邮件/短信/站内信")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<Map<String, Integer>> sendMessage(@PathVariable("organizationId") Long organizationId, @RequestBody @Encrypt MessageSender messageSender) {
        messageSender.setTenantId(organizationId);
        validObject(messageSender);
        return Results.success(relSendMessageService.relSendMessage(messageSender));
    }

    @ApiOperation(value = "关联发送消息，邮件/短信/站内信(返回发送结果，若服务端开启了异步发送则不返回)")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/with-receipt")
    public ResponseEntity<List<Message>> sendMessageWithReceipt(@PathVariable("organizationId") Long organizationId, @RequestBody @Encrypt MessageSender messageSender) {
        messageSender.setTenantId(organizationId);
        validObject(messageSender);
        return Results.success(relSendMessageService.relSendMessageReceipt(messageSender));
    }

    @ApiOperation(value = "关联发送消息，邮件/短信/站内信/微信/钉钉")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/all")
    public ResponseEntity<Map<String, Integer>> sendAllMessage(@PathVariable("organizationId") Long organizationId, @RequestBody @Encrypt AllSender sender) {
        Map<String, Integer> result = new HashMap<>(16);
        MessageSender messageSender = sender.getMessageSender();
        WeChatSender weChatSender = sender.getWeChatSender();
        DingTalkSender dingTalkSender = sender.getDingTalkSender();
        if (messageSender != null) {
            messageSender.setTenantId(organizationId);
            validObject(messageSender);
            result.putAll(relSendMessageService.relSendMessage(messageSender));
        }
        if (weChatSender != null) {
            weChatSender.setTenantId(organizationId);
            validObject(weChatSender);
            weChatSender.setLang(StringUtils.hasText(weChatSender.getLang()) ? weChatSender.getLang() : messageClientProperties.getDefaultLang());
            result.putAll(relSendMessageService.relSendMessage(weChatSender));
        }
        if (dingTalkSender != null) {
            dingTalkSender.setTenantId(organizationId);
            validObject(dingTalkSender);
            dingTalkSender.setLang(StringUtils.hasText(dingTalkSender.getLang()) ? dingTalkSender.getLang() : messageClientProperties.getDefaultLang());
            result.putAll(relSendMessageService.relSendMessage(dingTalkSender));
        }
        return Results.success(result);
    }

    @ApiOperation(value = "关联发送消息，邮件/短信/站内信/微信/钉钉(返回发送结果，若服务端开启了异步发送则不返回)")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/all/with-receipt")
    public ResponseEntity<List<Message>> sendAllMessageWithReceipt(@PathVariable("organizationId") Long organizationId, @RequestBody @Encrypt AllSender sender) {
        List<Message> result = new ArrayList<>();
        MessageSender messageSender = sender.getMessageSender();
        WeChatSender weChatSender = sender.getWeChatSender();
        DingTalkSender dingTalkSender = sender.getDingTalkSender();
        if (messageSender != null) {
            messageSender.setTenantId(organizationId);
            validObject(messageSender);
            result.addAll(relSendMessageService.relSendMessageReceipt(messageSender));
        }
        if (weChatSender != null) {
            weChatSender.setTenantId(organizationId);
            validObject(weChatSender);
            weChatSender.setLang(StringUtils.hasText(weChatSender.getLang()) ? weChatSender.getLang() : messageClientProperties.getDefaultLang());
            result.addAll(relSendMessageService.relSendMessageReceipt(weChatSender));
        }
        if (dingTalkSender != null) {
            dingTalkSender.setTenantId(organizationId);
            validObject(dingTalkSender);
            dingTalkSender.setLang(StringUtils.hasText(dingTalkSender.getLang()) ? dingTalkSender.getLang() : messageClientProperties.getDefaultLang());
            result.addAll(relSendMessageService.relSendMessageReceipt(dingTalkSender));
        }
        return Results.success(result);
    }
}
