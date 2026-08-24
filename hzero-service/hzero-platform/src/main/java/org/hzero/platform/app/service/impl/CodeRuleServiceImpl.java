package org.hzero.platform.app.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.hzero.boot.platform.code.builder.DefaultCodeRuleBuilder;
import org.hzero.boot.platform.code.constant.CodeConstants;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.platform.api.dto.CodeRuleDTO;
import org.hzero.platform.api.dto.CodeRuleDistDTO;
import org.hzero.platform.api.dto.CodeRuleParamDTO;
import org.hzero.platform.app.service.CodeRuleDistService;
import org.hzero.platform.app.service.CodeRuleService;
import org.hzero.platform.domain.entity.CodeRule;
import org.hzero.platform.domain.entity.CodeRuleDetail;
import org.hzero.platform.domain.entity.CodeRuleDist;
import org.hzero.platform.domain.entity.CodeRuleValue;
import org.hzero.platform.domain.repository.CodeRuleDetailRepository;
import org.hzero.platform.domain.repository.CodeRuleDistRepository;
import org.hzero.platform.domain.repository.CodeRuleRepository;
import org.hzero.platform.domain.repository.CodeRuleValueRepository;
import org.hzero.platform.domain.service.CodeRuleDomainService;
import org.hzero.platform.infra.constant.FndConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;

/**
 * <p>
 * 编码规则service impl
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/13 19:03
 */
@Service
public class CodeRuleServiceImpl implements CodeRuleService {

