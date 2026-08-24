package org.hzero.iam.app.service;

import org.hzero.iam.domain.entity.DocTypeDimension;

import java.util.List;

/**
 * @author qingsheng.chen@hand-china.com 2019-07-02 14:33
 */
public interface DocTypeDimensionAssignService {
    /**
     * 查询角色单据类型定义维度分配列表
     *
     * @param roleId 角色ID
     * @return 角色单据类型定义维度分配列表
     */
    List<DocTypeDimension> listRoleAssign(long roleId, long tenantId);

    /**
     * 查询用户单据类型定义维度分配列表
     *
     * @param userId   用户ID
     * @param tenantId 租户ID
     * @return 用户单据类型定义维度分配列表
     */
    List<DocTypeDimension> listUserAssign(long userId, long tenantId);

    /**
     * 查询安全组单据类型定义维度分配列表
     *
     * @param secGrpId 安全组ID
     * @return 安全组单据类型定义维度分配列表
     */
    List<DocTypeDimension> listSecGrpAssign(long secGrpId);
}
