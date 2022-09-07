package org.hzero.platform.app.service.impl;

import io.choerodon.core.exception.CommonException;
import org.hzero.core.base.BaseConstants;
import org.hzero.platform.app.service.ContentTemplateService;
import org.hzero.platform.domain.entity.ContentTemplate;
import org.hzero.platform.domain.repository.ContentTemplateRepository;
import org.hzero.platform.infra.mapper.ContentTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 内容模板管理服务实现类
 *
 * @author xiaoyu.zhao@hand-china.com 2019/06/25 15:45
 */
@Service
public class ContentTemplateServiceImpl implements ContentTemplateService {

    @Autowired
    private ContentTemplateRepository templateRepository;
    @Autowired
    private ContentTemplateMapper templateMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ContentTemplate insertTemplates(ContentTemplate templates) {
        int count = templateMapper.selectTemplateByTenantAndCode(templates.getTemplateCode(), templates.getTenantId());
        if (count != 0) {
            // 当前保存的数据在数据库中已经存在
            throw new CommonException(BaseConstants.ErrorCode.DATA_EXISTS);
        }
        templateRepository.insertSelective(templates);
        return templates;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ContentTemplate updateTemplate(ContentTemplate templates) {
        // 更新参数
        templateRepository.updateOptional(templates, ContentTemplate.FIELD_ENABLED_FLAG,
                ContentTemplate.FIELD_TEMPLATE_NAME, ContentTemplate.FIELD_TEMPLATE_AVATAR,
                ContentTemplate.FIELD_TEMPLATE_PATH, ContentTemplate.FIELD_TEMPLATE_LEVEL_CODE);
        return templates;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeTemplate(ContentTemplate templates) {
        Assert.notNull(templates.getTemplateId(), BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        templateRepository.deleteByPrimaryKey(templates.getTemplateId());
    }

}
