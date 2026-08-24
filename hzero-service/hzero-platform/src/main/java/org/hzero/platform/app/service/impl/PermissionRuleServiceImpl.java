package org.hzero.platform.app.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Pair;
import org.hzero.platform.app.service.PermissionRuleService;
import org.hzero.platform.domain.entity.PermissionRange;
import org.hzero.platform.domain.entity.PermissionRel;
import org.hzero.platform.domain.entity.PermissionRule;
import org.hzero.platform.domain.repository.PermissionRangeRepository;
import org.hzero.platform.domain.repository.PermissionRelRepository;
import org.hzero.platform.domain.repository.PermissionRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * 屏蔽规则应用服务默认实现
 *
 * @author yunxiang.zhou01@hand-china.com 2018-07-23 15:01:44
 */
@Service
public class PermissionRuleServiceImpl implements PermissionRuleService {
    private final PermissionRuleRepository permissionRuleRepository;
    private final PermissionRangeRepository permissionRangeRepository;
    private final PermissionRelRepository permissionRelRepository;

    @Autowired
    public PermissionRuleServiceImpl(PermissionRuleRepository permissionRuleRepository,
                                     PermissionRangeRepository permissionRangeRepository,
                                     PermissionRelRepository permissionRelRepository) {
        this.permissionRuleRepository = permissionRuleRepository;
        this.permissionRangeRepository = permissionRangeRepository;
        this.permissionRelRepository = permissionRelRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Pair<PermissionRange, PermissionRule>> savePermission(Long tenantId, List<Pair<PermissionRange, PermissionRule>> permissionList) {
        if (CollectionUtils.isEmpty(permissionList)) {
            return permissionList;
        }
        permissionList.forEach(permission -> {
            // 屏蔽范围
            PermissionRange existsRange = permissionRangeRepository.queryPermissionRange(permission.getFirst().getTableName(),
                    permission.getFirst().getServiceName(),
                    permission.getFirst().getTenantId(),
                    permission.getFirst().getSqlId());
            if (existsRange != null) {
                if (!Objects.equals(existsRange.getDescription(), permission.getFirst().getDescription())
                        || !Objects.equals(existsRange.getEnabledFlag(), permission.getFirst().getEnabledFlag())) {
                    permissionRangeRepository.updatePermissionRange(existsRange.setDescription(permission.getFirst().getDescription()).setEnabledFlag(permission.getFirst().getEnabledFlag()));
                }
                permission.getFirst().setRangeId(existsRange.getRangeId());
            } else {
                permissionRangeRepository.insertSelective(permission.getFirst());
            }
            // 屏蔽规则
            PermissionRule existsRule = permissionRuleRepository.queryPermissionRule(permission.getSecond().getRuleCode(),
                    permission.getSecond().getTenantId());
            if (existsRule != null) {
                permissionRuleRepository.updatePermissionRule(existsRule
                        .setDescription(permission.getSecond().getDescription())
                        .setSqlValue(permission.getSecond().getSqlValue())
                        .setEnabledFlag(permission.getSecond().getEnabledFlag())
                        .setEditableFlag(permission.getFirst().getEditableFlag()));
                permission.getSecond().setRuleId(existsRule.getRuleId());
            } else {
                permissionRuleRepository.insertSelective(permission.getSecond());
            }
            // 关联规则和范围
            PermissionRel existsRel = permissionRelRepository.selectOne(new PermissionRel()
                    .setRangeId(permission.getFirst().getRangeId()).setRuleId(permission.getSecond().getRuleId()));
            if (existsRel == null) {
                PermissionRel permissionRel = new PermissionRel().setRangeId(permission.getFirst().getRangeId())
                        .setRuleId(permission.getSecond().getRuleId()).setEditableFlag(BaseConstants.Flag.NO);
                permissionRel.setTenantId(tenantId);
                permissionRelRepository.insertPermissionRel(permissionRel);
            }
        });
        return permissionList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disablePermission(Long tenantId, List<Long> disablePermissionRuleList) {
        if (CollectionUtils.isEmpty(disablePermissionRuleList)) {
            return;
        }
        disablePermissionRuleList.forEach(disablePermissionRule -> {
            PermissionRule permissionRule = permissionRuleRepository.selectByPrimaryKey(disablePermissionRule);
            if (permissionRule != null) {
                permissionRuleRepository.updatePermissionRule(permissionRule.setEnabledFlag(BaseConstants.Flag.NO));
            }
        });
        permissionRangeRepository.listDisablePermissionRange(disablePermissionRuleList)
                .forEach(permissionRange -> permissionRangeRepository.updatePermissionRange(permissionRange.setEnabledFlag(BaseConstants.Flag.NO)));
    }

    @Override
    public void disableRel(Long tenantId, Collection<Pair<Long, Long>> disableRelList) {
        if (CollectionUtils.isEmpty(disableRelList)) {
            return;
        }
        disableRelList.forEach(disableRel -> {
            PermissionRel permissionRel = permissionRelRepository.selectOne(new PermissionRel().setRangeId(disableRel.getFirst()).setRuleId(disableRel.getSecond()));
            if (permissionRel != null) {
                permissionRelRepository.deletePermissionRel(permissionRel.getPermissionRelId(), false);
            }
        });
        permissionRangeRepository.listEmptyRange(disableRelList.stream().map(Pair::getFirst).collect(Collectors.toList()))
                .forEach(range -> permissionRangeRepository.deletePermissionRange(range.getRangeId(), tenantId, false));
        permissionRuleRepository.listEmptyRule(disableRelList.stream().map(Pair::getSecond).collect(Collectors.toList()))
                .forEach(rule -> permissionRuleRepository.deletePermissionRule(rule.getRuleId(), tenantId, false));
    }
}
