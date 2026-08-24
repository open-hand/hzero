package org.hzero.platform.infra.repository.impl;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.platform.code.constant.CodeConstants;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.api.dto.CodeRuleDistDTO;
import org.hzero.platform.domain.entity.CodeRule;
import org.hzero.platform.domain.entity.CodeRuleDetail;
import org.hzero.platform.domain.entity.CodeRuleDist;
import org.hzero.platform.domain.repository.CodeRuleDetailRepository;
import org.hzero.platform.domain.repository.CodeRuleDistRepository;
import org.hzero.platform.domain.repository.CodeRuleRepository;
import org.hzero.platform.infra.constant.FndConstants;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.hzero.platform.infra.mapper.CodeRuleDistMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 编码规则分配repositoryImpl
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/13 18:56
 */
@Component
public class CodeRuleDistRepositoryImpl extends BaseRepositoryImpl<CodeRuleDist> implements CodeRuleDistRepository {

    @Autowired
    private CodeRuleDetailRepository codeRuleDetailRepository;

    @Autowired
    private CodeRuleRepository codeRuleRepository;

    @Autowired
    private CodeRuleDistMapper codeRuleDistMapper;

    @Override
    public CodeRuleDist insertGlobalDist(Long ruleId) {
        CodeRuleDist codeRuleDist = new CodeRuleDist();
        codeRuleDist.setRuleId(ruleId);
        codeRuleDist.setEnabledFlag(BaseConstants.Flag.NO);
        codeRuleDist.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
        codeRuleDist.setLevelCode(FndConstants.CodeRuleLevelCode.GLOBAL);
        codeRuleDist.setLevelValue(FndConstants.CodeRuleLevelCode.GLOBAL);
        this.insertSelective(codeRuleDist);
        return codeRuleDist;
    }

