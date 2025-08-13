package org.hzero.message.app.service.impl;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import org.hzero.boot.message.DefaultMessageGenerator;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.helper.LanguageHelper;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.EscapeUtils;
import org.hzero.core.util.ResponseUtils;
import org.hzero.message.api.dto.LanguageDTO;
import org.hzero.message.api.dto.SimpleLangDTO;
import org.hzero.message.app.service.MessageTemplateService;
import org.hzero.message.app.service.TemplateServerService;
import org.hzero.message.domain.entity.MessageTemplate;
import org.hzero.message.domain.entity.TemplateServerLine;
import org.hzero.message.domain.repository.MessageTemplateRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.message.infra.feign.LangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 消息模板应用服务默认实现
 *
 * @author qingsheng.chen@hand-china 2018-07-25 16:11:11
 */
@Service
public class MessageTemplateServiceImpl implements MessageTemplateService {

    private final RedisHelper redisHelper;
    private final LangService langService;
    private final TemplateServerService templateServerService;
    private final MessageTemplateRepository messageTemplateRepository;

    private static final Pattern PATTERN = Pattern.compile(HmsgConstant.TEMPLATE_CONTENT_REGEX);
    private static final Pattern SECRET_PATTERN = Pattern.compile(HmsgConstant.TEMPLATE_SECRET_CONTENT_REGEX);

    @Autowired
    public MessageTemplateServiceImpl(RedisHelper redisHelper,
                                      LangService langService,
                                      TemplateServerService templateServerService,
                                      MessageTemplateRepository messageTemplateRepository) {
        this.redisHelper = redisHelper;
        this.langService = langService;
        this.templateServerService = templateServerService;
        this.messageTemplateRepository = messageTemplateRepository;
    }

    @Override
    public Page<MessageTemplate> listMessageTemplate(Long tenantId, String templateCode, String templateName, Integer enabledFlag, String lang, boolean includeSiteIfQueryByTenantId, PageRequest pageRequest) {
        return messageTemplateRepository.selectMessageTemplate(tenantId, templateCode, templateName, enabledFlag, lang, includeSiteIfQueryByTenantId, pageRequest);
    }

    @Override
    public MessageTemplate getMessageTemplate(Long tenantId, long templateId) {
        return messageTemplateRepository.selectMessageTemplateByPrimaryKey(tenantId, templateId);
    }

    @Override
    public MessageTemplate copyMessageTemplate(Long tenantId, Long templateId) {
        Assert.isTrue(!Objects.equals(tenantId, BaseConstants.DEFAULT_TENANT_ID), BaseConstants.ErrorCode.DATA_INVALID);
        MessageTemplate messageTemplate = messageTemplateRepository.selectByPrimaryKey(templateId);
        if (messageTemplate == null || !Objects.equals(messageTemplate.getTenantId(), BaseConstants.DEFAULT_TENANT_ID)) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        return messageTemplate.setTemplateId(null);
    }

    @Override
    public MessageTemplate getMessageTemplate(long tenantId, String templateCode, String lang) {
        MessageTemplate messageTemplate = getMessageTemplateWithCache(tenantId, templateCode, lang);
        if (messageTemplate == null) {
            // 查找中文模板
            messageTemplate = getMessageTemplateWithCache(tenantId, templateCode, LanguageHelper.getDefaultLanguage());
        }
        // 如果租户级找不到，查找平台级
        if (messageTemplate == null && tenantId != BaseConstants.DEFAULT_TENANT_ID) {
            messageTemplate = getMessageTemplateWithCache(BaseConstants.DEFAULT_TENANT_ID, templateCode, lang);
            if (messageTemplate == null) {
                // 查找中文模板
                messageTemplate = getMessageTemplateWithCache(BaseConstants.DEFAULT_TENANT_ID, templateCode, LanguageHelper.getDefaultLanguage());
            }
        }
        return messageTemplate;
    }

    private MessageTemplate getMessageTemplateWithCache(long tenantId, String templateCode, String lang) {
        MessageTemplate messageTemplate = redisHelper.fromJson(redisHelper.strGet(DefaultMessageGenerator.getRedisKey(tenantId, templateCode, lang)), MessageTemplate.class);
        if (messageTemplate != null && Objects.equals(messageTemplate.getEnabledFlag(), BaseConstants.Flag.NO)) {
            // 消息模板被禁用
            return null;
        }
        if (messageTemplate == null) {
            // 查询数据库
            MessageTemplate selectCondition = new MessageTemplate().setTenantId(tenantId).setTemplateCode(templateCode).setLang(lang).setEnabledFlag(BaseConstants.Flag.YES);
            messageTemplate = messageTemplateRepository.selectOne(selectCondition);
            if (messageTemplate != null) {
                redisHelper.strSet(DefaultMessageGenerator.getRedisKey(messageTemplate.getTenantId(), templateCode, lang), redisHelper.toJson(messageTemplate));
            }
        }
        return messageTemplate;
    }

