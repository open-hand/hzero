package org.hzero.scheduler.infra.repository.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.scheduler.domain.entity.ConcPermission;
import org.hzero.scheduler.domain.repository.ConcPermissionRepository;
import org.hzero.scheduler.infra.mapper.ConcPermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 并发请求权限 资源库实现
 *
 * @author shuangfei.zhu@hand-china.com 2019-01-31 09:47:02
 */
@Component
public class ConcPermissionRepositoryImpl extends BaseRepositoryImpl<ConcPermission> implements ConcPermissionRepository {

    @Autowired
    private ConcPermissionMapper permissionMapper;

    @Override
    public Page<ConcPermission> selectByConcurrentId(Long concurrentId, Long tenantId, boolean ignore, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> permissionMapper.selectByConcurrentId(concurrentId, tenantId, ignore));
    }

    @Override
    public List<ConcPermission> selectQuantity(Long roleId, Long tenantId, Long concurrentId) {
        return permissionMapper.selectQuantity(roleId, tenantId, concurrentId, LocalDateTime.now().toLocalDate());
    }
}
