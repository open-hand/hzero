package org.hzero.iam.domain.service.secgrp.observer.dim;

import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.SecGrpDclDim;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

/**
 * 数据维度变更角色订阅者接口
 *
 * @author bo.he02@hand-china.com 2020/04/21
 */
public interface RoleSecGrpDimObserver extends SecGrpDimObserver {
    /**
     * 给角色分配安全组维度
     *
     * @param roles         需要处理维度的角色
     * @param dim           维度范围
     * @param authTypeCodes 待处理的维度码
     */
    void assignRolesDim(@Nonnull List<Role> roles, @Nonnull SecGrpDclDim dim, @Nonnull Set<String> authTypeCodes);

    /**
     * 回收角色分配的安全组维度
     *
     * @param roles         需要处理维度的角色
     * @param dim           维度范围
     * @param authTypeCodes 待处理的维度码
     */
    void recycleRolesDim(@Nonnull List<Role> roles, @Nonnull SecGrpDclDim dim, @Nonnull Set<String> authTypeCodes);

    /**
     * 回收角色分配的安全组维度行数据
     *
     * @param roles         需要处理维度的角色
     * @param dim           维度范围
     * @param authTypeCodes 待处理的维度码
     */
    void recycleRolesDimLine(@Nonnull List<Role> roles, @Nonnull SecGrpDclDim dim, @Nonnull Set<String> authTypeCodes);
}
