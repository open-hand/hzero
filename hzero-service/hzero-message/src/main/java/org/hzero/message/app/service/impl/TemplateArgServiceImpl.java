package org.hzero.message.app.service.impl;

import org.apache.commons.lang.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.message.app.service.TemplateArgService;
import org.hzero.message.domain.entity.MessageTemplate;
import org.hzero.message.domain.entity.TemplateArg;
import org.hzero.message.domain.repository.MessageTemplateRepository;
import org.hzero.message.domain.repository.TemplateArgRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 消息模板参数应用服务默认实现
 *
 * @author fanghan.liu@hand-china.com 2019-10-08 16:49:41
 */
@Service
public class TemplateArgServiceImpl implements TemplateArgService {

    private final TemplateArgRepository templateArgRepository;
    private final MessageTemplateRepository messageTemplateRepository;

    private static final Pattern PATTERN = Pattern.compile(HmsgConstant.TEMPLATE_CONTENT_REGEX);

    @Autowired
    public TemplateArgServiceImpl(TemplateArgRepository templateArgRepository, MessageTemplateRepository messageTemplateRepository) {
        this.templateArgRepository = templateArgRepository;
        this.messageTemplateRepository = messageTemplateRepository;
    }

    @Override
    public Page<TemplateArg> pageTemplateArgs(Long templateId, String argName, PageRequest pageRequest) {
        TemplateArg templateArg = new TemplateArg();
        templateArg.setTemplateId(templateId);
        if (!StringUtils.isEmpty(argName)) {
            templateArg.setArgName(argName);
        }
        return PageHelper.doPageAndSort(pageRequest, () -> templateArgRepository.selectByTemplateId(templateArg));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initTemplateArgs(Long templateId, Long tenantId) {
        // 获取消息模板
        MessageTemplate messageTemplate = messageTemplateRepository.selectOne(new MessageTemplate().setTenantId(tenantId).setTemplateId(templateId));
        Assert.notNull(messageTemplate, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        List<String> contentArgs = new LinkedList<>();
        // 解析模板标题，将参数名放入list
        Matcher titleMatcher = PATTERN.matcher(messageTemplate.getTemplateTitle());
        while (titleMatcher.find()) {
            String arg = titleMatcher.group();
            String argName = arg.substring(2, arg.length() - 1);
            if (argName.startsWith("!")) {
                argName = argName.substring(1);
            }
            contentArgs.add(argName);
        }
        // 解析模板正文，将参数名放入list
        Matcher matcher = PATTERN.matcher(messageTemplate.getTemplateContent());
        while (matcher.find()) {
            String arg = matcher.group();
            String argName = arg.substring(2, arg.length() - 1);
            if (argName.startsWith("!")) {
                argName = argName.substring(1);
            }
            contentArgs.add(argName);
        }
        // 获取模板参数表中所有列，将参数名放入list
        TemplateArg templateArg = new TemplateArg();
        templateArg.setTemplateId(templateId);
        templateArg.setTenantId(messageTemplate.getTenantId());
        List<TemplateArg> templateArgs = templateArgRepository.selectByCondition(Condition.builder(TemplateArg.class)
                .select(TemplateArg.FIELD_ARG_NAME, TemplateArg.FIELD_ARG_ID)
                .andWhere(Sqls.custom()
                        .andEqualTo(TemplateArg.FIELD_TEMPLATE_ID, templateId))
                .build());
        List<String> args = templateArgs.stream().map(TemplateArg::getArgName).collect(Collectors.toList());
        // 取正文参数和模板参数表中参数的差集
        List<String> addCollect = contentArgs.stream().filter(content -> !args.contains(content)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(addCollect)) {
            addCollect.forEach(arg -> {
                Assert.isTrue(arg.length() <= 30, HmsgConstant.ErrorCode.TEMPLATE_ARG_TOO_LONG);
                templateArg.setArgName(arg);
                templateArg.setArgId(null);
                templateArgRepository.insertSelective(templateArg);
            });
        }
        // 取模板参数表中参数和正文参数的差集
        List<String> deleteArgNameList = args.stream().filter(arg -> !contentArgs.contains(arg)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(deleteArgNameList)) {
            List<TemplateArg> deleteList = templateArgs.stream().filter(temp -> deleteArgNameList.contains(temp.getArgName())).collect(Collectors.toList());
            templateArgRepository.batchDelete(deleteList);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TemplateArg updateTemplateArgs(TemplateArg templateArg) {
        templateArgRepository.updateOptional(templateArg, TemplateArg.FIELD_DESCRIPTION);
        return templateArg;
    }
}
