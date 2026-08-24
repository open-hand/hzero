package org.hzero.platform.infra.repository.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.mybatis.util.Sqls;
import org.hzero.platform.api.dto.PermissionRangeDTO;
import org.hzero.platform.domain.entity.PermissionRange;
import org.hzero.platform.domain.entity.PermissionRangeExcl;
import org.hzero.platform.domain.entity.PermissionRel;
import org.hzero.platform.domain.repository.PermissionRangeExclRepository;
import org.hzero.platform.domain.repository.PermissionRangeRepository;
import org.hzero.platform.domain.repository.PermissionRelRepository;
import org.hzero.platform.infra.mapper.PermissionRangeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 屏蔽范围 资源库实现
 *
 * @author yunxiang.zhou01@hand-china.com 2018-07-23 15:01:44
 */
@Component
public class PermissionRangeRepositoryImpl extends BaseRepositoryImpl<PermissionRange>
        implements PermissionRangeRepository {

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private PermissionRangeMapper permissionRangeMapper;

    @Autowired
    private PermissionRelRepository permissionRelRepository;

    @Autowired
    private PermissionRangeExclRepository permissionRangeExclRepository;

    @Override
    public List<PermissionRangeDTO> selectPermissionRange(PageRequest pageRequest, PermissionRange permissionRange) {
        PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize());
        List<PermissionRangeDTO> permissionRangeList = permissionRangeMapper.selectPermissionRange(permissionRange);
        if (!CollectionUtils.isEmpty(permissionRangeList)) {
            Map<Long, List<PermissionRangeExcl>> exclMap = permissionRangeExclRepository.listExcl(permissionRangeList.stream().map(PermissionRangeDTO::getRangeId).collect(Collectors.toSet()))
                    .stream()
                    .collect(Collectors.groupingBy(PermissionRangeExcl::getRangeId));
            permissionRangeList.forEach(item ->
                    item.setRangeExclList(exclMap.get(item.getRangeId())).groupExcel()
            );
        }
        return permissionRangeList;
    }

    @Override
    public List<PermissionRange> listDisablePermissionRange(List<Long> permissionRuleIdList) {
        return permissionRangeMapper.listDisablePermissionRange(permissionRuleIdList);
    }

    @Override
    public List<PermissionRange> listEmptyRange(List<Long> rangeIdList) {
        return permissionRangeMapper.listEmptyRange(rangeIdList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PermissionRange insertPermissionRange(PermissionRange permissionRange) {
        if (this.selectCountByCondition(Condition.builder(PermissionRange.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(PermissionRange.FIELD_TENANT_ID, permissionRange.getTenantId())
                        .andEqualTo(PermissionRange.FIELD_SERVICE_NAME, permissionRange.getServiceName())
                        .andEqualTo(PermissionRange.FIELD_SQL_ID, permissionRange.getSqlId())
                        .andEqualTo(PermissionRange.FIELD_TABLE_NAME, permissionRange.getTableName()))
                .build()) != 0) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_EXISTS);
        }

        permissionRange.setEditableFlag(BaseConstants.Flag.YES);
        this.insertSelective(permissionRange);
        saveRangeExcl(permissionRange);
        return permissionRange;
    }

    private void saveRangeExcl(PermissionRange permissionRange) {
        Set<Long> exclRemoveSet = permissionRangeExclRepository.select(PermissionRangeExcl.FIELD_RANGE_ID, permissionRange.getRangeId())
                .stream()
                .map(PermissionRangeExcl::getRangeExcludeId)
                .collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(permissionRange.getRangeServiceNameExclList())) {
            permissionRange.getRangeServiceNameExclList().forEach(item -> {
                if (StringUtils.hasText(item.getServiceName())) {
                    if (item.getRangeExcludeId() == null) {
                        Assert.isTrue(permissionRangeExclRepository
                                .selectCount(new PermissionRangeExcl()
                                        .setRangeId(permissionRange.getRangeId())
                                        .setServiceName(item.getServiceName())) == 0, BaseConstants.ErrorCode.DATA_EXISTS);
                        permissionRangeExclRepository.insert(item
                                .setRangeId(permissionRange.getRangeId())
                                .setTenantId(null)
                                .setSqlId(null));
                    } else {
                        PermissionRangeExcl permissionRangeExcl = permissionRangeExclRepository.selectByPrimaryKey(item.getRangeExcludeId());
                        Assert.isTrue(permissionRangeExcl != null && Objects.equals(permissionRangeExcl.getRangeId(), permissionRange.getRangeId()),
                                BaseConstants.ErrorCode.DATA_INVALID);
                        Assert.isTrue(permissionRangeExclRepository
                                .selectCountByCondition(Condition.builder(PermissionRangeExcl.class)
                                        .andWhere(Sqls.custom()
                                                .andEqualTo(PermissionRangeExcl.FIELD_RANGE_ID, permissionRange.getRangeId())
                                                .andEqualTo(PermissionRangeExcl.FIELD_SERVICE_NAME, item.getServiceName())
                                                .andNotEqualTo(PermissionRangeExcl.FIELD_RANGE_EXCLUDE_ID, item.getRangeExcludeId()))
                                        .build()) == 0, BaseConstants.ErrorCode.DATA_EXISTS);
                        permissionRangeExclRepository.updateOptional(item, PermissionRangeExcl.FIELD_SERVICE_NAME);
                        exclRemoveSet.remove(item.getRangeExcludeId());
                    }
                }
            });
        }
        if (!CollectionUtils.isEmpty(permissionRange.getRangeTenantExclList())) {
            permissionRange.getRangeTenantExclList().forEach(item -> {
                if (Objects.nonNull(item.getTenantId())) {
                    if (item.getRangeExcludeId() == null) {
                        Assert.isTrue(permissionRangeExclRepository
                                .selectCount(new PermissionRangeExcl()
                                        .setRangeId(permissionRange.getRangeId())
                                        .setTenantId(item.getTenantId())) == 0, BaseConstants.ErrorCode.DATA_EXISTS);
                        permissionRangeExclRepository.insert(item
                                .setRangeId(permissionRange.getRangeId())
                                .setServiceName(null)
                                .setSqlId(null));
                    } else {
                        PermissionRangeExcl permissionRangeExcl = permissionRangeExclRepository.selectByPrimaryKey(item.getRangeExcludeId());
                        Assert.isTrue(permissionRangeExcl != null && Objects.equals(permissionRangeExcl.getRangeId(), permissionRange.getRangeId()),
                                BaseConstants.ErrorCode.DATA_INVALID);
                        Assert.isTrue(permissionRangeExclRepository
                                .selectCountByCondition(Condition.builder(PermissionRangeExcl.class)
                                        .andWhere(Sqls.custom()
                                                .andEqualTo(PermissionRangeExcl.FIELD_RANGE_ID, permissionRange.getRangeId())
                                                .andEqualTo(PermissionRangeExcl.FIELD_TENANT_ID, item.getTenantId())
                                                .andNotEqualTo(PermissionRangeExcl.FIELD_RANGE_EXCLUDE_ID, item.getRangeExcludeId()))
                                        .build()) == 0, BaseConstants.ErrorCode.DATA_EXISTS);
                        permissionRangeExclRepository.updateOptional(item, PermissionRangeExcl.FIELD_TENANT_ID);
                        exclRemoveSet.remove(item.getRangeExcludeId());
                    }
                }
            });
        }
        if (!CollectionUtils.isEmpty(permissionRange.getRangeSqlidExclList())) {
            permissionRange.getRangeSqlidExclList().forEach(item -> {
                if (StringUtils.hasText(item.getSqlId())) {
                    if (item.getRangeExcludeId() == null) {
                        Assert.isTrue(permissionRangeExclRepository
                                .selectCount(new PermissionRangeExcl()
                                        .setRangeId(permissionRange.getRangeId())
                                        .setSqlId(item.getSqlId())) == 0, BaseConstants.ErrorCode.DATA_EXISTS);
                        permissionRangeExclRepository.insert(item
                                .setRangeId(permissionRange.getRangeId())
                                .setServiceName(null)
                                .setTenantId(null));
                    } else {
                        PermissionRangeExcl permissionRangeExcl = permissionRangeExclRepository.selectByPrimaryKey(item.getRangeExcludeId());
                        Assert.isTrue(permissionRangeExcl != null && Objects.equals(permissionRangeExcl.getRangeId(), permissionRange.getRangeId()),
                                BaseConstants.ErrorCode.DATA_INVALID);
                        Assert.isTrue(permissionRangeExclRepository
                                .selectCountByCondition(Condition.builder(PermissionRangeExcl.class)
                                        .andWhere(Sqls.custom()
                                                .andEqualTo(PermissionRangeExcl.FIELD_RANGE_ID, permissionRange.getRangeId())
                                                .andEqualTo(PermissionRangeExcl.FIELD_SQL_ID, item.getSqlId())
                                                .andNotEqualTo(PermissionRangeExcl.FIELD_RANGE_EXCLUDE_ID, item.getRangeExcludeId()))
                                        .build()) == 0, BaseConstants.ErrorCode.DATA_EXISTS);
                        permissionRangeExclRepository.updateOptional(item, PermissionRangeExcl.FIELD_SQL_ID);
                        exclRemoveSet.remove(item.getRangeExcludeId());
                    }
                }
            });
        }
        if (!CollectionUtils.isEmpty(exclRemoveSet)) {
            exclRemoveSet.forEach(permissionRangeExclRepository::deleteByPrimaryKey);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PermissionRange updatePermissionRange(PermissionRange permissionRange) {
        permissionRange.judgeDataLegality(this);
        PermissionRange oldRange = this.selectByPrimaryKey(permissionRange);
        if (!Objects.equals(oldRange.getEditableFlag(), BaseConstants.Flag.NO)) {
            this.updateOptional(permissionRange, PermissionRange.FIELD_TABLE_NAME, PermissionRange.FIELD_SQL_ID,
                    PermissionRange.FIELD_DESCRIPTION, PermissionRange.FIELD_TENANT_ID,
                    PermissionRange.FIELD_ENABLED_FLAG, PermissionRange.FIELD_SERVICE_NAME,
                    PermissionRange.FIELD_CUSTOM_RULE_FLAG);
        }
        saveRangeExcl(permissionRange);
        PermissionRange.deleteCache(redisHelper, oldRange);
        PermissionRange.initCache(permissionRange.getRangeId(), redisHelper, this, permissionRangeExclRepository, permissionRelRepository);
        return permissionRange;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermissionRange(Long rangeId, Long tenantId, boolean validEditable) {
        PermissionRange range = this.selectByPrimaryKey(rangeId);
        if (validEditable && Objects.equals(range.getEditableFlag(), BaseConstants.Flag.NO)) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
        this.deleteByPrimaryKey(rangeId);
        permissionRangeExclRepository.select(PermissionRangeExcl.FIELD_RANGE_ID, rangeId)
                .forEach(permissionRangeExclRepository::deleteByPrimaryKey);
        PermissionRel permissionRel = new PermissionRel();
        permissionRel.setRangeId(rangeId);
        permissionRelRepository.delete(permissionRel);
        PermissionRange.deleteCache(redisHelper, range);
    }

    @Override
    public void initAllData() {
        SecurityTokenHelper.close();
        PermissionRange.initAllData(redisHelper, this, permissionRangeExclRepository, permissionRelRepository);
        SecurityTokenHelper.clear();
    }

    @Override
    public PermissionRange queryPermissionRange(String tableName, String serviceName, Long tenantId, String sqlId) {
        return permissionRangeMapper.queryPermissionRule(tableName, serviceName, tenantId, sqlId);
    }
}
