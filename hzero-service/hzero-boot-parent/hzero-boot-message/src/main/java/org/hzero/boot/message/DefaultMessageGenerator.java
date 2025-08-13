package org.hzero.boot.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hzero.boot.message.config.MessageClientProperties;
import org.hzero.boot.message.entity.Message;
import org.hzero.boot.message.entity.MessageTemplate;
import org.hzero.boot.message.feign.MessageRemoteService;
import org.hzero.boot.message.service.MessageGenerator;
import org.hzero.boot.message.util.VelocityUtils;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.helper.LanguageHelper;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.ResponseUtils;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author qingsheng.chen@hand-china.com 2019-06-20 15:04
 */
public class DefaultMessageGenerator implements MessageGenerator {

    private static final String MESSAGE_TEMPLATE_PREFIX = HZeroService.Message.CODE + ":message:template:";
    private static final Pattern PATTERN = Pattern.compile("\\$\\{\\{.*?}}");

    private final MessageClientProperties messageClientProperties;
    private final MessageRemoteService messageRemoteService;
    private final RedisHelper redisHelper;

    public DefaultMessageGenerator(DefaultMessageGenerator that) {
        this.messageClientProperties = that.messageClientProperties;
        this.messageRemoteService = that.messageRemoteService;
        this.redisHelper = that.redisHelper;
    }

    public DefaultMessageGenerator(MessageClientProperties messageClientProperties,
                                   MessageRemoteService messageRemoteService,
                                   RedisHelper redisHelper) {
        this.messageClientProperties = messageClientProperties;
        this.messageRemoteService = messageRemoteService;
        this.redisHelper = redisHelper;
    }

    @Override
    public Message generateMessage(long tenantId, String templateCode, String serverTypeCode, Map<String, String> args, boolean sqlEnable, String lang) {
        // 获取消息模板
        lang = getLang(lang);
        MessageTemplate messageTemplate = getMessageTemplate(tenantId, templateCode, lang);
        if (messageTemplate == null || BaseConstants.Flag.NO.equals(messageTemplate.getEnabledFlag())) {
            return null;
        }
        // 拼接消息内容
        Map<String, Object> objectArgs = new HashMap<>(16);
        objectArgs.putAll(args);
        return generateMessage(generateMessage(messageTemplate, templateCode, lang, tenantId), objectArgs);
    }

    @Override
    public Message generateMessageObjectArgs(long tenantId, String templateCode, String serverTypeCode, Map<String, Object> objectArgs, boolean sqlEnable, String lang) {
        // 获取消息模板
        lang = getLang(lang);
        MessageTemplate messageTemplate = getMessageTemplate(tenantId, templateCode, lang);
        if (messageTemplate == null || BaseConstants.Flag.NO.equals(messageTemplate.getEnabledFlag())) {
            return null;
        }
        // 拼接消息内容
        return generateMessage(generateMessage(messageTemplate, templateCode, lang, tenantId), objectArgs);
    }

    Message generateMessage(Message message, Map<String, Object> objectArgs) {
        // 从消息内容中替换获取的参数
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
            message.setSendArgs(redisHelper.toJson(storeArgs));
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

    Message generateMessage(MessageTemplate messageTemplate, String templateCode, String lang, long tenantId) {
        return new Message().setMessageTypeCode(messageTemplate.getTemplateTypeCode())
                .setSubject(messageTemplate.getTemplateTitle())
                .setContent(messageTemplate.getTemplateContent())
                .setTemplateCode(templateCode)
                .setLang(lang)
                .setTenantId(tenantId)
                .setExternalCode(messageTemplate.getExternalCode())
                .setServerTypeCode(messageTemplate.getServerTypeCode());
    }

    MessageTemplate getMessageTemplate(long tenantId, String templateCode, String lang) {
        // 如果启用动态Redis，切换设置的Redis
        redisHelper.setCurrentDatabase(HZeroService.Message.REDIS_DB);
        // 先尝试取缓存
        MessageTemplate messageTemplate = redisHelper.strGet(getRedisKey(tenantId, templateCode, lang), MessageTemplate.class);
        // 如果启用动态Redis，清除设置的Redis
        redisHelper.clearCurrentDatabase();
        if (messageTemplate != null) {
            return messageTemplate;
        }
        return ResponseUtils.getResponse(messageRemoteService.queryMessageTemplate(tenantId, templateCode, lang), MessageTemplate.class);
    }

    String getLang(String lang) {
        if (!StringUtils.hasText(lang)) {
            return messageClientProperties.getDefaultLang();
        }
        if (StringUtils.isEmpty(lang)) {
            lang = LanguageHelper.getDefaultLanguage();
        }
        return lang;
    }

    public static String getRedisKey(long tenantId, String templateCode, String lang) {
        return MESSAGE_TEMPLATE_PREFIX + tenantId +
                "." + templateCode +
                "." + lang;
    }

    @Override
    public Map<String, Object> appendSqlParam(long tenantId, String templateCode, Map<String, Object> objectArgs, String lang) {
        // 没有sql，直接返回原参数
        return objectArgs;
    }
}