    @Override
    public CodeRuleDist insertGlobalDist(Long ruleId, Long tenantId) {
        CodeRuleDist codeRuleDist = new CodeRuleDist();
        codeRuleDist.setRuleId(ruleId);
        codeRuleDist.setTenantId(tenantId);
        codeRuleDist.setEnabledFlag(BaseConstants.Flag.NO);
        codeRuleDist.setLevelCode(FndConstants.CodeRuleLevelCode.GLOBAL);
        codeRuleDist.setLevelValue(FndConstants.CodeRuleLevelCode.GLOBAL);
        this.insertSelective(codeRuleDist);
        return codeRuleDist;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CodeRuleDist insertOrUpdate(CodeRuleDist codeRuleDist) {
        if (codeRuleDist.judgeInsert()) {
            List<CodeRuleDist> codeRuleDistList = this.select(codeRuleDist);
            if (CollectionUtils.isNotEmpty(codeRuleDistList)) {
                throw new CommonException(BaseConstants.ErrorCode.DATA_EXISTS);
            }
            this.insertSelective(codeRuleDist);
        } else {
            this.updateByPrimaryKeySelective(codeRuleDist);
        }
        return codeRuleDist;
    }

    @Override
    public CodeRuleDist updateCodeRuleDist(CodeRuleDist codeRuleDist) {
        // 查询出历史数据准备删除
        CodeRule oldCodeRule = new CodeRule();
        oldCodeRule.setRuleId(codeRuleDist.getRuleId());
        oldCodeRule = codeRuleRepository.selectByPrimaryKey(oldCodeRule);
        CodeRuleDist oldCodeRuleDist = new CodeRuleDist();
        oldCodeRuleDist.setRuleDistId(codeRuleDist.getRuleDistId());
        oldCodeRuleDist = this.selectByPrimaryKey(oldCodeRuleDist);
        // 得到更新前的key
        String oldKey = CodeRule.generateCacheKey(oldCodeRule.getRuleLevel(), oldCodeRule.getTenantId(),
                oldCodeRule.getRuleCode(), oldCodeRuleDist.getLevelCode(), oldCodeRuleDist.getLevelValue());
        List<CodeRuleDetail> codeRuleDetailList = codeRuleRepository.getCodeRuleListFromCache(oldKey);
        // 将是否启用过设置为null，表示不更新这个字段
        codeRuleDist.setUsedFlag(null);
        if (CodeConstants.CodeRuleLevelCode.GLOBAL.equals(codeRuleDist.getLevelCode())) {
            codeRuleDist.setLevelValue(CodeConstants.CodeRuleLevelCode.GLOBAL);
        }
        this.updateByPrimaryKeySelective(codeRuleDist);
        // 更新redis
        codeRuleRepository.deleteCache(oldKey);
        codeRuleRepository.initCache(oldCodeRule, codeRuleDist, codeRuleDetailList);
        return codeRuleDist;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDist(Long tenantId, List<CodeRuleDist> codeRuleDistList) {
        CodeRule codeRule = null;
        // 先删除数据库
        for (CodeRuleDist codeRuleDist : codeRuleDistList) {
            // FIX20200612 删除编码规则分配，先校验是否被使用
            if (codeRuleDist.judgeIsUsed()) {
                throw new CommonException(HpfmMsgCodeConstants.ERROR_PERMISSION_RULE_FORBID_DELETE);
            }
            if (codeRule == null) {
                codeRule = new CodeRule();
                codeRule.setRuleId(codeRuleDist.getRuleId());
                if (tenantId.equals(BaseConstants.DEFAULT_TENANT_ID)) {
                    codeRule = codeRuleRepository.selectByPrimaryKey(codeRule);
                } else {
                    // 校验数据是属于当前租户的合法性
                    codeRule.setTenantId(tenantId);
                    codeRule = codeRuleRepository.selectOne(codeRule);
                }
                assert codeRule != null : BaseConstants.ErrorCode.DATA_NOT_EXISTS;
            }
            List<CodeRuleDetail> codeRuleDetailList =
                    codeRuleDetailRepository.select(CodeRuleDetail.FIELD_RULE_DIST_ID, codeRuleDist.getRuleDistId());
            if (CollectionUtils.isNotEmpty(codeRuleDetailList)) {
                codeRuleDetailList.forEach(detail -> codeRuleDetailRepository.deleteByPrimaryKey(detail));
            }
            this.deleteByPrimaryKey(codeRuleDist);
        }
        // 删除redis
        for (CodeRuleDist codeRuleDist : codeRuleDistList) {
            codeRuleRepository.deleteCache(codeRule, codeRuleDist);
        }
    }

    @Override
    public List<CodeRuleDistDTO> selectCodeRuleDistList(Long ruleId) {
        CodeRuleDist codeRuleDist = new CodeRuleDist();
        codeRuleDist.setRuleId(ruleId);
        return codeRuleDistMapper.selectCodeRuleDistList(codeRuleDist);
    }

    @Override
    public CodeRuleDistDTO selectCodeRuleDistAndDetail(Long tenantId, Long ruleDistId) {
        CodeRuleDist codeRuleDist = new CodeRuleDist();
        codeRuleDist.setRuleDistId(ruleDistId);
        codeRuleDist.setTenantId(tenantId);
        List<CodeRuleDistDTO> codeRuleDistList = codeRuleDistMapper.selectCodeRuleDistList(codeRuleDist);
        if (CollectionUtils.isNotEmpty(codeRuleDistList) && codeRuleDistList.size() == 1) {
            CodeRuleDistDTO codeRuleDistDTO = codeRuleDistList.remove(0);
            codeRuleDistDTO.setCodeRuleDetailDTOList(codeRuleDetailRepository.selectCodeRuleDetailList(ruleDistId));
            return codeRuleDistDTO;
        }
        throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
    }

    @Override
    public CodeRuleDist insertCodeRuleDist(CodeRuleDist codeRuleDist) {
        // 校验是否存在重复数据
        CodeRuleDist tempDist = new CodeRuleDist();
        tempDist.setRuleId(codeRuleDist.getRuleId());
        tempDist.setLevelCode(codeRuleDist.getLevelCode());
        tempDist.setLevelValue(codeRuleDist.getLevelValue());
        if (selectCount(tempDist) != 0) {   // 不知道为啥这个位置 使用select()方法查询条件不生效
            throw new CommonException(BaseConstants.ErrorCode.DATA_EXISTS);
        }

        this.insertSelective(codeRuleDist);
        return codeRuleDist;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDistUseFlag(Long ruleDistId) {
        CodeRuleDist codeRuleDist = this.selectByPrimaryKey(ruleDistId);
        codeRuleDist.setUsedFlag(BaseConstants.Flag.YES);
        return this.updateOptional(codeRuleDist, CodeRuleDist.FIELD_USED_FLAG);
    }
}
