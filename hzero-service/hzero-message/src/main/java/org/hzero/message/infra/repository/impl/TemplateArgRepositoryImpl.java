package org.hzero.message.infra.repository.impl;

import java.util.List;

import org.hzero.message.domain.entity.TemplateArg;
import org.hzero.message.domain.repository.TemplateArgRepository;
import org.hzero.message.infra.mapper.TemplateArgMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.stereotype.Component;

/**
 * 消息模板参数 资源库实现
 *
 * @author fanghan.liu@hand-china.com 2019-10-08 16:49:41
 */
@Component
public class TemplateArgRepositoryImpl extends BaseRepositoryImpl<TemplateArg> implements TemplateArgRepository {

    private final TemplateArgMapper templateArgMapper;

    public TemplateArgRepositoryImpl(TemplateArgMapper templateArgMapper) {
        this.templateArgMapper = templateArgMapper;
    }

    @Override
    public List<TemplateArg> selectByTemplateId(TemplateArg templateArg) {
        return templateArgMapper.selectByTemplateId(templateArg);
    }
}
