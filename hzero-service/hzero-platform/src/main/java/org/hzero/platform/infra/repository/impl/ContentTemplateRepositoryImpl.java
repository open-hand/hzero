package org.hzero.platform.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.domain.entity.ContentTemplate;
import org.hzero.platform.domain.repository.ContentTemplateRepository;
import org.hzero.platform.infra.mapper.ContentTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 内容模板 资源库实现
 *
 * @author xiaoyu.zhao@hand-china.com 2018-08-13 15:37:15
 */
@Component
public class ContentTemplateRepositoryImpl extends BaseRepositoryImpl<ContentTemplate> implements
        ContentTemplateRepository {

    @Autowired
    private ContentTemplateMapper templateMapper;

    @Override
    @ProcessLovValue
    public Page<ContentTemplate> selectTemplates(PageRequest pageRequest, ContentTemplate templates) {
        return PageHelper.doPageAndSort(pageRequest, () -> templateMapper.selectTemplates(templates));
    }
}
