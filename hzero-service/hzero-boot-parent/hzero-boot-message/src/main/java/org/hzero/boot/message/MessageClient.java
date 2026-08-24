package org.hzero.boot.message;

import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hzero.boot.message.config.MessageClientProperties;
import org.hzero.boot.message.constant.HmsgBootConstant;
import org.hzero.boot.message.constant.WebSocketConstant;
import org.hzero.boot.message.dto.NoticeDTO;
import org.hzero.boot.message.dto.OnLineUserDTO;
import org.hzero.boot.message.entity.*;
import org.hzero.boot.message.feign.MessageRemoteService;
import org.hzero.boot.message.feign.PlatformRemoteService;
import org.hzero.boot.message.redis.PublishNoticeRedis;
import org.hzero.boot.message.service.*;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;

/**
 * <p>
 * 消息客户端
 * 提供消息生成，接收人获取，消息发送等功能
 * </p>
 *
 * @author qingsheng.chen 2018/8/6 星期一 20:09
 */
public class MessageClient implements MessageGenerator, MessageReceiver, WebMessageSender, EmailSender, SmsSender, RelSender, WeChatMessageSender, DingTalkMessageSender, WebHookMessageSender {
    public static final String ERROR_MESSAGE_EMPTY_RECEIVER = "error.message.empty_receiver";
    private static Object sqlSessionFactory;
    private final MessageRemoteService messageRemoteService;
    private final PlatformRemoteService platformRemoteService;
    private final MessageAsyncService messageAsyncService;
    private final MessageClientProperties messageClientProperties;
    private MessageGenerator messageGenerator;

