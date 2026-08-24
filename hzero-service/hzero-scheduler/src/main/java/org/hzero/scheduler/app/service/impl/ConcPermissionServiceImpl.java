package org.hzero.scheduler.app.service.impl;


import java.util.Objects;

import org.hzero.scheduler.app.service.ConcPermissionService;
import org.hzero.scheduler.domain.entity.ConcPermission;
import org.hzero.scheduler.domain.repository.ConcPermissionRepository;
import org.hzero.scheduler.infra.constant.HsdrConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 并发请求权限应用服务默认实现
 *
 * @author shuangfei.zhu@hand-china.com 2019-01-31 09:47:02
 */
@Service
public class ConcPermissionServiceImpl implements ConcPermissionService {

    @Autowired
    private ConcPermissionRepository permissionRepository;

    @Override
    public Page<ConcPermission> selectByConcurrentId(Long concurrentId, Long tenantId, boolean ignore, PageRequest pageRequest) {
        Page<ConcPermission> permissions = permissionRepository.selectByConcurrentId(concurrentId, tenantId, ignore, pageRequest);
        permissions.forEach(permission -> {
            if (Objects.equals(permission.getRoleId(), HsdrConstant.NEGATIVE_ONE)) {
                permission.setRoleId(null);
            }
        });
        return permissions;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConcPermission createPermission(ConcPermission permission) {
        if (permission.getRoleId() == null) {
            permission.setRoleId(HsdrConstant.NEGATIVE_ONE);
        }
        permission.validate(permissionRepository);
        permissionRepository.insertSelective(permission);
        return permission;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConcPermission updatePermission(ConcPermission permission) {
        if (permission.getRoleId() == null) {
            permission.setRoleId(HsdrConstant.NEGATIVE_ONE);
        }
        permissionRepository.updateOptional(permission,
                ConcPermission.FIELD_ENABLED_FLAG,
                ConcPermission.FIELD_LIMIT_QUANTITY,
                ConcPermission.FIELD_START_DATE,
                ConcPermission.FIELD_END_DATE);
        return permission;
    }
}
