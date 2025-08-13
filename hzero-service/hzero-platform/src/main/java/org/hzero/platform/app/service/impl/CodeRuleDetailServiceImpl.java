package org.hzero.platform.app.service.impl;

import org.hzero.core.base.BaseConstants;
import org.hzero.platform.app.service.CodeRuleDetailService;
import org.hzero.platform.domain.entity.CodeRule;
import org.hzero.platform.domain.entity.CodeRuleDetail;
import org.hzero.platform.domain.entity.CodeRuleDist;
import org.hzero.platform.domain.repository.CodeRuleDetailRepository;
import org.hzero.platform.domain.repository.CodeRuleDistRepository;
import org.hzero.platform.domain.repository.CodeRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.exception.CommonException;

/**
 * <p>
 * 编码规则明细service impl
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/13 19:03
 */
@Service
public class CodeRuleDetailServiceImpl implements CodeRuleDetailService {

    @Autowired
    private CodeRuleRepository codeRuleRepository;

    @Autowired
    private CodeRuleDistRepository codeRuleDistRepository;

    @Autowired
    private CodeRuleDetailRepository codeRuleDetailRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CodeRuleDetail insertOrUpdate(Long tenantId, CodeRuleDetail codeRuleDetail) {
        codeRuleDetail.setTenantId(tenantId);
        CodeRuleDist codeRuleDist = codeRuleDistRepository.selectByPrimaryKey(codeRuleDetail.getRuleDistId());
        // 校验当前编码规则是否使用过，如果使用过则禁止新增或更新
        if (codeRuleDist.judgeIsUsed()) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
        // 判断当前新增的数据是当前租户的
        if (!CodeRule.judgeDataLegality(codeRuleRepository, tenantId, codeRuleDist.getRuleId())) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
        if (codeRuleDetail.judgeInsert()) {
            codeRuleDetail.validate(codeRuleDetailRepository);
            return codeRuleDetailRepository.insertCodeRuleDetail(codeRuleDetail);
        } else {
            return codeRuleDetailRepository.updateCodeRuleDetail(codeRuleDetail);
        }
    }
}
