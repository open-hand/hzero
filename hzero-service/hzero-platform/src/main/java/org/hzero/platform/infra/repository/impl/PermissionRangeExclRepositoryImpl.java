package org.hzero.platform.infra.repository.impl;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.domain.entity.PermissionRangeExcl;
import org.hzero.platform.domain.repository.PermissionRangeExclRepository;
import org.hzero.platform.infra.mapper.PermissionRangeExclMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 屏蔽范围黑名单 资源库实现
 *
 * @author qingsheng.chen@hand-china.com 2020-06-10 10:17:25
 */
@Component
public class PermissionRangeExclRepositoryImpl extends BaseRepositoryImpl<PermissionRangeExcl> implements PermissionRangeExclRepository {
    private PermissionRangeExclMapper permissionRangeExclMapper;

    @Autowired
    public PermissionRangeExclRepositoryImpl(PermissionRangeExclMapper permissionRangeExclMapper) {
        this.permissionRangeExclMapper = permissionRangeExclMapper;
    }

    @Override
    public List<PermissionRangeExcl> listExcl(Set<Long> rangeIds) {
        return permissionRangeExclMapper.listExcl(rangeIds);
    }
}
