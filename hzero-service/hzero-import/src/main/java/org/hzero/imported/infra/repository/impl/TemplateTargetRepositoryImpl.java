package org.hzero.imported.infra.repository.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.ds.DatasourceHelper;
import org.hzero.boot.platform.ds.constant.DsConstants;
import org.hzero.boot.platform.ds.vo.DatasourceVO;
import org.hzero.boot.platform.rule.entity.RuleScriptVO;
import org.hzero.boot.platform.rule.helper.RuleScriptHelper;
import org.hzero.imported.domain.entity.TemplateTarget;
import org.hzero.imported.domain.repository.TemplateTargetRepository;
import org.hzero.imported.infra.mapper.TemplateTargetMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 模板头配置资源库
 *
 * @author shuangfei.zhu@hand-china.com 2018/11/29 11:04
 */
@Service
public class TemplateTargetRepositoryImpl extends BaseRepositoryImpl<TemplateTarget> implements TemplateTargetRepository {

    @Autowired
    private TemplateTargetMapper templateTargetMapper;
    @Autowired
    private DatasourceHelper datasourceHelper;
    @Autowired
    private RuleScriptHelper ruleScriptHelper;

    @Override
    public List<TemplateTarget> listTemplateTarget(long headerId) {
        List<TemplateTarget> list = templateTargetMapper.listTemplateTarget(headerId);
        list.forEach(item -> {
            if (StringUtils.isNotBlank(item.getDatasourceCode())) {
                DatasourceVO datasource = datasourceHelper.getDatasource(DsConstants.DsPurpose.DI, item.getTenantId(), item.getDatasourceCode());
                if (datasource != null) {
                    item.setDatasourceDesc(datasource.getDescription());
                }
            }
            if (StringUtils.isNotBlank(item.getRuleScriptCode())) {
                RuleScriptVO ruleScript = ruleScriptHelper.getRuleScript(item.getTenantId(), item.getRuleScriptCode());
                if (ruleScript != null) {
                    item.setScriptDescription(ruleScript.getScriptDescription());
                }
            }
        });
        return list;
    }

    @Override
    public int deleteByHeaderId(Long templateId) {
        return templateTargetMapper.deleteByHeaderId(templateId);
    }
}
