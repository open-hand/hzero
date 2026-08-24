package org.hzero.platform.infra.repository.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.platform.code.builder.DefaultCodeRuleBuilder;
import org.hzero.boot.platform.code.constant.CodeConstants;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.api.dto.CodeRuleDTO;
import org.hzero.platform.api.dto.CodeRuleDistDTO;
import org.hzero.platform.domain.entity.CodeRule;
import org.hzero.platform.domain.entity.CodeRuleDetail;
import org.hzero.platform.domain.entity.CodeRuleDist;
import org.hzero.platform.domain.repository.CodeRuleDetailRepository;
import org.hzero.platform.domain.repository.CodeRuleDistRepository;
import org.hzero.platform.domain.repository.CodeRuleRepository;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.hzero.platform.infra.mapper.CodeRuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>
 * 编码规则repositoryImpl
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/13 18:59
 */
@Component
public class CodeRuleRepositoryImpl extends BaseRepositoryImpl<CodeRule> implements CodeRuleRepository {

    @Autowired
    private CodeRuleDetailRepository codeRuleDetailRepository;

    @Autowired
    private CodeRuleDistRepository codeRuleDistRepository;

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private CodeRuleMapper codeRuleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<CodeRule> codeRuleList) {
        Map<CodeRule, List<CodeRuleDist>> codeRuleListMap = new HashMap<>(codeRuleList.size());
        codeRuleList.forEach(codeRule -> {
            // 删除时判断租户
            if (!CodeRule.judgeDataLegality(this, codeRule.getTenantId(), codeRule.getRuleId())) {
                throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
            }
            // 先删除数据库将并数据存起来再删除redis
            List<CodeRuleDist> codeRuleDistList =
                    codeRuleDistRepository.select(CodeRuleDist.FIELD_RULE_ID, codeRule.getRuleId());
            if (CollectionUtils.isNotEmpty(codeRuleDistList)) {
                codeRuleDistList.forEach(dist -> {
                    // 如果有启用的，则不允许删除 FIX20200612 启用、被使用均不允许删除
                    if (BaseConstants.Flag.YES.equals(dist.getEnabledFlag()) || dist.judgeIsUsed()) {
                        throw new CommonException(HpfmMsgCodeConstants.ERROR_PERMISSION_RULE_FORBID_DELETE);
                    }
                });
                codeRuleDistList.forEach(dist -> {
                    List<CodeRuleDetail> codeRuleDetailList =
                            codeRuleDetailRepository.select(CodeRuleDetail.FIELD_RULE_DIST_ID, dist.getRuleDistId());
                    if (CollectionUtils.isNotEmpty(codeRuleDetailList)) {
                        codeRuleDetailList.forEach(detail -> codeRuleDetailRepository.deleteByPrimaryKey(detail));
                    }
                    codeRuleDistRepository.deleteByPrimaryKey(dist);
                });
                codeRuleListMap.put(codeRule, codeRuleDistList);
            }
            this.deleteByPrimaryKey(codeRule);
        });
        // 删除redis
        Set<Map.Entry<CodeRule, List<CodeRuleDist>>> entrySet = codeRuleListMap.entrySet();
        entrySet.forEach(entry -> {
            List<CodeRuleDist> codeRuleDistList = entry.getValue();
            codeRuleDistList.forEach(dist -> this.deleteCache(entry.getKey(), dist));
        });
    }

    @Override
    public CodeRuleDTO query(Long tenantId, Long codeRuleId, PageRequest pageRequest) {
        CodeRule codeRule = new CodeRule();
        codeRule.setRuleId(codeRuleId);
        codeRule.setTenantId(tenantId);
        List<CodeRuleDTO> codeRuleDTOList = codeRuleMapper.selectCodeRuleList(codeRule);
        if (CollectionUtils.isNotEmpty(codeRuleDTOList) && codeRuleDTOList.size() == 1) {
            CodeRuleDTO codeRuleDTO = codeRuleDTOList.remove(0);
            // 分配详情分页
            PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize());
            List<CodeRuleDistDTO> codeRuleDistList = codeRuleDistRepository.selectCodeRuleDistList(codeRuleId);
            codeRuleDTO.setCodeRuleDistDTOList(codeRuleDistList);
            return codeRuleDTO;
        }
        throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
    }

    @Override
    public List<CodeRuleDTO> selectCodeRuleList(CodeRule codeRule, PageRequest pageRequest) {
        PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize());
        return codeRuleMapper.selectCodeRuleList(codeRule);
    }

    @Override
    public void initCache(CodeRule codeRule) {
        if (codeRule != null && CollectionUtils.isNotEmpty(codeRule.getRuleDistList())) {
            List<CodeRuleDist> codeRuleDistList = codeRule.getRuleDistList();
            codeRuleDistList.forEach(codeRuleDist -> {
                if (BaseConstants.Flag.NO.equals(codeRuleDist.getEnabledFlag())) {
                    return;
                }
                String key = CodeRule.generateCacheKey(codeRule.getRuleLevel(), codeRule.getTenantId(),
                        codeRule.getRuleCode(), codeRuleDist.getLevelCode(), codeRuleDist.getLevelValue());
                List<CodeRuleDetail> codeRuleDetailList = codeRuleDist.getRuleDetailList();
                if (CollectionUtils.isNotEmpty(codeRuleDetailList)) {
                    Map<String, String> map = new HashMap<>(16);
                    codeRuleDetailList.forEach(item -> map.put(String.valueOf(item.getOrderSeq()), redisHelper.toJson(item)));
                    redisHelper.hshPutAll(key, map);
                }
            });
        }
    }

    @Override
    public void initCache(CodeRule codeRule, CodeRuleDist codeRuleDist, List<CodeRuleDetail> codeRuleDetailList) {
        if (CollectionUtils.isNotEmpty(codeRuleDetailList) && codeRule != null && codeRuleDist != null) {
            if (BaseConstants.Flag.NO.equals(codeRuleDist.getEnabledFlag())) {
                return;
            }
            String key = CodeRule.generateCacheKey(codeRule.getRuleLevel(), codeRule.getTenantId(), codeRule.getRuleCode(),
                    codeRuleDist.getLevelCode(), codeRuleDist.getLevelValue());
            Map<String, String> map = new HashMap<>(16);
            codeRuleDetailList.forEach(item -> map.put(String.valueOf(item.getOrderSeq()), redisHelper.toJson(item)));
            redisHelper.hshPutAll(key, map);
        }
    }

    @Override
    public void initCache(String key, List<CodeRuleDetail> codeRuleDetailList) {
        Map<String, String> map = new HashMap<>(16);
        codeRuleDetailList.forEach(item -> map.put(String.valueOf(item.getOrderSeq()), redisHelper.toJson(item)));
        redisHelper.hshPutAll(key, map);
    }

    @Override
    public void deleteCache(CodeRule codeRule) {
        if (codeRule != null && CollectionUtils.isNotEmpty(codeRule.getRuleDistList())) {
            List<CodeRuleDist> codeRuleDistList = codeRule.getRuleDistList();
            codeRuleDistList.forEach(codeRuleDist -> {
                String key = CodeRule.generateCacheKey(codeRule.getRuleLevel(), codeRule.getTenantId(),
                        codeRule.getRuleCode(), codeRuleDist.getLevelCode(), codeRuleDist.getLevelValue());
                redisHelper.delKey(key);
            });
        }
    }

    @Override
    public void deleteCache(CodeRule codeRule, CodeRuleDist codeRuleDist) {
        if (codeRule != null && codeRuleDist != null) {
            clearCache(codeRule.getTenantId(), codeRule.getRuleCode(), codeRule.getRuleLevel(), codeRuleDist.getLevelCode(), codeRule.getRuleLevel());
        }
    }

    @Override
    public void deleteCache(String key) {
        redisHelper.delKey(key);
    }

    @Override
    public List<CodeRuleDetail> getCodeRuleListFromCache(String key) {
        return CodeRuleDetail.getCodeRuleListFromCache(redisHelper, key);
    }

    @Override
    public void clearFailFastCache(Long tenantId, String ruleCode, String level, String levelCode, String levelValue) {
        redisHelper.delKey(new DefaultCodeRuleBuilder
                .CodeRuleKey(tenantId, ruleCode,
                CodeConstants.Level.TENANT.equals(level) && CodeConstants.CodeRuleLevelCode.COMPANY.equals(levelCode) ? CodeConstants.Level.COMPANY : level,
                levelCode, levelValue).getFailFastKey());
        if (CodeConstants.Level.TENANT.equals(level) && BaseConstants.DEFAULT_TENANT_ID.equals(tenantId) && CodeConstants.CodeRuleLevelCode.GLOBAL.equals(levelCode)) {
            redisHelper.delKey(new DefaultCodeRuleBuilder
                    .CodeRuleKey(tenantId, ruleCode, CodeConstants.Level.PLATFORM, levelCode, levelValue).getFailFastKey());
        }
    }

    @Override
    public void clearCache(Long tenantId, String ruleCode, String level, String levelCode, String levelValue) {
        redisHelper.delKey(new DefaultCodeRuleBuilder
                .CodeRuleKey(tenantId, ruleCode,
                CodeConstants.Level.TENANT.equals(level) && CodeConstants.CodeRuleLevelCode.COMPANY.equals(levelCode) ? CodeConstants.Level.COMPANY : level,
                levelCode, levelValue).getKey());
        if (CodeConstants.Level.TENANT.equals(level) && BaseConstants.DEFAULT_TENANT_ID.equals(tenantId) && CodeConstants.CodeRuleLevelCode.GLOBAL.equals(levelCode)) {
            redisHelper.delKey(new DefaultCodeRuleBuilder
                    .CodeRuleKey(tenantId, ruleCode, CodeConstants.Level.PLATFORM, levelCode, levelValue).getKey());
        }

        redisHelper.delKey(new DefaultCodeRuleBuilder
                .CodeRuleKey(tenantId, ruleCode,
                CodeConstants.Level.TENANT.equals(level) && CodeConstants.CodeRuleLevelCode.COMPANY.equals(levelCode) ? CodeConstants.Level.COMPANY : level,
                levelCode, levelValue).getSequenceKey());
        if (CodeConstants.Level.TENANT.equals(level) && BaseConstants.DEFAULT_TENANT_ID.equals(tenantId) && CodeConstants.CodeRuleLevelCode.GLOBAL.equals(levelCode)) {
            redisHelper.delKey(new DefaultCodeRuleBuilder
                    .CodeRuleKey(tenantId, ruleCode, CodeConstants.Level.PLATFORM, levelCode, levelValue).getSequenceKey());
        }

        redisHelper.delKey(new DefaultCodeRuleBuilder
                .CodeRuleKey(tenantId, ruleCode,
                CodeConstants.Level.TENANT.equals(level) && CodeConstants.CodeRuleLevelCode.COMPANY.equals(levelCode) ? CodeConstants.Level.COMPANY : level,
                levelCode, levelValue).getUsedKey());
        if (CodeConstants.Level.TENANT.equals(level) && BaseConstants.DEFAULT_TENANT_ID.equals(tenantId) && CodeConstants.CodeRuleLevelCode.GLOBAL.equals(levelCode)) {
            redisHelper.delKey(new DefaultCodeRuleBuilder
                    .CodeRuleKey(tenantId, ruleCode, CodeConstants.Level.PLATFORM, levelCode, levelValue).getUsedKey());
        }
    }

    @Override
    public CodeRule selectCodeRuleByDetailId(Long ruleDetailId) {
        return codeRuleMapper.selectCodeRuleByDetailId(ruleDetailId);
    }

    @Override
    public List<CodeRuleDTO> getCodeRuleList(Long tenantId, List<String> ruleCodeList) {
        return codeRuleMapper.selectCodeRuleListByCodes(tenantId, ruleCodeList);
    }
}
