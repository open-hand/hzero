package org.hzero.message.infra.repository.impl;

import org.apache.commons.collections.CollectionUtils;
import org.hzero.message.domain.entity.MessageTemplate;
import org.hzero.message.domain.repository.MessageTemplateRepository;
import org.hzero.message.infra.mapper.MessageTemplateMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 消息模板 资源库实现
 *
 * @author qingsheng.chen@hand-china 2018-07-25 16:11:11
 */
@Component
public class MessageTemplateRepositoryImpl extends BaseRepositoryImpl<MessageTemplate> implements MessageTemplateRepository {

    private MessageTemplateMapper messageTemplateMapper;

    @Autowired
    public MessageTemplateRepositoryImpl(MessageTemplateMapper messageTemplateMapper) {
        this.messageTemplateMapper = messageTemplateMapper;
    }

    @Override
    public Page<MessageTemplate> selectMessageTemplate(Long tenantId, String templateCode, String templateName, Integer enabledFlag, String lang, boolean includeSiteIfQueryByTenantId, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> messageTemplateMapper.selectMessageTemplate(tenantId, templateCode, templateName, enabledFlag, lang, includeSiteIfQueryByTenantId));
    }

    @Override
    public MessageTemplate selectMessageTemplateByPrimaryKey(Long tenantId, long templateId) {
        return messageTemplateMapper.selectMessageTemplateByPrimaryKey(tenantId, templateId);
    }

    @Override
    public Set<String> listMessageTemplateLang(long tenantId, Set<String> templateCodeList) {
        return messageTemplateMapper.listMessageTemplateLang(tenantId, templateCodeList);
    }

    @Override
    public MessageTemplate selectByCode(Long tenantId, String templateCode) {
        List<MessageTemplate> messageTemplates = messageTemplateMapper.selectByCode(tenantId, templateCode);
        if (CollectionUtils.isNotEmpty(messageTemplates)) {
            return messageTemplates.get(0);
        }
        return null;
    }
}
