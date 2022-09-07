package org.hzero.message.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.message.config.MessageClientProperties;
import org.hzero.boot.message.constant.HmsgBootConstant;
import org.hzero.boot.message.entity.Message;
import org.hzero.boot.message.entity.*;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.ResponseUtils;
import org.hzero.message.app.service.*;
import org.hzero.message.domain.entity.TemplateServerLine;
import org.hzero.message.domain.entity.*;
import org.hzero.message.domain.repository.TemplateServerLineRepository;
import org.hzero.message.domain.repository.TemplateServerRepository;
import org.hzero.message.domain.repository.TemplateServerWhRepository;
import org.hzero.message.domain.repository.WebhookServerRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.message.infra.feign.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * 根据typeCode发送消息
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/18 16:13
 */
public class RelSendMessageServiceImpl implements RelSendMessageService {

    @Autowired
    private WebSendService webSendService;
    @Autowired
    private EmailSendService emailSendService;
    @Autowired
    private SmsSendService smsSendService;
    @Autowired
    private CallSendService callSendService;
    @Autowired
    private TemplateServerService templateServerService;
    @Autowired
    private TemplateServerRepository templateServerRepository;
    @Autowired
    private TemplateServerLineRepository templateServerLineRepository;
    @Autowired
    private MessageReceiverService messageReceiverService;
    @Autowired
    private WeChatSendService weChatSendService;
    @Autowired
    private DingTalkSendService dingTalkSendService;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private PlatformService platformService;
    @Autowired
    private TemplateServerWhRepository templateServerWhRepository;
    @Autowired
    private WebHookSendService webHookSendService;
    @Autowired
    private WebhookServerRepository webhookServerRepository;
    @Autowired
    private MessageClientProperties messageClientProperties;

    @Override
    public Map<String, Integer> relSendMessage(MessageSender messageSender) {
        List<org.hzero.message.domain.entity.Message> result = relSendMessageReceipt(messageSender);
        Map<String, Integer> map = new HashMap<>(16);
        for (org.hzero.message.domain.entity.Message message : result) {
            // 有一条记录是成功，该消息类型就显示成功
            if (BaseConstants.Flag.YES.equals(map.get(message.getMessageTypeCode()))) {
                continue;
            }
            map.put(message.getMessageTypeCode(), message.getSendFlag());
        }
        return map;
    }

