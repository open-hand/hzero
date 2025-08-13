package org.hzero.iam.domain.repository;

import java.util.Set;

import org.hzero.iam.domain.entity.TenantPermission;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 租户权限资源库
 *
 * @author KAIBING.JIANG@HAND-CHINA.COM 2019-11-28 11:51:08
 */
public interface TenantPermissionRepository extends BaseRepository<TenantPermission> {

    /**
     * 根据权限ID集合删除租户权限关系
     * 
     * @param permissionIds 权限ID集合
     */
    void removeByPermissionIds(Set<Long> permissionIds);

}
