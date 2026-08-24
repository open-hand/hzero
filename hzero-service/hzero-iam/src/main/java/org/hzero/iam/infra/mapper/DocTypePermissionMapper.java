package org.hzero.iam.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.DocTypePermission;

import java.util.List;

/**
 * 单据权限和数据权限关联表
 *
 * @author qingsheng.chen@hand-china.com 2019-07-04 17:58
 */
public interface DocTypePermissionMapper extends BaseMapper<DocTypePermission> {
    /**
     * 查询权限列表
     *
     * @param tenantId  租户ID
     * @param docTypeId 单据类型定义ID
     * @return 权限列表
     */
    List<DocTypePermission> listPermission(@Param("tenantId") Long tenantId,
                                           @Param("docTypeId") Long docTypeId);

    /**
     * 查询无法关联到单据权限定义的权限
     *
     * @return 权限列表
     */
    List<DocTypePermission> listPermissionNotAssociated();
}