    @Override
    public List<org.hzero.message.domain.entity.Message> relSendMessageReceipt(MessageSender messageSender) {
        if (messageSender.getReceiverAddressList() == null) {
            messageSender.setReceiverAddressList(Collections.emptyList());
        }
        // 检查接收人列表
        messageSender = messageReceiverService.queryReceiver(messageSender);
        TemplateServer templateServer = templateServerService.getTemplateServer(messageSender.getTenantId(), messageSender.getMessageCode());
        Assert.notNull(templateServer, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        Assert.isTrue(Objects.equals(templateServer.getEnabledFlag(), BaseConstants.Flag.YES), BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        // 未指定接收配置编码，使用发送配置编码作为接收编码
        if (StringUtils.isBlank(messageSender.getReceiveConfigCode())) {
            messageSender.setReceiveConfigCode(templateServer.getMessageCode());
        }
        Map<String, List<TemplateServerLine>> serverLineMap = templateServerLineRepository.enabledTemplateServerLine(templateServer.getTempServerId(), templateServer.getTenantId())
                .stream().collect(Collectors.groupingBy(TemplateServerLine::getTypeCode));
        return sendMessage(serverLineMap, messageSender);
    }

    private boolean sendEnable(List<String> typeCodeList, String typeCode) {
        return CollectionUtils.isEmpty(typeCodeList) || typeCodeList.contains(typeCode);
    }

    private List<org.hzero.message.domain.entity.Message> sendMessage(Map<String, List<TemplateServerLine>> serverLineMap, MessageSender messageSender) {
        List<org.hzero.message.domain.entity.Message> result = new ArrayList<>();
        // 每种类型的模板只能指定一个
        // 模板允许站内消息 且 (不指定发送方式且userId不为空 或 发送方式中指定了站内消息)
        if (serverLineMap.containsKey(HmsgConstant.MessageType.WEB) && sendEnable(messageSender.getTypeCodeList(), HmsgConstant.MessageType.WEB)) {
            sendWeb(serverLineMap, result, new MessageSender(messageSender));
        }
        // 模板允许短信 且 (不指定发送方式且phone不为空 或 发送方式中指定了短信)
        if (serverLineMap.containsKey(HmsgConstant.MessageType.SMS) && sendEnable(messageSender.getTypeCodeList(), HmsgConstant.MessageType.SMS)) {
            sendSms(serverLineMap, result, new MessageSender(messageSender));
        }
        // 模板允许邮件 且 (不指定发送方式且email不为空 或 发送方式中指定了邮件)
        if (serverLineMap.containsKey(HmsgConstant.MessageType.EMAIL) && sendEnable(messageSender.getTypeCodeList(), HmsgConstant.MessageType.EMAIL)) {
            sendEmail(serverLineMap, result, new MessageSender(messageSender));
        }
        // 模板允许语音 且 (不指定发送方式且phone不为空 或 发送方式中指定了语音)
        if (serverLineMap.containsKey(HmsgConstant.MessageType.CALL) && sendEnable(messageSender.getTypeCodeList(), HmsgConstant.MessageType.CALL)) {
            sendCall(serverLineMap, result, new MessageSender(messageSender));
        }
        // 模板允许企业微信 且 (不指定发送方式且userId不为空 或 发送方式中指定了企业微信)
        if (serverLineMap.containsKey(HmsgConstant.MessageType.WC_E) && sendEnable(messageSender.getTypeCodeList(), HmsgConstant.MessageType.WC_E)) {
            sendEnterpriseWeChat(serverLineMap, result, new MessageSender(messageSender));
        }
        // 模板允许微信公众号 且 (不指定发送方式且userId不为空 或 发送方式中指定了微信公众号)
        if (serverLineMap.containsKey(HmsgConstant.MessageType.WC_O) && sendEnable(messageSender.getTypeCodeList(), HmsgConstant.MessageType.WC_O)) {
            sendOfficialWeChat(serverLineMap, result, new MessageSender(messageSender));
        }
        // 模板允许钉钉 且 (不指定发送方式且userId不为空 或 发送方式中指定了钉钉)
        if (serverLineMap.containsKey(HmsgConstant.MessageType.DT) && sendEnable(messageSender.getTypeCodeList(), HmsgConstant.MessageType.DT)) {
            sendDingTalk(serverLineMap, result, new MessageSender(messageSender));
        }
        // 模板允许WebHook
        if (serverLineMap.containsKey(HmsgConstant.MessageType.WEB_HOOK) && sendEnable(messageSender.getTypeCodeList(), HmsgConstant.MessageType.WEB_HOOK)) {
            sendWebHook(serverLineMap, result, new MessageSender(messageSender));
        }
        return result;
    }

    private void sendWeb(Map<String, List<TemplateServerLine>> serverLineMap, List<org.hzero.message.domain.entity.Message> result, MessageSender sender) {
        Map<String, Message> messageMap = sender.getMessageMap();
        if (messageMap == null) {
            messageMap = new HashMap<>(4);
        }
        List<TemplateServerLine> list = serverLineMap.get(HmsgConstant.MessageType.WEB);
        // 根据接收配置过滤接收人
        filterWebReceiver(sender);
        List<Receiver> receivers = sender.getReceiverAddressList().stream().filter(item -> item.getUserId() != null).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(receivers)) {
            MessageSender messageSender = new MessageSender()
                    .setTenantId(sender.getTenantId())
                    .setLang(sender.getLang())
                    .setArgs(sender.getArgs())
                    .setObjectArgs(sender.getObjectArgs());
            if (messageSender.getMessageMap() != null && messageSender.getMessageMap().containsKey(HmsgConstant.MessageType.WEB)) {
                messageSender = messageSender.setMessage(messageSender.getMessageMap().get(HmsgConstant.MessageType.WEB));
            } else {
                messageSender = messageSender.setMessage(null);
            }
            for (TemplateServerLine line : list) {
                org.hzero.message.domain.entity.Message msg = webSendService.sendMessage(messageSender.setMessageCode(line.getTemplateCode()).setReceiverAddressList(receivers).setServerCode(line.getServerCode()).setMessage(messageMap.get(HmsgConstant.MessageType.WEB)));
                result.add(msg == null ? new org.hzero.message.domain.entity.Message().setSendFlag(BaseConstants.Flag.YES).setMessageTypeCode(HmsgConstant.MessageType.WEB) : msg);
            }
        }
    }

    protected void filterWebReceiver(MessageSender sender) {
        for (Receiver item : sender.getReceiverAddressList()) {
            if (item.getUserId() != null &&
                    UserReceiveConfig.check(redisHelper, HmsgConstant.MessageType.WEB, String.valueOf(item.getUserId()), sender.getReceiveConfigCode(), sender.getTenantId())) {
                item.setUserId(null).setTargetUserTenantId(null);
            }
        }
    }

    private void sendSms(Map<String, List<TemplateServerLine>> serverLineMap, List<org.hzero.message.domain.entity.Message> result, MessageSender sender) {
        Map<String, Message> messageMap = sender.getMessageMap();
        if (messageMap == null) {
            messageMap = new HashMap<>(4);
        }
        List<TemplateServerLine> list = serverLineMap.get(HmsgConstant.MessageType.SMS);
        // 根据接收配置过滤接收人
        filterSmsReceiver(sender);
        List<Receiver> receivers = sender.getReceiverAddressList().stream().filter(item -> item.getPhone() != null).distinct().collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(receivers)) {
            MessageSender messageSender = new MessageSender()
                    .setTenantId(sender.getTenantId())
                    .setLang(sender.getLang())
                    .setArgs(sender.getArgs())
                    .setObjectArgs(sender.getObjectArgs());
            if (messageSender.getMessageMap() != null && messageSender.getMessageMap().containsKey(HmsgConstant.MessageType.SMS)) {
                messageSender = messageSender.setMessage(messageSender.getMessageMap().get(HmsgConstant.MessageType.SMS));
            } else {
                messageSender = messageSender.setMessage(null);
            }
            for (TemplateServerLine line : list) {
                org.hzero.message.domain.entity.Message msg = smsSendService.sendMessage(messageSender.setMessageCode(line.getTemplateCode()).setReceiverAddressList(receivers).setServerCode(line.getServerCode()).setMessage(messageMap.get(HmsgConstant.MessageType.SMS)));
                result.add(msg == null ? new org.hzero.message.domain.entity.Message().setSendFlag(BaseConstants.Flag.YES).setMessageTypeCode(HmsgConstant.MessageType.SMS) : msg);
            }
        }
    }

