package org.hzero.iam.domain.repository;

import org.hzero.iam.domain.entity.DocTypePermission;
import org.hzero.mybatis.base.BaseRepository;

import java.util.List;

/**
 * @author qingsheng.chen@hand-china.com 2019-07-05 09:18
 */

public interface DocTypePermissionRepository extends BaseRepository<DocTypePermission> {
    /**
     * 查询权限列表
     *
     * @param tenantId  租户ID
     * @param docTypeId 单据类型定义ID
     * @return 权限列表
     */
    List<DocTypePermission> listPermission(Long tenantId, Long docTypeId);

    /**
     * 查询无法关联到单据权限定义的权限
     *
     * @return 权限列表
     */
    List<DocTypePermission> listPermissionNotAssociated();
}
