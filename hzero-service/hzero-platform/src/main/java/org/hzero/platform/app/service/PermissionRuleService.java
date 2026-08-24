package org.hzero.platform.app.service;

import org.hzero.core.util.Pair;
import org.hzero.platform.domain.entity.PermissionRange;
import org.hzero.platform.domain.entity.PermissionRule;

import java.util.Collection;
import java.util.List;

/**
 * 屏蔽规则应用服务
 *
 * @author yunxiang.zhou01@hand-china.com 2018-07-23 15:01:44
 */
public interface PermissionRuleService {
    /**
     * 批量保存权限
     *
     * @param tenantId   租户ID
     * @param permission 权限列表
     * @return 权限列表
     */
    List<Pair<PermissionRange, PermissionRule>> savePermission(Long tenantId, List<Pair<PermissionRange, PermissionRule>> permission);

    /**
     * 批量禁用数据权限
     *
     * @param tenantId                  租户ID
     * @param disablePermissionRuleList 禁用权限规则列表
     */
    void disablePermission(Long tenantId, List<Long> disablePermissionRuleList);

    /**
     * 批量删除数据权限和范围关联
     *
     * @param tenantId   租户ID
     * @param disableRelList 警用关系列表
     */
    void disableRel(Long tenantId, Collection<Pair<Long, Long>> disableRelList);
}