    private boolean async;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.application.name}")
    private String serviceName;

    public MessageClient(MessageRemoteService messageRemoteService,
                         PlatformRemoteService platformRemoteService,
                         MessageAsyncService messageAsyncService,
                         MessageClientProperties messageClientProperties,
                         MessageGenerator messageGenerator,
                         RedisTemplate<String, String> redisTemplate,
                         ObjectMapper objectMapper) {
        this.messageRemoteService = messageRemoteService;
        this.platformRemoteService = platformRemoteService;
        this.messageAsyncService = messageAsyncService;
        this.messageClientProperties = messageClientProperties;
        this.messageGenerator = messageGenerator;
        this.async = false;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public static void setSqlSessionFactory(Object bean) {
        sqlSessionFactory = bean;
    }

    /**
     * 重发消息
     *
     * @param tenantId      租户Id
     * @param transactionId 消息事务Id
     * @return 发送结果
     */
    public Message resendMessage(long tenantId, long transactionId) {
        return ResponseUtils.getResponse(messageRemoteService.resendMessage(tenantId, transactionId), Message.class);
    }

    @Override
    public Message generateMessage(long tenantId, String templateCode, String serverTypeCode, Map<String, String> args, boolean sqlEnable, String lang) {
        return getMessageGenerator().generateMessage(tenantId, templateCode, serverTypeCode, args, sqlEnable, lang);
    }

    @Override
    public Message generateMessageObjectArgs(long tenantId, String templateCode, String serverTypeCode, Map<String, Object> objectArgs, boolean sqlEnable, String lang) {
        return getMessageGenerator().generateMessageObjectArgs(tenantId, templateCode, serverTypeCode, objectArgs, sqlEnable, lang);
    }

    @Override
    public Map<String, Object> appendSqlParam(long tenantId, String templateCode, Map<String, Object> objectArgs, String lang) {
        return getMessageGenerator().appendSqlParam(tenantId, templateCode, objectArgs, lang);
    }

    public Map<String, String> appendStrSqlParam(long tenantId, String templateCode, Map<String, String> args, String lang) {
        if (args == null) {
            args = new HashMap<>(16);
        }
        Map<String, Object> objectArgs = new HashMap<>(16);
        objectArgs.putAll(args);
        // 获取sql参数
        objectArgs = appendSqlParam(tenantId, templateCode, objectArgs, lang);
        // 插入参数中
        for (Map.Entry<String, Object> entry : objectArgs.entrySet()) {
            args.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return args;
    }

    private MessageGenerator getMessageGenerator() {
        if (messageGenerator instanceof SqlMessageGenerator) {
            return messageGenerator;
        }
        if (messageGenerator instanceof DefaultMessageGenerator && sqlSessionFactory != null) {
            messageGenerator = new SqlMessageGenerator((DefaultMessageGenerator) messageGenerator, sqlSessionFactory);
        }
        return messageGenerator;
    }

    @Override
    public List<Receiver> receiver(long tenantId, String receiverTypeCode, Map<String, String> args) {
        Map<String, Object> objectArgs = new HashMap<>(16);
        if (args != null) {
            objectArgs.putAll(args);
        }
        return receiverWithObjectArgs(tenantId, receiverTypeCode, objectArgs);
    }

    @Override
    public List<Receiver> receiverWithObjectArgs(long tenantId, String receiverTypeCode, Map<String, Object> objectArgs) {
        if (objectArgs == null) {
            objectArgs = new HashMap<>(1);
        }
        return ResponseUtils.getResponse(messageRemoteService.queryReceiver(tenantId, receiverTypeCode, objectArgs), new TypeReference<List<Receiver>>() {
        });
    }

    @Override
    public List<Receiver> openReceiver(long tenantId, String messageType, String receiverTypeCode, Map<String, String> args) {
        String thirdPlatformType;
        switch (messageType) {
            case HmsgBootConstant.MessageType.WC_E:
                thirdPlatformType = HmsgBootConstant.ThirdPlatformType.WX;
                break;
            case HmsgBootConstant.MessageType.DT:
                thirdPlatformType = HmsgBootConstant.ThirdPlatformType.DD;
                break;
            default:
                throw new CommonException("error.receiver.get");
        }
        if (args == null) {
            args = new HashMap<>(1);
        }
        return ResponseUtils.getResponse(messageRemoteService.queryOpenReceiver(tenantId, thirdPlatformType, receiverTypeCode, args), new TypeReference<List<Receiver>>() {
        });
    }

    // ### Web Message ###

    @Override
    public Message sendWebMessage(long tenantId, String messageTemplateCode, String receiverGroupCode, Map<String, String> args) {
        return sendWebMessage(tenantId, messageTemplateCode, messageClientProperties.getDefaultLang(), receiverGroupCode, args);
    }

    @Override
    public Message sendWebMessage(long tenantId, String messageTemplateCode, List<Receiver> receiverList, Map<String, String> args) {
        return sendWebMessage(tenantId, messageTemplateCode, messageClientProperties.getDefaultLang(), receiverList, args);
    }

    @Override
    public Message sendWebMessage(long tenantId, String messageTemplateCode, String lang, String receiverGroupCode, Map<String, String> args) {
        List<Receiver> receiver = receiver(tenantId, receiverGroupCode, args);
        Assert.notEmpty(receiver, ERROR_MESSAGE_EMPTY_RECEIVER);
        return sendWebMessage(tenantId, messageTemplateCode, messageClientProperties.getDefaultLang(), receiver, args);
    }

    @Override
    public Message sendWebMessage(long tenantId, String messageTemplateCode, String lang, List<Receiver> receiverList, Map<String, String> args) {
        Map<String, Object> objectArgs = new HashMap<>(16);
        if (args != null) {
            objectArgs.putAll(args);
        }
        // 获取sql参数
        objectArgs = appendSqlParam(tenantId, messageTemplateCode, objectArgs, lang);
        MessageSender messageSender = new MessageSender()
                .setTenantId(tenantId)
                .setMessageCode(messageTemplateCode)
                .setLang(lang)
                .setReceiverAddressList(receiverList)
                .setObjectArgs(objectArgs);
        if (async) {
            messageAsyncService.sendWebMessage(messageSender);
            return null;
        } else {
            return responseEntityHandler(messageRemoteService.sendWebMessage(tenantId, messageSender));
        }
    }

    // ### Email ###

    @Override
    public Message sendEmail(long tenantId, String serverCode, String messageTemplateCode, String receiverGroupCode, Map<String, String> args, Attachment... attachments) {
        return sendEmail(tenantId, serverCode, messageTemplateCode, messageClientProperties.getDefaultLang(), receiverGroupCode, args, attachments);
    }

    @Override
    public Message sendEmail(long tenantId, String serverCode, String messageTemplateCode, String receiverGroupCode, Map<String, String> args, List<String> ccList, List<String> bccList, Attachment... attachments) {
        return sendEmail(tenantId, serverCode, messageTemplateCode, messageClientProperties.getDefaultLang(), receiverGroupCode, args, ccList, bccList, attachments);
    }

    @Override
    public Message sendEmail(long tenantId, String serverCode, String messageTemplateCode, List<Receiver> receiverList, Map<String, String> args, Attachment... attachments) {
        return sendEmail(tenantId, serverCode, messageTemplateCode, messageClientProperties.getDefaultLang(), receiverList, args, attachments);
    }

    @Override
    public Message sendEmail(long tenantId, String serverCode, String messageTemplateCode, List<Receiver> receiverList, Map<String, String> args, List<String> ccList, List<String> bccList, Attachment... attachments) {
        return sendEmail(tenantId, serverCode, messageTemplateCode, messageClientProperties.getDefaultLang(), receiverList, args, ccList, bccList, attachments);
    }

    @Override
    public Message sendEmail(long tenantId, String serverCode, String messageTemplateCode, String lang, String receiverGroupCode, Map<String, String> args, Attachment... attachments) {
        List<Receiver> receiver = receiver(tenantId, receiverGroupCode, args);
        return sendEmail(tenantId, serverCode, messageTemplateCode, messageClientProperties.getDefaultLang(), receiver, args, attachments);
    }

    @Override
    public Message sendEmail(long tenantId, String serverCode, String messageTemplateCode, String lang, String receiverGroupCode, Map<String, String> args, List<String> ccList, List<String> bccList, Attachment... attachments) {
        List<Receiver> receiver = receiver(tenantId, receiverGroupCode, args);
        return sendEmail(tenantId, serverCode, messageTemplateCode, messageClientProperties.getDefaultLang(), receiver, args, ccList, bccList, attachments);
    }

    @Override
    public Message sendEmail(long tenantId, String serverCode, String messageTemplateCode, String lang, List<Receiver> receiverList, Map<String, String> args, Attachment... attachments) {
        return sendEmail(tenantId, serverCode, messageTemplateCode, lang, receiverList, args, null, null, attachments);
    }

    @Override
    public Message sendEmail(long tenantId, String serverCode, String messageTemplateCode, String lang, List<Receiver> receiverList, Map<String, String> args, List<String> ccList, List<String> bccList, Attachment... attachments) {
        if (attachments == null) {
            attachments = new Attachment[0];
        }
        Assert.notEmpty(receiverList, ERROR_MESSAGE_EMPTY_RECEIVER);
        Map<String, Object> objectArgs = new HashMap<>(16);
        if (args != null) {
            objectArgs.putAll(args);
        }
        // 获取sql参数
        objectArgs = appendSqlParam(tenantId, messageTemplateCode, objectArgs, lang);
        MessageSender messageSender = new MessageSender()
                .setTenantId(tenantId)
                .setMessageCode(messageTemplateCode)
                .setLang(lang)
                .setServerCode(serverCode)
                .setReceiverAddressList(receiverList)
                .setObjectArgs(objectArgs)
                .setCcList(ccList)
                .setBccList(bccList);
        if (attachments.length > 0) {
            messageSender.setAttachmentList(Arrays.asList(attachments));
        }
        if (async) {
            messageAsyncService.sendEmail(messageSender);
            return null;
        } else {
            return responseEntityHandler(messageRemoteService.sendEmail(tenantId, messageSender));
        }
    }

    @Override
    public Message sendCustomEmail(Long tenantId, String serverCode, String subject, String content, List<Receiver> receiverList, List<String> ccList, List<String> bccList, Attachment... attachments) {
        if (attachments == null) {
            attachments = new Attachment[0];
        }
        Assert.notEmpty(receiverList, ERROR_MESSAGE_EMPTY_RECEIVER);
        Assert.notNull(tenantId, BaseConstants.ErrorCode.DATA_INVALID);
        Message message = new Message().setServerCode(serverCode)
                .setMessageTypeCode("EMAIL")
                .setTemplateCode("CUSTOM")
                .setLang(messageClientProperties.getDefaultLang())
                .setTenantId(tenantId)
                .setSubject(subject)
                .setContent(content);
        MessageSender messageSender = new MessageSender()
                .setTenantId(tenantId)
                .setMessageCode("CUSTOM")
                .setServerCode(serverCode)
                .setReceiverAddressList(receiverList)
                .setCcList(ccList)
                .setBccList(bccList)
                .setMessage(message);
        if (attachments.length > 0) {
            messageSender.setAttachmentList(Arrays.asList(attachments));
        }
        if (async) {
            messageAsyncService.sendEmail(messageSender);
            return null;
        } else {
            return responseEntityHandler(messageRemoteService.sendEmail(tenantId, messageSender));
        }
    }

    // ### SMS ###

    @Override
    public Message sendSms(long tenantId, String serverCode, String messageTemplateCode, String receiverGroupCode, Map<String, String> args) {
        return sendSms(tenantId, serverCode, messageTemplateCode, messageClientProperties.getDefaultLang(), receiverGroupCode, args);
    }

    @Override
    public Message sendSms(long tenantId, String serverCode, String messageTemplateCode, List<Receiver> receiverList, Map<String, String> args) {
        return sendSms(tenantId, serverCode, messageTemplateCode, messageClientProperties.getDefaultLang(), receiverList, args);
    }

    @Override
    public Message sendSms(long tenantId, String serverCode, String messageTemplateCode, String lang, String receiverGroupCode, Map<String, String> args) {
        List<Receiver> receiver = receiver(tenantId, receiverGroupCode, args);
        return sendSms(tenantId, serverCode, messageTemplateCode, messageClientProperties.getDefaultLang(), receiver, args);
    }

    @Override
    public Message sendSms(long tenantId, String serverCode, String messageTemplateCode, String lang, List<Receiver> receiverList, Map<String, String> args) {
        Assert.notEmpty(receiverList, ERROR_MESSAGE_EMPTY_RECEIVER);
        Map<String, Object> objectArgs = new HashMap<>(16);
        if (args != null) {
            objectArgs.putAll(args);
        }
        // 获取sql参数
        objectArgs = appendSqlParam(tenantId, messageTemplateCode, objectArgs, lang);
        MessageSender messageSender = new MessageSender()
                .setTenantId(tenantId)
                .setMessageCode(messageTemplateCode)
                .setLang(lang)
                .setServerCode(serverCode)
                .setReceiverAddressList(receiverList)
                .setObjectArgs(objectArgs);
        if (async) {
            messageAsyncService.sendSms(messageSender);
            return null;
        } else {
            return responseEntityHandler(messageRemoteService.sendSms(tenantId, messageSender));
        }
    }

    // ### REL ###

    @Override
    public void sendMessage(long tenantId, String messageCode, List<Receiver> receiverList, Map<String, String> args, Attachment... attachments) {
        sendMessage(tenantId, messageCode, messageClientProperties.getDefaultLang(), receiverList, args, attachments);
    }

    @Override
    public void sendMessage(long tenantId, String messageCode, String receiverGroupCode, Map<String, String> args, Attachment... attachments) {
        sendMessage(tenantId, messageCode, messageClientProperties.getDefaultLang(), receiverGroupCode, args, attachments);
    }

    @Override
    public void sendMessage(long tenantId, String messageCode, List<Receiver> receiverList, Map<String, String> args, List<String> typeCodeList, Attachment... attachments) {
        sendMessage(tenantId, messageCode, messageClientProperties.getDefaultLang(), receiverList, args, typeCodeList, attachments);
    }

    @Override
    public void sendMessage(long tenantId, String messageCode, String receiverGroupCode, Map<String, String> args, List<String> typeCodeList, Attachment... attachments) {
        sendMessage(tenantId, messageCode, messageClientProperties.getDefaultLang(), receiverGroupCode, args, typeCodeList, attachments);
    }

    @Override
    public void sendMessage(long tenantId, String messageCode, String lang, List<Receiver> receiverList, Map<String, String> args, Attachment... attachments) {
        sendMessage(tenantId, messageCode, lang, receiverList, args, null, attachments);
    }

    @Override
    public void sendMessage(long tenantId, String messageCode, String lang, String receiverGroupCode, Map<String, String> args, Attachment... attachments) {
        sendMessage(tenantId, messageCode, lang, receiverGroupCode, args, null, attachments);
    }

    @Override
    public void sendMessage(long tenantId, String messageCode, String lang, String receiverGroupCode, Map<String, String> args, List<String> typeCodeList, Attachment... attachments) {
        List<Receiver> receiver = receiver(tenantId, receiverGroupCode, args);
        sendMessage(tenantId, messageCode, lang, receiver, args, typeCodeList, attachments);
    }

    @Override
    public void sendMessage(long tenantId, String messageCode, String lang, List<Receiver> receiverAddressList, Map<String, String> args, List<String> typeCodeList, Attachment... attachments) {
        if (attachments == null) {
            attachments = new Attachment[0];
        }
        Map<String, Object> objectArgs = new HashMap<>(16);
        if (args != null) {
            objectArgs.putAll(args);
        }
        // 获取sql参数
        List<TemplateServerLine> templateServerLines = ResponseUtils.getResponse(messageRemoteService.listTemplateServerLine(tenantId, messageCode), new TypeReference<List<TemplateServerLine>>() {
        });
        for (TemplateServerLine line : templateServerLines) {
            objectArgs = appendSqlParam(tenantId, line.getTemplateCode(), objectArgs, lang);
        }
        MessageSender messageSender = new MessageSender()
                .setTenantId(tenantId)
                .setMessageCode(messageCode)
                .setLang(lang)
                .setReceiverAddressList(receiverAddressList)
                .setArgs(args)
                .setObjectArgs(objectArgs)
                .setTypeCodeList(typeCodeList);
        if (attachments.length > 0) {
            messageSender.setAttachmentList(Arrays.asList(attachments));
        }
        if (async) {
            messageAsyncService.sendMessage(messageSender);
        } else {
            messageRemoteService.sendMessage(tenantId, messageSender);
        }
    }

    @Override
    public void sendMessage(MessageSender messageSender) {
        MessageSender sender = buildMessageSender(messageSender);
        if (async) {
            messageAsyncService.sendMessage(sender);
        } else {
            messageRemoteService.sendMessage(sender.getTenantId(), sender);
        }
    }

    private MessageSender buildMessageSender(MessageSender messageSender) {
        Assert.notNull(messageSender.getTenantId(), BaseConstants.ErrorCode.DATA_INVALID);
        Assert.isTrue(StringUtils.hasText(messageSender.getMessageCode()), BaseConstants.ErrorCode.DATA_INVALID);
        // 处理sql参数
        Map<String, Object> objectArgs = new HashMap<>(16);
        if (messageSender.getArgs() != null) {
            objectArgs.putAll(messageSender.getArgs());
        }
        if (messageSender.getObjectArgs() != null) {
            objectArgs.putAll(messageSender.getObjectArgs());
        }
        // 获取sql参数
        List<TemplateServerLine> templateServerLines = ResponseUtils.getResponse(messageRemoteService.listTemplateServerLine(messageSender.getTenantId(), messageSender.getMessageCode()), new TypeReference<List<TemplateServerLine>>() {
        });
        for (TemplateServerLine line : templateServerLines) {
            objectArgs = appendSqlParam(messageSender.getTenantId(), line.getTemplateCode(), objectArgs, messageSender.getLang());
        }
        messageSender.setArgs(null).setObjectArgs(objectArgs);
        return messageSender;
    }

    @Override
    public List<Message> sendMessageWithReceipt(MessageSender messageSender) {
        MessageSender sender = buildMessageSender(messageSender);
        if (async) {
            messageAsyncService.sendMessage(sender);
            return Collections.emptyList();
        } else {
            return ResponseUtils.getResponse(messageRemoteService.sendMessage(sender.getTenantId(), sender), new TypeReference<List<Message>>() {
            });
        }
    }

    @Override
    public void sendMessage(Long tenantId, String receiverGroupCode, MessageSender messageSender, WeChatSender weChatSender) {
        sendMessage(tenantId, receiverGroupCode, messageSender, weChatSender, null);
    }

    @Override
    public void sendMessage(Long tenantId, String receiverGroupCode, MessageSender messageSender, WeChatSender weChatSender, DingTalkSender dingTalkSender) {
        Map<String, Object> objectArgs = new HashMap<>(16);
        if (messageSender.getArgs() != null) {
            objectArgs.putAll(messageSender.getArgs());
        }
        if (messageSender.getObjectArgs() != null) {
            objectArgs.putAll(messageSender.getObjectArgs());
        }
        List<Receiver> receiver = receiverWithObjectArgs(tenantId, receiverGroupCode, objectArgs);
        messageSender.setReceiverAddressList(receiver);
        sendMessage(messageSender, weChatSender, dingTalkSender);
    }

    @Override
    public void sendMessage(MessageSender messageSender, WeChatSender weChatSender) {
        sendMessage(messageSender, weChatSender, null);
    }

    @Override
    public void sendMessage(MessageSender messageSender, WeChatSender weChatSender, DingTalkSender dingTalkSender) {
        sendMessageWithReceipt(messageSender, weChatSender, dingTalkSender);
    }

    private Long getTenantId(MessageSender messageSender, WeChatSender weChatSender, DingTalkSender dingTalkSender) {
        if (messageSender != null) {
            return messageSender.getTenantId();
        } else if (weChatSender != null) {
            return weChatSender.getTenantId();
        } else if (dingTalkSender != null) {
            return dingTalkSender.getTenantId();
        } else {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
    }

    private String getMessageCode(MessageSender messageSender, WeChatSender weChatSender, DingTalkSender dingTalkSender) {
        if (messageSender != null) {
            return messageSender.getMessageCode();
        } else if (weChatSender != null) {
            return weChatSender.getMessageCode();
        } else if (dingTalkSender != null) {
            return dingTalkSender.getMessageCode();
        } else {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
    }

    private String getLang(MessageSender messageSender, WeChatSender weChatSender, DingTalkSender dingTalkSender) {
        if (messageSender != null) {
            return messageSender.getLang();
        } else if (weChatSender != null) {
            return weChatSender.getLang();
        } else if (dingTalkSender != null) {
            return dingTalkSender.getLang();
        } else {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
    }

    private AllSender buildAllSender(Long tenantId, MessageSender messageSender, WeChatSender weChatSender, DingTalkSender dingTalkSender) {
        String messageCode = getMessageCode(messageSender, weChatSender, dingTalkSender);
        String lang = getLang(messageSender, weChatSender, dingTalkSender);
        // 聚合参数
        Map<String, Object> objectArgs = new HashMap<>(16);
        if (messageSender != null && messageSender.getArgs() != null) {
            objectArgs.putAll(messageSender.getArgs());
        }
        if (messageSender != null && messageSender.getObjectArgs() != null) {
            objectArgs.putAll(messageSender.getObjectArgs());
        }
        if (weChatSender != null && weChatSender.getArgs() != null) {
            objectArgs.putAll(weChatSender.getArgs());
        }
        if (dingTalkSender != null && dingTalkSender.getArgs() != null) {
            objectArgs.putAll(dingTalkSender.getArgs());
        }
        // 获取sql参数
        List<TemplateServerLine> templateServerLines = ResponseUtils.getResponse(messageRemoteService.listTemplateServerLine(tenantId, messageCode), new TypeReference<List<TemplateServerLine>>() {
        });
        for (TemplateServerLine line : templateServerLines) {
            objectArgs = appendSqlParam(tenantId, line.getTemplateCode(), objectArgs, lang);
        }
        if (messageSender != null) {
            Assert.isTrue(StringUtils.hasText(messageSender.getMessageCode()), BaseConstants.ErrorCode.DATA_INVALID);
            // 参数回填
            messageSender.setArgs(null).setObjectArgs(objectArgs);
        }
        if (weChatSender != null) {
            Assert.isTrue(StringUtils.hasText(weChatSender.getMessageCode()), BaseConstants.ErrorCode.DATA_INVALID);
            // 公众号参数
            Map<String, WeChatFont> data = weChatSender.getData() == null ? new HashMap<>(16) : weChatSender.getData();
            objectArgs.forEach((k, v) -> {
                if (!data.containsKey(k)) {
                    data.put(k, new WeChatFont().setValue(String.valueOf(v)));
                }
            });
            weChatSender.setData(data);
            // 企业号参数
            Map<String, String> map = weChatSender.getArgs() == null ? new HashMap<>(16) : weChatSender.getArgs();
            objectArgs.forEach((k, v) -> map.put(k, String.valueOf(v)));
            weChatSender.setArgs(map);
        }
        if (dingTalkSender != null) {
            Assert.isTrue(StringUtils.hasText(dingTalkSender.getMessageCode()), BaseConstants.ErrorCode.DATA_INVALID);
            Assert.notNull(dingTalkSender.getAgentId(), BaseConstants.ErrorCode.DATA_INVALID);
            // 钉钉参数
            Map<String, String> map = dingTalkSender.getArgs() == null ? new HashMap<>(16) : dingTalkSender.getArgs();
            objectArgs.forEach((k, v) -> map.put(k, String.valueOf(v)));
            dingTalkSender.setArgs(map);
        }
        return new AllSender().setMessageSender(messageSender).setWeChatSender(weChatSender).setDingTalkSender(dingTalkSender);
    }

    @Override
    public List<Message> sendMessageWithReceipt(MessageSender messageSender, WeChatSender weChatSender, DingTalkSender dingTalkSender) {
        Long tenantId = getTenantId(messageSender, weChatSender, dingTalkSender);
        AllSender sender = buildAllSender(tenantId, messageSender, weChatSender, dingTalkSender);
        if (async) {
            messageAsyncService.sendAllMessage(tenantId, sender);
            return Collections.emptyList();
        } else {
            return ResponseUtils.getResponse(messageRemoteService.sendAllMessage(tenantId, sender), new TypeReference<List<Message>>() {
            });
        }
    }

    private <T> T responseEntityHandler(ResponseEntity<T> responseEntity) {
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new CommonException("error.message.send");
        }
        return responseEntity.getBody();
    }

    /**
     * 异步发送
     *
     * @return client
     */
    public MessageClient async() {
        MessageClient messageClient = ApplicationContextHelper.getContext().getBean(MessageClient.class);
        messageClient.setAsync(true);
        return messageClient;
    }

    /**
     * 同步发送
     *
     * @return client
     */
    public MessageClient sync() {
        MessageClient messageClient = ApplicationContextHelper.getContext().getBean(MessageClient.class);
        messageClient.setAsync(false);
        return messageClient;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    // ## websocket相关方法 ##

    /**
     * 获取在线用户信息
     *
     * @return 在线用户信息
     */
    public List<OnLineUserDTO> getUser() {
        return ResponseUtils.getResponse(platformRemoteService.listOnlineUser(BaseConstants.DEFAULT_TENANT_ID), new TypeReference<List<OnLineUserDTO>>() {
        });
    }

    /**
     * 指定sessionId发送webSocket消息
     *
     * @param sessionId sessionId
     * @param key       自定义的key
     * @param message   消息内容
     * @deprecated 该方法仅支持websocket使用redis的广播模式，无法使用stream模式。建议依赖hzero-starter-websocket，调用SocketSendHelper.sendBySession方法
     */
    public void sendBySession(String sessionId, String key, String message) {
        Msg msg = new Msg().setSessionId(sessionId).setKey(key).setMessage(message).setType(WebSocketConstant.SendType.SESSION).setService(serviceName);
        try {
            redisTemplate.convertAndSend(WebSocketConstant.CHANNEL, objectMapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
            throw new CommonException(e);
        }
    }

    /**
     * 指定用户发送webSocket消息
     *
     * @param userId  用户Id
     * @param key     自定义的key
     * @param message 消息内容
     * @deprecated 该方法仅支持websocket使用redis的广播模式，无法使用stream模式。建议依赖hzero-starter-websocket，调用SocketSendHelper.sendByUserId方法
     */
    public void sendByUserId(Long userId, String key, String message) {
        Msg msg = new Msg().setUserId(userId).setKey(key).setMessage(message).setType(WebSocketConstant.SendType.USER).setService(serviceName);
        try {
            redisTemplate.convertAndSend(WebSocketConstant.CHANNEL, objectMapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
            throw new CommonException(e);
        }
    }

    /**
     * 向所有用户发送webSocket消息
     *
     * @param key     自定义的key
     * @param message 消息内容
     * @deprecated 该方法仅支持websocket使用redis的广播模式，无法使用stream模式。建议依赖hzero-starter-websocket，调用SocketSendHelper.sendToAll方法
     */
    public void sendToAll(String key, String message) {
        Msg msg = new Msg().setKey(key).setMessage(message).setType(WebSocketConstant.SendType.ALL).setService(serviceName);
        try {
            redisTemplate.convertAndSend(WebSocketConstant.CHANNEL, objectMapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
            throw new CommonException(e);
        }
    }

    /**
     * 获取公告消息
     *
     * @param tenantId           租户Id
     * @param noticeCategoryCode 公告类型
     * @param lang               语言
     * @param count              查询总数
     * @return 公告消息
     */
    public List<NoticeCacheVO> obtainNoticeMsg(Long tenantId, String noticeCategoryCode, String lang, int count) {
        List<NoticeCacheVO> notices = PublishNoticeRedis.selectLatestPublishedNotice(tenantId, noticeCategoryCode, lang, count);
        if (CollectionUtils.isEmpty(notices)) {
            notices = PublishNoticeRedis.selectLatestPublishedNotice(BaseConstants.DEFAULT_TENANT_ID, noticeCategoryCode, lang, count);
        }
        return notices;
    }

    /**
     * 获取公告信息明细
     *
     * @param noticeId 公告Id
     * @return NoticeDTO
     */
    public NoticeDTO getNoticeDetailsById(Long noticeId) {
        return ResponseUtils.getResponse(messageRemoteService.getNoticeDetailsById(noticeId), NoticeDTO.class);
    }

    @Override
    public Message sendWeChatOfficialMessage(long tenantId, String serverCode, String messageTemplateCode, List<String> userList, Map<String, WeChatFont> data) {
        return sendWeChatOfficialMessage(tenantId, serverCode, messageTemplateCode, messageClientProperties.getDefaultLang(), userList, data, null, null);
    }

    @Override
    public Message sendWeChatOfficialMessage(long tenantId, String serverCode, String messageTemplateCode, String lang, List<String> userList, Map<String, WeChatFont> data, String url, Miniprogram miniprogram) {
        if (data == null) {
            data = new HashMap<>(16);
        }
        Map<String, Object> objectArgs = new HashMap<>(16);
        for (Map.Entry<String, WeChatFont> entry : data.entrySet()) {
            objectArgs.put(entry.getKey(), entry.getValue());
        }
        // 获取sql参数
        objectArgs = appendSqlParam(tenantId, messageTemplateCode, objectArgs, lang);
        // 插入参数中
        for (Map.Entry<String, Object> entry : objectArgs.entrySet()) {
            if (!data.containsKey(entry.getKey())) {
                data.put(entry.getKey(), new WeChatFont().setValue(String.valueOf(entry.getValue())));
            }
        }
        Assert.notEmpty(userList, ERROR_MESSAGE_EMPTY_RECEIVER);
        WeChatSender weChatSender = new WeChatSender()
                .setTenantId(tenantId)
                .setMessageCode(messageTemplateCode)
                .setLang(lang)
                .setServerCode(serverCode)
                .setUserList(userList)
                .setData(data)
                .setUrl(url)
                .setMiniprogram(miniprogram);
        if (async) {
            messageAsyncService.sendWeChatOfficial(weChatSender);
            return null;
        } else {
            return responseEntityHandler(messageRemoteService.sendWeChatOfficial(tenantId, weChatSender));
        }
    }

    @Override
    public Message sendWeChatEnterpriseMessage(long tenantId, String serverCode, String messageTemplateCode, Long agentId, List<String> userList, Map<String, String> args) {
        return sendWeChatEnterpriseMessage(tenantId, serverCode, messageTemplateCode, messageClientProperties.getDefaultLang(), agentId, userList, null, null, 0, args);
    }

    @Override
    public Message sendWeChatEnterpriseMessage(long tenantId, String serverCode, String messageTemplateCode, String lang, Long agentId, List<String> userList, List<String> partyList, List<String> tagList, Integer safe, Map<String, String> args) {
        // 添加sql参数
        args = appendStrSqlParam(tenantId, messageTemplateCode, args, lang);
        WeChatSender weChatSender = new WeChatSender()
                .setTenantId(tenantId)
                .setServerCode(serverCode)
                .setMessageCode(messageTemplateCode)
                .setLang(lang)
                .setAgentId(agentId)
                .setUserIdList(userList)
                .setPartyList(partyList)
                .setTagList(tagList)
                .setSafe(safe)
                .setArgs(args);
        if (async) {
            messageAsyncService.sendWeChatEnterprise(weChatSender);
            return null;
        } else {
            return responseEntityHandler(messageRemoteService.sendWeChatEnterprise(tenantId, weChatSender));
        }
    }

    @Override
    public Message sendWeChatEnterpriseMessage(long tenantId, String serverCode, String messageTemplateCode, String receiverTypeCode, Long agentId, Map<String, String> args) {
        return sendWeChatEnterpriseMessage(tenantId, serverCode, messageTemplateCode, messageClientProperties.getDefaultLang(), receiverTypeCode, agentId, args);
    }

    @Override
    public Message sendWeChatEnterpriseMessage(long tenantId, String serverCode, String messageTemplateCode, String lang, String receiverTypeCode, Long agentId, Map<String, String> args) {
        List<Receiver> openReceivers = openReceiver(tenantId, HmsgBootConstant.MessageType.WC_E, receiverTypeCode, args);
        List<String> userList = null;
        if (!CollectionUtils.isEmpty(openReceivers)) {
            userList = openReceivers.stream().map(Receiver::getOpenUserId).collect(Collectors.toList());
        }
        return sendWeChatEnterpriseMessage(tenantId, serverCode, messageTemplateCode, lang, agentId, userList, null, null, 0, args);
    }

    @Override
    public Message sendDingTalkMessage(Long tenantId, String serverCode, String messageTemplateCode, Map<String, String> args, Long agentId, List<String> userIdList) {
        return sendDingTalkMessage(tenantId, serverCode, messageTemplateCode, null, args, agentId, userIdList, null, false);
    }

    @Override
    public Message sendDingTalkMessage(Long tenantId, String serverCode, String messageTemplateCode, String lang, Map<String, String> args, Long agentId, List<String> userIdList, List<String> deptIdList, boolean toAllUser) {
        // 添加sql参数
        args = appendStrSqlParam(tenantId, messageTemplateCode, args, lang);
        DingTalkSender dingTalkSender = new DingTalkSender()
                .setTenantId(tenantId)
                .setServerCode(serverCode)
                .setMessageCode(messageTemplateCode)
                .setLang(lang)
                .setAgentId(agentId)
                .setUserIdList(userIdList)
                .setDeptIdList(deptIdList)
                .setArgs(args)
                .setToAllUser(toAllUser);
        if (async) {
            messageAsyncService.sendDingTalkMessage(dingTalkSender);
            return null;
        } else {
            return responseEntityHandler(messageRemoteService.sendDingTalk(tenantId, dingTalkSender));
        }
    }

    @Override
    public Message sendDingTalkMessage(long tenantId, String serverCode, String messageTemplateCode, String receiverTypeCode, Long agentId, Map<String, String> args) {
        return sendDingTalkMessage(tenantId, serverCode, messageTemplateCode, messageClientProperties.getDefaultLang(), receiverTypeCode, agentId, args);
    }

    @Override
    public Message sendDingTalkMessage(long tenantId, String serverCode, String messageTemplateCode, String lang, String receiverTypeCode, Long agentId, Map<String, String> args) {
        List<Receiver> openReceivers = openReceiver(tenantId, HmsgBootConstant.MessageType.DT, receiverTypeCode, args);
        List<String> userList = null;
        if (!CollectionUtils.isEmpty(openReceivers)) {
            userList = openReceivers.stream().map(Receiver::getOpenUserId).collect(Collectors.toList());
        }
        return sendDingTalkMessage(tenantId, serverCode, messageTemplateCode, lang, args, agentId, userList, null, false);
    }

    @Override
    public Message sendWebHookMessage(Long tenantId, String messageTemplateCode, String serverCode, List<Receiver> receiverAddressList, Map<String, String> args) {
        return sendWebHookMessage(tenantId, messageTemplateCode, serverCode, receiverAddressList, null, args);
    }

    @Override
    public Message sendWebHookMessage(Long tenantId, String messageTemplateCode, String serverCode, Map<String, String> args) {
        return sendWebHookMessage(tenantId, messageTemplateCode, serverCode, new ArrayList<>(), args);
    }

    @Override
    public Message sendWebHookMessage(Long tenantId, String messageTemplateCode, String serverCode, String receiverTypeCode, String lang, Map<String, String> args) {
        List<Receiver> receiver = receiver(tenantId, receiverTypeCode, args);
        return sendWebHookMessage(tenantId, messageTemplateCode, serverCode, receiver, lang, args);
    }

    @Override
    public Message sendWebHookMessage(Long tenantId, String messageTemplateCode, String serverCode, String receiverTypeCode, Map<String, String> args) {
        List<Receiver> receiver = receiver(tenantId, receiverTypeCode, args);
        return sendWebHookMessage(tenantId, messageTemplateCode, serverCode, receiver, null, args);
    }

    @Override
    public Message sendWebHookMessage(Long tenantId, String messageTemplateCode, String serverCode, List<Receiver> receiverAddressList, String lang, Map<String, String> args) {
        // 添加sql参数
        args = appendStrSqlParam(tenantId, messageTemplateCode, args, lang);
        if (org.apache.commons.lang3.StringUtils.isBlank(lang)) {
            lang = messageClientProperties.getDefaultLang();
        }
        WebHookSender webHookSender = new WebHookSender()
                .setArgs(args)
                .setTenantId(tenantId)
                .setLang(lang)
                .setMessageCode(messageTemplateCode)
                .setReceiverAddressList(receiverAddressList)
                .setServerCode(serverCode);
        if (async) {
            messageAsyncService.sendWebHookMessage(webHookSender);
            return null;
        } else {
            return responseEntityHandler(messageRemoteService.sendWebHookMessage(tenantId, webHookSender));
        }
    }
}
