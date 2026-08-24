package org.hzero.message.infra.repository.impl;

import java.util.List;

import org.hzero.message.domain.entity.TemplateServerLine;
import org.hzero.message.domain.repository.TemplateServerLineRepository;
import org.hzero.message.infra.mapper.TemplateServerLineMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 服务模板关联行实现
 * </p>
 *
 * @author qingsheng.chen 2018/9/30 星期日 13:28
 */
@Component
public class TemplateServerLineRepositoryImpl extends BaseRepositoryImpl<TemplateServerLine> implements TemplateServerLineRepository {

    private final TemplateServerLineMapper templateServerLineMapper;

    @Autowired
    public TemplateServerLineRepositoryImpl(TemplateServerLineMapper templateServerLineMapper) {
        this.templateServerLineMapper = templateServerLineMapper;
    }

    @Override
    public List<TemplateServerLine> listTemplateServerLine(long tempServerId, long tenantId) {
        return templateServerLineMapper.selectTemplateServerLine(tempServerId, tenantId);
    }

    @Override
    public List<TemplateServerLine> enabledTemplateServerLine(long tempServerId, long tenantId) {
        return templateServerLineMapper.enabledTemplateServerLine(tempServerId, tenantId);
    }

    @Override
    public TemplateServerLine getTemplateServerLine(long tenantId, String messageCode, String typeCode) {
        return templateServerLineMapper.getTemplateServerLine(tenantId, messageCode, typeCode);
    }
}
