package org.hzero.platform.infra.repository.impl;

import java.util.List;
import java.util.Objects;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.api.dto.PermissionRelDTO;
import org.hzero.platform.domain.entity.PermissionRange;
import org.hzero.platform.domain.entity.PermissionRel;
import org.hzero.platform.domain.entity.PermissionRule;
import org.hzero.platform.domain.repository.PermissionRangeExclRepository;
import org.hzero.platform.domain.repository.PermissionRangeRepository;
import org.hzero.platform.domain.repository.PermissionRelRepository;
import org.hzero.platform.domain.repository.PermissionRuleRepository;
import org.hzero.platform.infra.mapper.PermissionRelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.exception.CommonException;

/**
 * 屏蔽范围规则关系 资源库实现
 *
 * @author yunxiang.zhou01@hand-china.com 2018-08-29 15:19:45
 */
@Component
public class PermissionRelRepositoryImpl extends BaseRepositoryImpl<PermissionRel> implements PermissionRelRepository {

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private PermissionRelMapper permissionRelMapper;

    @Autowired
    private PermissionRuleRepository permissionRuleRepository;

    @Autowired
    private PermissionRangeRepository permissionRangeRepository;

    @Autowired
    private PermissionRangeExclRepository permissionRangeExclRepository;

    @Override
    public List<PermissionRelDTO> selectPermissionRuleByRangeId(Long rangeId) {
        return permissionRelMapper.selectPermissionRuleByRangeId(rangeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PermissionRel insertPermissionRel(PermissionRel permissionRel) {
        permissionRel.judgeDataLegality(this, permissionRuleRepository);
        if (permissionRel.getEditableFlag() == null) {
            permissionRel.setEditableFlag(BaseConstants.Flag.YES);
        }
        PermissionRule permissionRule = permissionRuleRepository.selectByPrimaryKey(permissionRel.getRuleId());
        permissionRel.setTenantId(permissionRule.getTenantId());
        this.insertSelective(permissionRel);
        PermissionRange.initCache(permissionRel.getRangeId(), redisHelper, permissionRangeRepository, permissionRangeExclRepository, this);
        return permissionRel;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PermissionRel updatePermissionRel(PermissionRel permissionRel) {
        permissionRel.judgeDataLegality(this, permissionRuleRepository);
        PermissionRel rel = selectByPrimaryKey(permissionRel.getPermissionRelId());
        if (Objects.equals(rel.getEditableFlag(), BaseConstants.Flag.NO)) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
        this.updateByPrimaryKeySelective(permissionRel);
        PermissionRange.initCache(permissionRel.getRangeId(), redisHelper, permissionRangeRepository,  permissionRangeExclRepository,this);
        return permissionRel;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermissionRel(Long permissionRelId, boolean validEditable) {
        PermissionRel permissionRel = this.selectByPrimaryKey(permissionRelId);
        if (validEditable && Objects.equals(permissionRel.getEditableFlag(), BaseConstants.Flag.NO)) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
        this.deleteByPrimaryKey(permissionRelId);
        PermissionRange.initCache(permissionRel.getRangeId(), redisHelper, permissionRangeRepository,  permissionRangeExclRepository,this);
    }
}