    @Override
    public List<LanguageDTO> listMessageTemplateLang(long tenantId, String messageCode) {
        List<TemplateServerLine> templateServerLineList = templateServerService.listTemplateServerLine(tenantId, messageCode);
        List<LanguageDTO> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(templateServerLineList)) {
            Set<String> langSet = messageTemplateRepository.listMessageTemplateLang(tenantId, templateServerLineList.stream()
                    .map(TemplateServerLine::getTemplateCode).collect(Collectors.toSet()));
            // feign调用翻译语言
            List<SimpleLangDTO> list = ResponseUtils.getResponse(langService.listLanguage(), new TypeReference<List<SimpleLangDTO>>() {
            });
            Map<String, String> langMap;
            if (CollectionUtils.isEmpty(list)) {
                langMap = new HashMap<>(1);
            } else {
                langMap = list.stream().collect(Collectors.toMap(SimpleLangDTO::getCode, SimpleLangDTO::getName, (k1, k2) -> k1));
            }
            langSet.forEach(lang -> result.add(new LanguageDTO().setLang(lang).setLangMeaning(langMap.getOrDefault(lang, lang))));
        }
        return result;
    }

    @Override
    public Set<String> listMessageTemplateArgs(long tenantId, String messageCode, String lang) {
        List<TemplateServerLine> templateServerLineList = templateServerService.listTemplateServerLine(tenantId, messageCode);
        Set<String> result = new LinkedHashSet<>();
        if (!CollectionUtils.isEmpty(templateServerLineList)) {
            templateServerLineList.forEach(line ->
                    // 处理消息内容，获取参数名
                    result.addAll(getTemplateArg(tenantId, line.getTemplateCode(), lang)));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageTemplate createMessageTemplate(MessageTemplate messageTemplate) {
        // 防范XSS攻击
        messageTemplate.setTemplateContent(EscapeUtils.preventScript(messageTemplate.getTemplateContent()));
        messageTemplate.validateTemplateCodeRepeat(messageTemplateRepository);
        messageTemplateRepository.insert(messageTemplate);
        // 更新缓存
        redisHelper.strSet(DefaultMessageGenerator.getRedisKey(messageTemplate.getTenantId(), messageTemplate.getTemplateCode(), messageTemplate.getLang()), redisHelper.toJson(messageTemplate));
        return messageTemplate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageTemplate updateMessageTemplate(MessageTemplate messageTemplate) {
        // 防范XSS攻击
        messageTemplate.setTemplateContent(EscapeUtils.preventScript(messageTemplate.getTemplateContent()));
        MessageTemplate delete = messageTemplate.validateMessageTemplateExists(messageTemplateRepository);
        // 删除原来的缓存
        redisHelper.delKey(DefaultMessageGenerator.getRedisKey(delete.getTenantId(), delete.getTemplateCode(), delete.getLang()));
        messageTemplateRepository.updateOptional(messageTemplate,
                MessageTemplate.FIELD_TEMPLATE_NAME, MessageTemplate.FIELD_TEMPLATE_TITLE, MessageTemplate.FIELD_TEMPLATE_CONTENT,
                MessageTemplate.FIELD_TEMPLATE_TYPE_CODE, MessageTemplate.FIELD_ENABLED_FLAG, MessageTemplate.FIELD_SQL_VALUE,
                MessageTemplate.FIELD_LANG, MessageTemplate.FIELD_EXTERNAL_CODE, MessageTemplate.FIELD_SERVER_TYPE_CODE,
                MessageTemplate.FIELD_MESSAGE_CATEGORY_CODE, MessageTemplate.FIELD_MESSAGE_SUBCATEGORY_CODE,
                MessageTemplate.FIELD_EDITOR_TYPE);
        // 更新缓存
        redisHelper.strSet(DefaultMessageGenerator.getRedisKey(messageTemplate.getTenantId(), messageTemplate.getTemplateCode(), messageTemplate.getLang()), redisHelper.toJson(messageTemplate));
        return messageTemplate;
    }

    @Override
    public List<String> getTemplateArg(long tenantId, String templateCode, String lang) {
        MessageTemplate messageTemplate = new MessageTemplate().setTenantId(tenantId).setLang(lang).setTemplateCode(templateCode);
        MessageTemplate template = messageTemplateRepository.selectOne(messageTemplate);
        if (template == null) {
            messageTemplate.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
            template = messageTemplateRepository.selectOne(messageTemplate);
        }
        if (template == null) {
            return new ArrayList<>();
        }
        List<String> argList = new LinkedList<>();
        // 解析标题
        findSecretArgs(template.getTemplateTitle(), argList);
        findArgs(template.getTemplateTitle(), argList);
        // 解析内容
        findSecretArgs(template.getTemplateContent(), argList);
        findArgs(template.getTemplateContent(), argList);
        return argList;
    }

    private void findArgs(String content, List<String> argList) {
        Matcher matcher = PATTERN.matcher(content);
        while (matcher.find()) {
            String arg = matcher.group();
            if (arg.startsWith("${{")) {
                continue;
            }
            String argName = arg.substring(2, arg.length() - 1);
            if (argName.startsWith("!")) {
                argName = argName.substring(1);
            }
            argList.add(argName);
        }
    }

    private void findSecretArgs(String content, List<String> argList) {
        Matcher matcher = SECRET_PATTERN.matcher(content);
        while (matcher.find()) {
            String arg = matcher.group();
            String argName = arg.substring(3, arg.length() - 2);
            if (argName.startsWith("!")) {
                argName = argName.substring(1);
            }
            argList.add(argName);
        }
    }
}