    @Autowired
    private CodeRuleDomainService codeRuleDomainService;
    @Autowired
    private CodeRuleDistService codeRuleDistService;
    @Autowired
    private CodeRuleRepository codeRuleRepository;
    @Autowired
    private CodeRuleDistRepository codeRuleDistRepository;
    @Autowired
    private CodeRuleDetailRepository codeRuleDetailRepository;
    @Autowired
    private CodeRuleValueRepository codeRuleValueRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CodeRule insertOrUpdate(CodeRule codeRule) {
        if (codeRule.judgeInsert()) {
            CodeRule tempCodeRule = new CodeRule();
            tempCodeRule.setTenantId(codeRule.getTenantId());
            tempCodeRule.setRuleCode(codeRule.getRuleCode());
            if (CollectionUtils.isNotEmpty(codeRuleRepository.select(tempCodeRule))) {
                throw new CommonException(BaseConstants.ErrorCode.DATA_EXISTS);
            }
            // 新增头
            codeRuleRepository.insertSelective(codeRule);
            // 新增头时默认新增一个当前层级全局级的分配信息
            //codeRuleDistRepository.insertGlobalDist(codeRule.getRuleId());
            codeRuleDistRepository.insertGlobalDist(codeRule.getRuleId(), codeRule.getTenantId());
            codeRuleRepository.clearFailFastCache(codeRule.getTenantId(), codeRule.getRuleCode(), codeRule.getRuleLevel(), FndConstants.CodeRuleLevelCode.GLOBAL, FndConstants.CodeRuleLevelCode.GLOBAL);
        } else {
            codeRuleRepository.updateByPrimaryKey(codeRule);
        }
        if (CollectionUtils.isNotEmpty(codeRule.getRuleDistList())) {
            codeRule.getRuleDistList().forEach(item ->
                    codeRuleDistService.insertOrUpdate(codeRule.getTenantId(), item.setRuleId(codeRule.getRuleId()))
            );
        }
        return codeRule;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String generateCode(String level, Long tenantId, String ruleCode, String levelCode, String levelValue,
                               Map<String, String> variableMap) {
        return codeRuleDomainService.generateCode(level, tenantId, ruleCode, levelCode, levelValue, variableMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String generatePlatformLevelCode(CodeRuleParamDTO codeRuleDTO) {
        return this.generateCode(
                FndConstants.Level.TENANT,
                BaseConstants.DEFAULT_TENANT_ID,
                codeRuleDTO.getRuleCode(),
                FndConstants.CodeRuleLevelCode.GLOBAL,
                FndConstants.CodeRuleLevelCode.GLOBAL,
                codeRuleDTO.getVariableMap()
        );
    }

    @Override
    public String generatePlatformLevelCode(String ruleCode, Map<String, String> variableMap) {
        CodeRuleParamDTO codeRuleParamDTO = new CodeRuleParamDTO();
        codeRuleParamDTO.setRuleCode(ruleCode);
        codeRuleParamDTO.setLevelCode(FndConstants.CodeRuleLevelCode.GLOBAL);
        codeRuleParamDTO.setLevelValue(FndConstants.CodeRuleLevelCode.GLOBAL);
        codeRuleParamDTO.setVariableMap(variableMap);
        return generatePlatformLevelCode(codeRuleParamDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String generateTenantLevelCode(Long tenantId, String ruleCode, String levelCode, String levelValue, Map<String, String> variableMap) {
        return this.generateCode(
                FndConstants.Level.TENANT,
                tenantId,
                ruleCode,
                levelCode,
                levelValue,
                variableMap
        );
    }

    @Override
    public CodeRule getCodeRule(Long tenantId, long codeRuleId) {
        CodeRule codeRule = codeRuleRepository.selectByPrimaryKey(codeRuleId);
        if (codeRule == null || !Objects.equals(codeRule.getTenantId(), tenantId)) {
            return null;
        }
        return getCodeRuleDist(codeRule);
    }

    @Override
    public CodeRule getCodeRule(Long tenantId, String ruleCode) {
        CodeRule codeRule = codeRuleRepository.selectOne(new CodeRule().setTenantId(tenantId).setRuleCode(ruleCode));
        if (codeRule == null) {
            return null;
        }
        return getCodeRuleDist(codeRule);
    }

    @Override
    public List<CodeRuleDetail> listCodeRuleWithPrevious(Long tenantId, String ruleCode, String ruleLevel, String levelCode, String levelValue, String previousRuleLevel, String previousLevelValue) {
        // 客户端兼容：原平台级 = 租户级 + （租户ID = 0）
        if (CodeConstants.Level.PLATFORM.equals(ruleLevel) && BaseConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
            ruleLevel = CodeConstants.Level.TENANT;
        }
        return codeRuleDetailRepository.listCodeRuleWithPrevious(tenantId, ruleCode, ruleLevel, levelCode, levelValue,
                previousRuleLevel, previousLevelValue);
    }

    @Override
    public int updateDetail(Long tenantId, CodeRuleDetail codeRuleDetail) {
        CodeRule codeRule = codeRuleRepository.selectCodeRuleByDetailId(codeRuleDetail.getRuleDetailId());
        Assert.notNull(codeRule, "Unknown CodeRuleDetail : " + codeRuleDetail.getRuleDetailId());
        DefaultCodeRuleBuilder.CodeRuleKey codeRuleKey = codeRuleDetail.getCodeRuleKey();
        if (codeRuleKey != null && codeRuleKey.getPrevious() != null) {
            CodeRuleValue codeRuleValue = codeRuleValueRepository.selectOne(new CodeRuleValue()
                    .setRuleLevel(codeRuleKey.getPreviousLevel()).setRuleDetailId(codeRuleDetail.getRuleDetailId()).setLevelValue(codeRuleKey.getPreviousLevelValue()));
            if (codeRuleValue == null) {
                codeRuleValue = new CodeRuleValue()
                        .setRuleDetailId(codeRuleDetail.getRuleDetailId())
                        .setRuleLevel(codeRuleKey.getPreviousLevel())
                        .setLevelValue(codeRuleKey.getPreviousLevelValue())
                        .setCurrentValue(codeRuleDetail.getCurrentValue())
                        .setTenantId(codeRuleDetail.getTenantId())
                        .setResetDate(codeRuleDetail.getResetDate());
                return codeRuleValueRepository.insertSelective(codeRuleValue);
            } else {
                codeRuleValue.setCurrentValue(codeRuleDetail.getCurrentValue())
                        .setResetDate(codeRuleDetail.getResetDate());
                return codeRuleValueRepository.updateOptional(codeRuleValue, CodeRuleValue.FIELD_CURRENT_VALUE, CodeRuleValue.FIELD_RESET_DATE);
            }
        } else {
            return codeRuleDetailRepository.updateCodeRuleDetailWithNotOVN(codeRuleDetail);
        }
    }

    @Override
    public List<CodeRuleDTO> getCodeRuleList(Long tenantId, List<String> ruleCodeList) {
        List<CodeRuleDTO> codeRules = codeRuleRepository.getCodeRuleList(tenantId, ruleCodeList);
        if (CollectionUtils.isEmpty(codeRules)) {
            return Collections.emptyList();
        }
        codeRules.forEach(codeRuleDTO -> {
            List<CodeRuleDistDTO> codeRuleDistList =
                    codeRuleDistRepository.selectCodeRuleDistList(codeRuleDTO.getRuleId());
            codeRuleDTO.setCodeRuleDistDTOList(codeRuleDistList);
        });
        return codeRules;
    }

    private CodeRule getCodeRuleDist(CodeRule codeRule) {
        List<CodeRuleDist> codeRuleDistList = codeRuleDistRepository.selectByCondition(Condition.builder(CodeRuleDist.class)
                .andWhere(Sqls.custom().andEqualTo(CodeRuleDist.FIELD_RULE_ID, codeRule.getRuleId())
                        .andEqualTo(CodeRuleDist.FIELD_ENABLED_FLAG, BaseConstants.Flag.YES))
                .build());
        codeRule.setRuleDistList(codeRuleDistList);
        if (CollectionUtils.isNotEmpty(codeRuleDistList)) {
            Map<Long, List<CodeRuleDetail>> codeRuleDetailMap = codeRuleDetailRepository.selectByCondition(Condition.builder(CodeRuleDetail.class)
                    .andWhere(Sqls.custom().andIn(CodeRuleDetail.FIELD_RULE_DIST_ID, codeRuleDistList.stream()
                            .map(CodeRuleDist::getRuleDistId).collect(Collectors.toSet())))
                    .build())
                    .stream()
                    .collect(Collectors.groupingBy(CodeRuleDetail::getRuleDistId));
            if (MapUtils.isNotEmpty(codeRuleDetailMap)) {
                codeRuleDistList.forEach(item ->
                        item.setRuleDetailList(codeRuleDetailMap.get(item.getRuleDistId()))
                );
            }
        }
        return codeRule;
    }

}
