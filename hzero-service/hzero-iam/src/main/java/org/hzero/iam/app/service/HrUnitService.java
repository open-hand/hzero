package org.hzero.iam.app.service;

import java.util.List;

import org.hzero.iam.domain.entity.HrUnit;

/**
 * <p>
 * 描述
 * </p>
 *
 * @author mingwei.liu@hand-china.com 2018/7/15
 */
public interface HrUnitService {
    /**
     * 查询用户在某租户下所属的组织树
     * 
     * @param tenantId
     * @param userId
     * @return
     */
    List<HrUnit> listWholeHrUnitTreeOfTenant(Long tenantId, Long userId);

    /**
     * tree struct
     * @param roleId 角色id
     * @param unitCode 组织代码
     * @param unitName 组织名称
     * @return
     */
    List<HrUnit> listAssignableHrUnitTreeByRoleId(Long roleId,String unitCode,String unitName);
}
