package org.hzero.message.app.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.MapUtils;
import org.hzero.boot.message.entity.*;
import org.hzero.boot.message.util.VelocityUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.convert.CommonConverter;
import org.hzero.message.app.service.MessageGeneratorService;
import org.hzero.message.app.service.MessageTemplateService;
import org.hzero.message.domain.entity.Message;
import org.hzero.message.domain.entity.MessageTemplate;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import io.choerodon.core.exception.CommonException;

/**
 * <p>
 * 消息生成
 * </p>
 *
 * @author qingsheng.chen 2018/7/31 星期二 10:12
 */
@Service
public class MessageGeneratorServiceImpl implements MessageGeneratorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageGeneratorServiceImpl.class);

    private static final Pattern PATTERN = Pattern.compile(HmsgConstant.TEMPLATE_SECRET_CONTENT_REGEX);

    private final MessageTemplateService messageTemplateService;
    private final ObjectMapper objectMapper;

    @Autowired
    public MessageGeneratorServiceImpl(MessageTemplateService messageTemplateService,
                                       ObjectMapper objectMapper) {
        this.messageTemplateService = messageTemplateService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Message generateMessage(String templateCode, String lang, Map<String, Object> args) {
        return generateMessage(BaseConstants.DEFAULT_TENANT_ID, templateCode, lang, args);
    }

    @Override
    public Message generateMessage(Long tenantId, String templateCode, String lang, Map<String, Object> objectArgs) {
        Message message = generate(tenantId, templateCode, lang, objectArgs);
        if (CollectionUtils.isEmpty(objectArgs)) {
            return message;
        }
        // 消息标题
        message.setSubject(VelocityUtils.parseObject(message.getSubject(), objectArgs));
        // 预处理消息内容，匹配${{}} 参数，需要加密脱敏处理
        List<String> secretArgs = new ArrayList<>();
        String templateContent = message.getContent();
        Matcher matcher = PATTERN.matcher(templateContent);
        while (matcher.find()) {
            String arg = matcher.group();
            String paramName = arg.substring(3, arg.length() - 2);
            secretArgs.add(paramName);
        }
        // 变更模板为velocity可识别的模板
        for (String item : secretArgs) {
            templateContent = templateContent.replace("${{" + item + "}}", "${" + item + "}");
        }
        // 明文消息内容
        String plainContent = VelocityUtils.parseObject(templateContent, objectArgs);
        message.setPlainContent(plainContent);
        if (CollectionUtils.isEmpty(secretArgs)) {
            message.setContent(plainContent);
        } else {
            // 存在参数需要加密存储
            String content = VelocityUtils.parseObject(templateContent, buildSecretArgs(objectArgs, secretArgs));
            message.setContent(content);
            // 敏感字段需要加密存储
            Map<String, Object> storeArgs = new HashMap<>(16);
            for (Map.Entry<String, Object> entry : objectArgs.entrySet()) {
                String key = entry.getKey();
                String value = String.valueOf(entry.getValue());
                if (secretArgs.contains(key)) {
                    storeArgs.put(key, DataSecurityHelper.encrypt(value));
                } else {
                    storeArgs.put(key, value);
                }
            }
            try {
                message.setSendArgs(objectMapper.writeValueAsString(storeArgs));
            } catch (JsonProcessingException e) {
                LOGGER.debug("Parameter encryption failed");
            }
        }
        return message;
    }

    /**
     * 参数脱敏
     *
     * @param objectArgs 消息参数
     * @param secretArgs 敏感字段
     * @return 脱敏参数
     */
    private Map<String, Object> buildSecretArgs(Map<String, Object> objectArgs, List<String> secretArgs) {
        Map<String, Object> map = new HashMap<>(16);
        for (Map.Entry<String, Object> entry : objectArgs.entrySet()) {
            String key = entry.getKey();
            String value = String.valueOf(entry.getValue());
            if (secretArgs.contains(key)) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < value.length(); i++) {
                    sb.append(BaseConstants.Symbol.STAR);
                }
                map.put(key, sb.toString());
            } else {
                map.put(key, value);
            }
        }
        return map;
    }

    @Override
    public Message generateMessage(Long tenantId, String templateCode, String lang, Map<String, String> templateArgs, Map<String, String> map) {
        // args 是模板渲染参数，map是发送钉钉、企业微信需要的参数
        Map<String, Object> objectArgs = new HashMap<>(16);
        objectArgs.putAll(templateArgs);
        Message message = generate(tenantId, templateCode, lang, objectArgs);
        if (CollectionUtils.isEmpty(objectArgs)) {
            return message;
        }
        // 消息标题
        message.setSubject(VelocityUtils.parseObject(message.getSubject(), objectArgs));
        // 预处理消息内容，匹配${{}} 参数，需要加密脱敏处理
        List<String> secretArgs = new ArrayList<>();
        String templateContent = message.getContent();
        Matcher matcher = PATTERN.matcher(templateContent);
        while (matcher.find()) {
            String arg = matcher.group();
            String paramName = arg.substring(3, arg.length() - 2);
            secretArgs.add(paramName);
        }
        // 变更模板为velocity可识别的模板
        for (String item : secretArgs) {
            templateContent = templateContent.replace("${{" + item + "}}", "${" + item + "}");
        }
        // 明文消息内容
        String plainContent = VelocityUtils.parseObject(templateContent, objectArgs);
        message.setPlainContent(plainContent);
        if (CollectionUtils.isEmpty(secretArgs)) {
            message.setContent(plainContent);
        } else {
            // 存在参数需要加密存储
            String content = VelocityUtils.parseObject(templateContent, buildSecretArgs(objectArgs, secretArgs));
            message.setContent(content);
        }
        try {
            // 钉钉和微信存储map，而不是args
            message.setSendArgs(objectMapper.writeValueAsString(map));
        } catch (JsonProcessingException e) {
            LOGGER.debug("Parameter encryption failed");
        }
        return message;
    }

    private Message generate(Long tenantId, String templateCode, String lang, Map<String, Object> objectArgs) {
        MessageTemplate messageTemplate = messageTemplateService.getMessageTemplate(tenantId, templateCode, lang);
        Assert.notNull(messageTemplate, String.format("Message template not found or not enabled : tenantId = [%d] , templateCode = [%s], lang = [%s]", tenantId, templateCode, lang));
        Message message;
        try {
            message = new Message()
                    .setSubject(messageTemplate.getTemplateTitle())
                    .setContent(messageTemplate.getTemplateContent())
                    .setTemplateCode(templateCode)
                    .setTenantId(tenantId)
                    .setLang(lang)
                    .setExternalCode(messageTemplate.getExternalCode())
                    .setSendArgs(objectMapper.writeValueAsString(objectArgs))
                    .setTemplateEditType(messageTemplate.getEditorType());
        } catch (JsonProcessingException e) {
            throw new CommonException(e);
        }
        return message;
    }

    @Override
    public Message generateMessage(MessageSender messageSender, Message message) {
        Message messageContent;
        // String类型的args转成Object类型的objectArgs
        Map<String, Object> objectArgs = messageSender.getObjectArgs() == null ? new HashMap<>(16) : messageSender.getObjectArgs();
        if (!CollectionUtils.isEmpty(messageSender.getArgs())) {
            objectArgs.putAll(messageSender.getArgs());
            messageSender.setObjectArgs(objectArgs);
        }
        if (messageSender.getMessage() != null) {
            messageContent = CommonConverter.beanConvert(Message.class, messageSender.getMessage());
        } else {
            messageContent = generateMessage(messageSender.getTenantId(), messageSender.getMessageCode(), messageSender.getLang(), objectArgs);
        }
        return message.setSubject(messageContent.getSubject())
                .setContent(messageContent.getContent())
                .setPlainContent(messageContent.getPlainContent())
                .setSendArgs(messageContent.getSendArgs())
                .setExternalCode(messageContent.getExternalCode())
                .setTemplateEditType(messageContent.getTemplateEditType());
    }

    @Override
    public Message generateMessage(WeChatSender weChatSender, Message message) {
        // 参数，公众号消息会把其他参数存在这里
        Map<String, String> params = new HashMap<>(16);
        // 消息模板使用的参数名和值
        Map<String, String> templateArgs = new HashMap<>(16);
        switch (message.getMessageTypeCode()) {
            case HmsgConstant.MessageType.WC_O:
                // 处理参数
                try {
                    params.put(WeChatSender.FIELD_DATA, objectMapper.writeValueAsString(weChatSender.getData()));
                    if (StringUtils.hasText(weChatSender.getUrl())) {
                        params.put(WeChatSender.FIELD_URL, weChatSender.getUrl());
                    }
                    if (weChatSender.getMiniprogram() != null) {
                        params.put(WeChatSender.FIELD_MINIPROGRAM, objectMapper.writeValueAsString(weChatSender.getMiniprogram()));
                    }
                } catch (Exception e) {
                    throw new CommonException(e);
                }
                // 获取不带色彩的参数集合
                for (Map.Entry<String, WeChatFont> entry : weChatSender.getData().entrySet()) {
                    templateArgs.put(entry.getKey(), entry.getValue().getValue());
                }
                break;
            case HmsgConstant.MessageType.WC_E:
                // 处理参数
                try {
                    params.put(WeChatSender.FIELD_AGENT_ID, String.valueOf(weChatSender.getAgentId()));
                    if (!CollectionUtils.isEmpty(weChatSender.getArgs())) {
                        params.put(WeChatSender.FIELD_ARGS, objectMapper.writeValueAsString(weChatSender.getArgs()));
                    }
                    if (!CollectionUtils.isEmpty(weChatSender.getPartyList())) {
                        params.put(WeChatSender.FIELD_PARTY_LIST, objectMapper.writeValueAsString(weChatSender.getPartyList()));
                    }
                    if (!CollectionUtils.isEmpty(weChatSender.getTagList())) {
                        params.put(WeChatSender.FIELD_TAG_LIST, objectMapper.writeValueAsString(weChatSender.getTagList()));
                    }
                    if (weChatSender.getSafe() != null) {
                        params.put(WeChatSender.FIELD_SAFE, String.valueOf(weChatSender.getSafe()));
                    }
                    if (weChatSender.getMsgType() != null) {
                        params.put(WeChatSender.FIELD_MSG_TYPE, weChatSender.getMsgType().getValue());
                    }
                } catch (Exception e) {
                    throw new CommonException(e);
                }
                templateArgs = weChatSender.getArgs();
                break;
            default:
                throw new CommonException(HmsgConstant.ErrorCode.RECEIVE_TYPE_NULL);
        }
        Message messageContent;
        if (weChatSender.getMessage() != null) {
            messageContent = CommonConverter.beanConvert(Message.class, weChatSender.getMessage());
        } else {
            messageContent = generateMessage(weChatSender.getTenantId(), weChatSender.getMessageCode(), weChatSender.getLang(), templateArgs, params);
        }
        return message.setSubject(messageContent.getSubject())
                .setContent(messageContent.getContent())
                .setPlainContent(messageContent.getPlainContent())
                .setSendArgs(messageContent.getSendArgs())
                .setExternalCode(messageContent.getExternalCode())
                .setTemplateEditType(messageContent.getTemplateEditType());
    }

    @Override
    public Message generateMessage(DingTalkSender dingTalkSender, Message message) {
        //钉钉发送消息参数
        Map<String, String> params = new HashMap<>(16);
        try {
            params.put(DingTalkSender.FIELD_AGENT_ID, String.valueOf(dingTalkSender.getAgentId()));
            if (!CollectionUtils.isEmpty(dingTalkSender.getUserIdList())) {
                params.put(DingTalkSender.FIELD_USER_ID_LIST, objectMapper.writeValueAsString(dingTalkSender.getUserIdList()));
            }
            if (!CollectionUtils.isEmpty(dingTalkSender.getDeptIdList())) {
                params.put(DingTalkSender.FIELD_DEPT_ID_LIST, objectMapper.writeValueAsString(dingTalkSender.getDeptIdList()));
            }
            if (dingTalkSender.getToAllUser() != null) {
                params.put(DingTalkSender.FIELD_TO_ALL_USER, objectMapper.writeValueAsString(dingTalkSender.getToAllUser()));
            }
            if (dingTalkSender.getMsgType() != null) {
                params.put(DingTalkSender.FIELD_MSG_TYPE, dingTalkSender.getMsgType().getValue());
            }
        } catch (Exception e) {
            throw new CommonException(e);
        }

        Message messageContent;
        if (dingTalkSender.getMessage() != null) {
            messageContent = CommonConverter.beanConvert(Message.class, dingTalkSender.getMessage());
        } else {
            messageContent = generateMessage(dingTalkSender.getTenantId(), dingTalkSender.getMessageCode(), dingTalkSender.getLang(), dingTalkSender.getArgs(), params);
        }
        return message.setSubject(messageContent.getSubject())
                .setContent(messageContent.getContent())
                .setPlainContent(messageContent.getPlainContent())
                .setSendArgs(messageContent.getSendArgs())
                .setExternalCode(messageContent.getExternalCode())
                .setTemplateEditType(messageContent.getTemplateEditType());
    }

    @Override
    public Message generateMessage(WebHookSender webHookSender, Message message) {
        Message messageContent;
        Map<String, Object> objArgs = new HashMap<>(16);
        if (webHookSender.getMessage() != null) {
            messageContent = CommonConverter.beanConvert(Message.class, webHookSender.getMessage());
        } else {
            Map<String, String> args = webHookSender.getArgs();
            if (MapUtils.isNotEmpty(args)) {
                objArgs.putAll(args);
            }
            messageContent = generateMessage(webHookSender.getTenantId(), webHookSender.getMessageCode(), webHookSender.getLang(), objArgs);
        }
        return message.setSubject(messageContent.getSubject())
                .setContent(messageContent.getContent())
                .setPlainContent(messageContent.getPlainContent())
                .setSendArgs(messageContent.getSendArgs())
                .setTemplateEditType(messageContent.getTemplateEditType());
    }
}
