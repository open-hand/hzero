package org.hzero.imported.infra.repository.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hzero.core.util.FilenameUtils;
import org.hzero.imported.domain.entity.TemplateHeader;
import org.hzero.imported.domain.repository.TemplateHeaderRepository;
import org.hzero.imported.infra.mapper.TemplateHeaderMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 模板头 资源库实现
 *
 * @author shuangfei.zhu@hand-china.com 2018/11/29 11:07
 */
@Component
public class TemplateHeaderRepositoryImpl extends BaseRepositoryImpl<TemplateHeader> implements TemplateHeaderRepository {

    @Autowired
    private TemplateHeaderMapper templateHeaderMapper;

    @Override
    public List<TemplateHeader> selectTemplateHeaderList(String templateCode, String templateName, Long tenantId) {
        List<TemplateHeader> templateHeaders = templateHeaderMapper.selectTemplateHeaderList(templateCode, templateName, tenantId);
        templateHeaders.forEach(header -> {
            if (!StringUtils.isEmpty(header.getTemplateUrl())) {
                header.setTemplateFileName(FilenameUtils.getFileName(header.getTemplateUrl()));
            }
        });
        return templateHeaders;
    }

    @Override
    public TemplateHeader selectHeaderByTemplateId(Long templateId, Long tenantId) {
        TemplateHeader templateHeader = templateHeaderMapper.selectHeaderByTemplateId(templateId, tenantId);
        if (!StringUtils.isEmpty(templateHeader.getTemplateUrl())) {
            templateHeader.setTemplateFileName(FilenameUtils.getFileName(templateHeader.getTemplateUrl()));
        }
        return templateHeader;
    }

    @Override
    public TemplateHeader selectByTargetId(Long targetId) {
        return templateHeaderMapper.selectByTargetId(targetId);
    }
}
