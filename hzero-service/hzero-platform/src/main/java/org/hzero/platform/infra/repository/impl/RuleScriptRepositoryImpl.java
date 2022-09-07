package org.hzero.platform.infra.repository.impl;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.domain.entity.RuleScript;
import org.hzero.platform.domain.repository.RuleScriptRepository;
import org.hzero.platform.infra.mapper.RuleScriptMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 规则脚本 资源库实现
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-27 16:22:55
 */
@Component
public class RuleScriptRepositoryImpl extends BaseRepositoryImpl<RuleScript> implements RuleScriptRepository {

    @Autowired
    private RuleScriptMapper ruleScriptMapper;

    @Override
    public Page<RuleScript> pageRuleScript(PageRequest pageRequest, RuleScript ruleScript) {
        return PageHelper.doPageAndSort(pageRequest, () -> ruleScriptMapper.listRuleScript(ruleScript));
    }

    @Override
    public RuleScript selectRuleScript(Long ruleScriptId, Long tenantId) {
        return ruleScriptMapper.selectRuleScriptById(ruleScriptId, tenantId);
    }

    @Override
    public RuleScript selectRuleScriptByCode(String scriptCode, Long tenantId) {
        return ruleScriptMapper.selectRuleScriptByCode(scriptCode, tenantId);
    }
}
