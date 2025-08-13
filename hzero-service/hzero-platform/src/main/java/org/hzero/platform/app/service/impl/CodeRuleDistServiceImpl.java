package org.hzero.platform.app.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.platform.app.service.CodeRuleDistService;
import org.hzero.platform.domain.entity.CodeRule;
import org.hzero.platform.domain.entity.CodeRuleDist;
import org.hzero.platform.domain.repository.CodeRuleDetailRepository;
import org.hzero.platform.domain.repository.CodeRuleDistRepository;
import org.hzero.platform.domain.repository.CodeRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;

/**
 * <p>
 * 编码规则分配service impl
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/13 19:03
 */
@Service
public class CodeRuleDistServiceImpl implements CodeRuleDistService {

    @Autowired
    private CodeRuleRepository codeRuleRepository;

    @Autowired
    private CodeRuleDistRepository codeRuleDistRepository;
    @Autowired
    private CodeRuleDetailRepository codeRuleDetailRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CodeRuleDist insertOrUpdate(Long tenantId, CodeRuleDist codeRuleDist) {
        // 判断当前新增的数据是当前租户的
        if (!CodeRule.judgeDataLegality(codeRuleRepository, tenantId, codeRuleDist.getRuleId())) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
        CodeRule codeRule = codeRuleRepository.selectByPrimaryKey(codeRuleDist.getRuleId());
        tenantId = codeRule.getTenantId();
        codeRuleDist.setTenantId(tenantId);
        Assert.notNull(codeRule, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        if (codeRuleDist.judgeInsert()) {
            codeRuleDist = codeRuleDistRepository.insertCodeRuleDist(codeRuleDist);
        } else {
            codeRuleDist = codeRuleDistRepository.updateCodeRuleDist(codeRuleDist);
        }
        if (BaseConstants.Flag.YES.equals(codeRuleDist.getEnabledFlag())) {
            codeRuleRepository.clearFailFastCache(tenantId, codeRule.getRuleCode(), codeRule.getRuleLevel(), codeRuleDist.getLevelCode(), codeRuleDist.getLevelValue());
        } else {
            codeRuleRepository.clearCache(tenantId, codeRule.getRuleCode(), codeRule.getRuleLevel(), codeRuleDist.getLevelCode(), codeRuleDist.getLevelValue());
        }
        if (CollectionUtils.isNotEmpty(codeRuleDist.getRuleDetailList())) {
            CodeRuleDist finalCodeRuleDist = codeRuleDist;
            codeRuleDist.getRuleDetailList().forEach(item -> codeRuleDetailRepository.insertOrUpdate(item.setRuleDistId(finalCodeRuleDist.getRuleDistId())));
        }
        return codeRuleDist;
    }
}
