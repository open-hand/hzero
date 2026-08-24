package org.hzero.iam.infra.repository.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import org.hzero.iam.domain.entity.TenantPermission;
import org.hzero.iam.domain.repository.TenantPermissionRepository;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

/**
 * 租户权限 资源库实现
 *
 * @author KAIBING.JIANG@HAND-CHINA.COM 2019-11-28
 */
@Component
public class TenantPermissionRepositoryImpl extends BaseRepositoryImpl<TenantPermission>
                implements TenantPermissionRepository {

    @Override
    public void removeByPermissionIds(Set<Long> permissionIds) {
        List<TenantPermission> tps = selectByCondition(Condition.builder(TenantPermission.class)
                        .where(Sqls.custom().andIn(TenantPermission.FIELD_PERMISSION_ID, permissionIds)).build());
        batchDelete(tps);
    }

}
