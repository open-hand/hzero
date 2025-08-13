package org.hzero.platform.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.api.dto.PermissionRuleDTO;
import org.hzero.platform.domain.entity.PermissionRange;
import org.hzero.platform.domain.entity.PermissionRel;
import org.hzero.platform.domain.entity.PermissionRule;
import org.hzero.platform.domain.repository.PermissionRangeExclRepository;
import org.hzero.platform.domain.repository.PermissionRangeRepository;
import org.hzero.platform.domain.repository.PermissionRelRepository;
import org.hzero.platform.domain.repository.PermissionRuleRepository;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.hzero.platform.infra.mapper.PermissionRuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 屏蔽规则 资源库实现
 *
 * @author yunxiang.zhou01@hand-china.com 2018-07-23 15:01:44
 */
@Component
public class PermissionRuleRepositoryImpl extends BaseRepositoryImpl<PermissionRule>
        implements PermissionRuleRepository {

    @Autowired
    private PermissionRuleMapper permissionRuleMapper;

    @Autowired
    private PermissionRangeRepository permissionRangeRepository;

    @Autowired
    private PermissionRelRepository permissionRelRepository;

    @Autowired
    private PermissionRangeExclRepository permissionRangeExclRepository;

    @Autowired
    private RedisHelper redisHelper;

    @Override
    public PermissionRule insertPermissionRule(PermissionRule permissionRule) {
        PermissionRule tempRule = new PermissionRule();
        tempRule.setTenantId(permissionRule.getTenantId());
        tempRule.setRuleCode(permissionRule.getRuleCode());
        if (this.selectCount(tempRule) != 0) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_EXISTS);
        }
        permissionRule.setEditableFlag(BaseConstants.Flag.YES);
        this.insertSelective(permissionRule);
        return permissionRule;
    }

    @Override
    public List<String> selectSqlValueByRangeList(List<PermissionRange> permissionRangeList) {
        if (CollectionUtils.isNotEmpty(permissionRangeList)) {
            return permissionRuleMapper.selectSqlValueByRangeList(permissionRangeList);
        }
        return Collections.emptyList();
    }

    @Override
    public List<PermissionRule> listEmptyRule(List<Long> ruleIdList) {
        return permissionRuleMapper.listEmptyRule(ruleIdList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PermissionRule updatePermissionRule(PermissionRule permissionRule) {
        permissionRule.judgeDataLegality(this);
        this.updateOptional(permissionRule, PermissionRule.FIELD_RULE_NAME,
                PermissionRule.FIELD_DESCRIPTION, PermissionRule.FIELD_SQL_VALUE,
                PermissionRule.FIELD_TENANT_ID, PermissionRule.FIELD_ENABLED_FLAG,
                PermissionRule.FIELD_RULE_TYPE_CODE);
        List<PermissionRel> permissionRelList =
                permissionRelRepository.select(PermissionRel.FIELD_RULE_ID, permissionRule.getRuleId());
        if (CollectionUtils.isNotEmpty(permissionRelList)) {
            // 刷新缓存
            permissionRelList.forEach(permissionRel -> PermissionRange.initCache(permissionRel.getRangeId(),
                    redisHelper, permissionRangeRepository, permissionRangeExclRepository, permissionRelRepository));
        }
        return permissionRule;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermissionRule(Long ruleId, Long tenantId, boolean validEditable) {
        PermissionRule rule = this.selectByPrimaryKey(ruleId);
        if (validEditable && Objects.equals(rule.getEditableFlag(), BaseConstants.Flag.NO)) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
        List<PermissionRel> permissionRelList = permissionRelRepository.select(PermissionRel.FIELD_RULE_ID, ruleId);
        // 如果屏蔽规则已经被引用，则报错
        if (CollectionUtils.isNotEmpty(permissionRelList)) {
            throw new CommonException(HpfmMsgCodeConstants.ERROR_PERMISSION_RULE_FORBID_DELETE);
        }
        this.deleteByPrimaryKey(ruleId);
    }

    @Override
    public List<PermissionRuleDTO> selectPermissionRule(PageRequest pageRequest, PermissionRule permissionRule) {
        return PageHelper.doPageAndSort(pageRequest, () -> permissionRuleMapper.selectPermissionRule(permissionRule));
    }

    @Override
    public List<PermissionRuleDTO> selectOrgPermissionRule(PageRequest pageRequest, PermissionRule permissionRule) {
        return PageHelper.doPageAndSort(pageRequest,
                () -> permissionRuleMapper.selectOrgPermissionRule(permissionRule));
    }

    @Override
    public Page<PermissionRuleDTO> listRuleEnabled(Long tenantId, String ruleCode, String ruleName, PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest, () -> permissionRuleMapper.listRuleEnabled(tenantId, ruleCode, ruleName));
    }

    @Override
    public PermissionRule queryPermissionRule(String ruleCode, Long tenantId) {
        return permissionRuleMapper.queryPermissionRule(ruleCode, tenantId);
    }
}