    protected void filterSmsReceiver(MessageSender sender) {
        for (Receiver item : sender.getReceiverAddressList()) {
            if (StringUtils.isNotBlank(item.getPhone()) &&
                    UserReceiveConfig.check(redisHelper, HmsgConstant.MessageType.SMS, item.getPhone(), sender.getReceiveConfigCode(), sender.getTenantId())) {
                item.setPhone(null).setIdd(null);
            }
        }
    }

    private void sendEmail(Map<String, List<TemplateServerLine>> serverLineMap, List<org.hzero.message.domain.entity.Message> result, MessageSender sender) {
        Map<String, Message> messageMap = sender.getMessageMap();
        if (messageMap == null) {
            messageMap = new HashMap<>(4);
        }
        List<TemplateServerLine> list = serverLineMap.get(HmsgConstant.MessageType.EMAIL);
        // 根据接收配置过滤接收人
        filterEmailReceiver(sender);
        List<Receiver> receivers = sender.getReceiverAddressList().stream().filter(item -> item.getEmail() != null).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(receivers)) {
            MessageSender messageSender = new MessageSender()
                    .setTenantId(sender.getTenantId())
                    .setLang(sender.getLang())
                    .setAttachmentList(sender.getAttachmentList())
                    .setCcList(sender.getCcList())
                    .setBccList(sender.getBccList())
                    .setArgs(sender.getArgs())
                    .setObjectArgs(sender.getObjectArgs());
            if (messageSender.getMessageMap() != null && messageSender.getMessageMap().containsKey(HmsgConstant.MessageType.EMAIL)) {
                messageSender = messageSender.setMessage(messageSender.getMessageMap().get(HmsgConstant.MessageType.EMAIL));
            } else {
                messageSender = messageSender.setMessage(null);
            }
            for (TemplateServerLine line : list) {
                org.hzero.message.domain.entity.Message msg = emailSendService.sendMessage(messageSender.setMessageCode(line.getTemplateCode()).setReceiverAddressList(receivers).setServerCode(line.getServerCode()).setMessage(messageMap.get(HmsgConstant.MessageType.EMAIL)).setBatchSend(sender.getBatchSend()), line.getTryTimes());
                result.add(msg == null ? new org.hzero.message.domain.entity.Message().setSendFlag(BaseConstants.Flag.YES).setMessageTypeCode(HmsgConstant.MessageType.EMAIL) : msg);
            }
        }
    }

    protected void filterEmailReceiver(MessageSender sender) {
        for (Receiver item : sender.getReceiverAddressList()) {
            if (StringUtils.isNotBlank(item.getEmail()) &&
                    UserReceiveConfig.check(redisHelper, HmsgConstant.MessageType.EMAIL, item.getEmail(), sender.getReceiveConfigCode(), sender.getTenantId())) {
                item.setEmail(null);
            }
        }
    }

    private void sendCall(Map<String, List<TemplateServerLine>> serverLineMap, List<org.hzero.message.domain.entity.Message> result, MessageSender sender) {
        Map<String, Message> messageMap = sender.getMessageMap();
        if (messageMap == null) {
            messageMap = new HashMap<>(4);
        }
        List<TemplateServerLine> list = serverLineMap.get(HmsgConstant.MessageType.CALL);
        // 根据接收配置过滤接收人
        filterCallReceiver(sender);
        List<Receiver> receivers = sender.getReceiverAddressList().stream().filter(item -> item.getPhone() != null).distinct().collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(receivers)) {
            MessageSender messageSender = new MessageSender()
                    .setTenantId(sender.getTenantId())
                    .setLang(sender.getLang())
                    .setArgs(sender.getArgs())
                    .setObjectArgs(sender.getObjectArgs());
            if (messageSender.getMessageMap() != null && messageSender.getMessageMap().containsKey(HmsgConstant.MessageType.CALL)) {
                messageSender = messageSender.setMessage(messageSender.getMessageMap().get(HmsgConstant.MessageType.CALL));
            } else {
                messageSender = messageSender.setMessage(null);
            }
            for (TemplateServerLine line : list) {
                org.hzero.message.domain.entity.Message msg = callSendService.sendMessage(messageSender.setMessageCode(line.getTemplateCode()).setReceiverAddressList(receivers).setServerCode(line.getServerCode()).setMessage(messageMap.get(HmsgConstant.MessageType.CALL)));
                result.add(msg == null ? new org.hzero.message.domain.entity.Message().setSendFlag(BaseConstants.Flag.YES).setMessageTypeCode(HmsgConstant.MessageType.CALL) : msg);
            }
        }
    }

    protected void filterCallReceiver(MessageSender sender) {
        for (Receiver item : sender.getReceiverAddressList()) {
            if (StringUtils.isNotBlank(item.getPhone()) &&
                    UserReceiveConfig.check(redisHelper, HmsgConstant.MessageType.CALL, item.getPhone(), sender.getReceiveConfigCode(), sender.getTenantId())) {
                item.setPhone(null).setIdd(null);
            }
        }
    }

    private void sendEnterpriseWeChat(Map<String, List<TemplateServerLine>> serverLineMap, List<org.hzero.message.domain.entity.Message> result, MessageSender sender) {
        List<TemplateServerLine> list = serverLineMap.get(HmsgConstant.MessageType.WC_E);
        List<Long> userIdList = sender.getReceiverAddressList().stream().filter(item -> item.getUserId() != null).map(Receiver::getUserId).distinct().collect(Collectors.toList());
        // 根据映射关系获取接收人
        List<String> openUserIdList = ResponseUtils.getResponse(platformService.getOpenUserIdsByUserIds(sender.getTenantId(), StringUtils.join(userIdList, BaseConstants.Symbol.COMMA), HmsgBootConstant.ThirdPlatformType.WX), new TypeReference<List<String>>() {
        });
        WeChatSender weChatSender = new WeChatSender()
                .setTenantId(sender.getTenantId())
                .setReceiveConfigCode(sender.getReceiveConfigCode())
                .setLang(sender.getLang())
                .setArgs(sender.getArgs())
                .setUserIdList(openUserIdList);
        // 根据接收配置过滤接收人
        openUserIdList = filterEnterpriseWeChatReceiver(weChatSender);
        if (!CollectionUtils.isEmpty(openUserIdList)) {
            for (TemplateServerLine line : list) {
                org.hzero.message.domain.entity.Message msg = weChatSendService.sendEnterpriseMessage(weChatSender.setMessageCode(line.getTemplateCode()).setServerCode(line.getServerCode()));
                result.add(msg == null ? new org.hzero.message.domain.entity.Message().setSendFlag(BaseConstants.Flag.YES).setMessageTypeCode(HmsgConstant.MessageType.WC_E) : msg);
            }
        }
    }

    protected List<String> filterEnterpriseWeChatReceiver(WeChatSender sender) {
        List<String> receiverList = new ArrayList<>();
        for (String userId : sender.getUserIdList()) {
            if (!UserReceiveConfig.check(redisHelper, HmsgConstant.MessageType.WC_E, userId, sender.getReceiveConfigCode(), sender.getTenantId())) {
                receiverList.add(userId);
            }
        }
        return receiverList;
    }

    @SuppressWarnings("all")
    private void sendOfficialWeChat(Map<String, List<TemplateServerLine>> serverLineMap, List<org.hzero.message.domain.entity.Message> result, MessageSender sender) {
        // TODO 考虑用户ID和外部openID如何映射
    }

    private void sendDingTalk(Map<String, List<TemplateServerLine>> serverLineMap, List<org.hzero.message.domain.entity.Message> result, MessageSender sender) {
        List<TemplateServerLine> list = serverLineMap.get(HmsgConstant.MessageType.DT);
        List<Long> userIdList = sender.getReceiverAddressList().stream().filter(item -> item.getUserId() != null).map(Receiver::getUserId).distinct().collect(Collectors.toList());
        // 根据映射关系获取接收人
        List<String> openUserIdList = ResponseUtils.getResponse(platformService.getOpenUserIdsByUserIds(sender.getTenantId(), StringUtils.join(userIdList, BaseConstants.Symbol.COMMA), HmsgBootConstant.ThirdPlatformType.DD), new TypeReference<List<String>>() {
        });
        DingTalkSender dingTalkSender = new DingTalkSender()
                .setTenantId(sender.getTenantId())
                .setReceiveConfigCode(sender.getReceiveConfigCode())
                .setLang(sender.getLang())
                .setArgs(sender.getArgs())
                .setUserIdList(openUserIdList);
        // 根据接收配置过滤接收人
        openUserIdList = filterDingTalkReceiver(dingTalkSender);
        if (!CollectionUtils.isEmpty(openUserIdList)) {
            sendDingTalkMessage(list, result, dingTalkSender);
        }
    }

    protected List<String> filterDingTalkReceiver(DingTalkSender sender) {
        List<String> receiverList = new ArrayList<>();
        for (String userId : sender.getUserIdList()) {
            if (!UserReceiveConfig.check(redisHelper, HmsgConstant.MessageType.DT, userId, sender.getReceiveConfigCode(), sender.getTenantId())) {
                receiverList.add(userId);
            }
        }
        return receiverList;
    }

    private void sendWebHook(Map<String, List<TemplateServerLine>> serverLineMap, List<org.hzero.message.domain.entity.Message> result, MessageSender messageSender) {
        if (messageSender.getLang() == null) {
            messageSender.setLang(messageClientProperties.getDefaultLang());
        }
        if (messageSender.getMessageMap() == null) {
            messageSender.setMessageMap(new HashMap<>(4));
        }
        List<TemplateServerLine> list = serverLineMap.get(HmsgConstant.MessageType.WEB_HOOK);
        List<WebHookSender> resultSenders = new ArrayList<>();
        for (TemplateServerLine templateServerLine : list) {
            List<TemplateServerWh> templateServerWhs = templateServerWhRepository.selectByTempServerLineId(templateServerLine.getTempServerLineId());
            if (CollectionUtils.isEmpty(templateServerWhs)) {
                continue;
            }
            // 存在分配的webhook配置，查询对应的webhook配置信息
            for (TemplateServerWh templateServerWh : templateServerWhs) {
                WebhookServer webhookServer = webhookServerRepository.getWebHookByCodeAndTenant(templateServerWh.getTenantId(), templateServerWh.getServerCode());
                if (Objects.isNull(webhookServer)) {
                    continue;
                }
                // 构造发送消息的sender信息
                WebHookSender webHookSender = new WebHookSender()
                        .setServerCode(webhookServer.getServerCode())
                        .setReceiverAddressList(messageSender.getReceiverAddressList())
                        .setTenantId(webhookServer.getTenantId())
                        .setLang(messageSender.getLang())
                        .setArgs(messageSender.getArgs())
                        .setMessageCode(templateServerLine.getTemplateCode())
                        .setMessage(messageSender.getMessageMap().get(HmsgConstant.MessageType.WEB_HOOK));
                resultSenders.add(webHookSender);
            }
        }
        // 过滤接收人
        filterWebHookReceiver(messageSender, resultSenders);
        // 发送消息
        for (WebHookSender resultSender : resultSenders) {
            org.hzero.message.domain.entity.Message msg = webHookSendService.sendWebHookMessage(resultSender);
            result.add(msg == null ? new org.hzero.message.domain.entity.Message().setSendFlag(BaseConstants.Flag.YES).setMessageTypeCode(HmsgConstant.MessageType.WEB_HOOK) : msg);
        }
    }

    /**
     * 过滤webHook接收人
     */
    @SuppressWarnings("all")
    protected void filterWebHookReceiver(MessageSender messageSender, List<WebHookSender> webHookSenderList) {
    }

    @Override
    public Map<String, Integer> relSendMessage(WeChatSender weChatSender) {
        List<org.hzero.message.domain.entity.Message> result = relSendMessageReceipt(weChatSender);
        Map<String, Integer> map = new HashMap<>(16);
        for (org.hzero.message.domain.entity.Message message : result) {
            if (!(HmsgConstant.MessageType.WC_O.equals(message.getMessageTypeCode()) || HmsgConstant.MessageType.WC_E.equals(message.getMessageTypeCode()))) {
                continue;
            }
            // 有一条记录是成功，该消息类型就显示成功
            if (BaseConstants.Flag.YES.equals(map.get(message.getMessageTypeCode()))) {
                continue;
            }
            map.put(message.getMessageTypeCode(), message.getSendFlag());
        }
        return map;
    }

    @Override
    public List<org.hzero.message.domain.entity.Message> relSendMessageReceipt(WeChatSender weChatSender) {
        List<org.hzero.message.domain.entity.Message> result = new ArrayList<>();
        TemplateServer templateServer = templateServerService.getTemplateServer(weChatSender.getTenantId(), weChatSender.getMessageCode());
        Assert.isTrue(templateServer != null && Objects.equals(templateServer.getEnabledFlag(), BaseConstants.Flag.YES), BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        // 检查用户接收配置
        if (StringUtils.isBlank(weChatSender.getReceiveConfigCode())) {
            weChatSender.setReceiveConfigCode(templateServer.getMessageCode());
        }
        weChatSender.setUserIdList(filterEnterpriseWeChatReceiver(weChatSender));
        Map<String, List<TemplateServerLine>> serverLineMap = templateServerLineRepository.enabledTemplateServerLine(templateServer.getTempServerId(), templateServer.getTenantId())
                .stream().collect(Collectors.groupingBy(TemplateServerLine::getTypeCode));
        sendWeChatMessage(serverLineMap, result, weChatSender);
        return result;
    }

    private void sendWeChatMessage(Map<String, List<TemplateServerLine>> serverLineMap, List<org.hzero.message.domain.entity.Message> result, WeChatSender weChatSender) {
        // 模板允许公众号 并且 (不指定发送方式且接收人不为空或发送方式指定了公众号)
        if (serverLineMap.containsKey(HmsgConstant.MessageType.WC_O) && allow(weChatSender.getTypeCodeList(), HmsgConstant.MessageType.WC_O, weChatSender)) {
            for (TemplateServerLine line : serverLineMap.get(HmsgConstant.MessageType.WC_O)) {
                org.hzero.message.domain.entity.Message msg = weChatSendService.sendOfficialMessage(weChatSender.setMessageCode(line.getTemplateCode()).setServerCode(line.getServerCode()));
                result.add(msg == null ? new org.hzero.message.domain.entity.Message().setSendFlag(BaseConstants.Flag.YES).setMessageTypeCode(HmsgConstant.MessageType.WC_O) : msg);
            }
        }
        // 模板允许企业微信 并且 (不指定发送方式且(接收人不为空 或 部门不为空 或 tag不为空)或发送方式指定了企业号)
        if (serverLineMap.containsKey(HmsgConstant.MessageType.WC_E) && allow(weChatSender.getTypeCodeList(), HmsgConstant.MessageType.WC_E, weChatSender)) {
            for (TemplateServerLine line : serverLineMap.get(HmsgConstant.MessageType.WC_E)) {
                org.hzero.message.domain.entity.Message msg = weChatSendService.sendEnterpriseMessage(weChatSender.setMessageCode(line.getTemplateCode()).setServerCode(line.getServerCode()));
                result.add(msg == null ? new org.hzero.message.domain.entity.Message().setSendFlag(BaseConstants.Flag.YES).setMessageTypeCode(HmsgConstant.MessageType.WC_E) : msg);
            }
        }
    }

    private boolean allow(List<String> allowTypeList, String weChatType, WeChatSender weChatSender) {
        if (!CollectionUtils.isEmpty(allowTypeList) && allowTypeList.contains(weChatType)) {
            return true;
        }
        if (Objects.equals(weChatType, HmsgConstant.MessageType.WC_O)) {
            return !CollectionUtils.isEmpty(weChatSender.getUserList());
        } else {
            return !CollectionUtils.isEmpty(weChatSender.getUserIdList()) || !CollectionUtils.isEmpty(weChatSender.getPartyList()) || !CollectionUtils.isEmpty(weChatSender.getTagList());
        }
    }

    @Override
    public Map<String, Integer> relSendMessage(DingTalkSender dingTalkSender) {
        List<org.hzero.message.domain.entity.Message> result = relSendMessageReceipt(dingTalkSender);
        Map<String, Integer> map = new HashMap<>(16);
        for (org.hzero.message.domain.entity.Message message : result) {
            if (!HmsgConstant.MessageType.DT.equals(message.getMessageTypeCode())) {
                continue;
            }
            // 有一条记录是成功，该消息类型就显示成功
            if (BaseConstants.Flag.YES.equals(map.get(message.getMessageTypeCode()))) {
                break;
            }
            map.put(message.getMessageTypeCode(), message.getSendFlag());
        }
        return map;
    }

    @Override
    public List<org.hzero.message.domain.entity.Message> relSendMessageReceipt(DingTalkSender dingTalkSender) {
        List<org.hzero.message.domain.entity.Message> result = new ArrayList<>();
        TemplateServer templateServer = templateServerService.getTemplateServer(dingTalkSender.getTenantId(), dingTalkSender.getMessageCode());
        Assert.isTrue(templateServer != null && Objects.equals(templateServer.getEnabledFlag(), BaseConstants.Flag.YES), BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        // 检查用户接收配置
        if (StringUtils.isBlank(dingTalkSender.getReceiveConfigCode())) {
            dingTalkSender.setReceiveConfigCode(templateServer.getMessageCode());
        }
        dingTalkSender.setUserIdList(filterDingTalkReceiver(dingTalkSender));
        Map<String, List<TemplateServerLine>> serverLineMap = templateServerLineRepository.enabledTemplateServerLine(templateServer.getTempServerId(), templateServer.getTenantId())
                .stream().collect(Collectors.groupingBy(TemplateServerLine::getTypeCode));
        if (serverLineMap.containsKey(HmsgConstant.MessageType.DT)) {
            sendDingTalkMessage(serverLineMap.get(HmsgConstant.MessageType.DT), result, dingTalkSender);
        }
        return result;
    }

    private void sendDingTalkMessage(List<TemplateServerLine> list, List<org.hzero.message.domain.entity.Message> result, DingTalkSender dingTalkSender) {
        for (TemplateServerLine line : list) {
            org.hzero.message.domain.entity.Message msg = dingTalkSendService.sendMessage(dingTalkSender.setMessageCode(line.getTemplateCode()).setServerCode(line.getServerCode()));
            result.add(msg == null ? new org.hzero.message.domain.entity.Message().setSendFlag(BaseConstants.Flag.YES).setMessageTypeCode(HmsgConstant.MessageType.DT) : msg);
        }
    }
}
