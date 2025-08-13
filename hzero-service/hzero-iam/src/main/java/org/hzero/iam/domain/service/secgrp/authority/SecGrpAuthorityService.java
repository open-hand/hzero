package org.hzero.iam.domain.service.secgrp.authority;

import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.service.secgrp.enums.SecGrpAuthorityType;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

/**
 * 安全组权限接口
 *
 * @author bojiangzhou 2020/02/12
 */
public interface SecGrpAuthorityService<T> {

    /**
     * 是否是支持的权限类型处理器
     *
     * @param authorityType 权限类型
     * @return 判断结果 true 支持当前处理 false 不支持当前处理
     */
    boolean support(@Nonnull SecGrpAuthorityType authorityType);

    /**
     * 将角色的权限赋予安全组
     *
     * @param secGrp 安全组
     * @param roleId 角色ID
     */
    default void initSecGrpAuthority(@Nonnull SecGrp secGrp, @Nonnull Long roleId) {
    }

    /**
     * 复制安全组的权限
     *
     * @param sourceSecGrps 源安全组集合，可能有多个
     * @param targetSecGrp  复制到的目标安全组
     */
    void copySecGrpAuthority(@Nonnull List<SecGrp> sourceSecGrps, @Nonnull SecGrp targetSecGrp);

    /**
     * 根据安全组删除权限
     * <p>
     * 注意：当前版本仅用于删除草稿状态的安全组权限关系，不能用于删除安全组(还需删除分配关系)
     *
     * @param secGrpId 要删除的安全组ID
     * @since 1.3.0
     */
    void deleteAuthorityBySecGrpId(@Nonnull Long secGrpId);

    /**
     * 添加安全组权限
     *
     * @param secGrpId    安全组
     * @param authorities 要添加的权限
     */
    default void addSecGrpAuthority(@Nonnull Long secGrpId, @Nonnull List<T> authorities) {
    }

    /**
     * 更新安全组权限
     *
     * @param secGrpId    安全组
     * @param authorities 要更新的权限
     */
    default void updateSecGrpAuthority(@Nonnull Long secGrpId, @Nonnull List<T> authorities) {
    }

    /**
     * 移除安全组中的权限
     *
     * @param secGrpId    安全组
     * @param authorities 要移除的权限
     */
    default void removeSecGrpAuthority(@Nonnull Long secGrpId, @NotEmpty List<T> authorities) {
    }

    /**
     * 启用安全组权限
     *
     * @param secGrpId 安全组ID
     */
    default void enableSecGrpAuthority(@Nonnull Long secGrpId) {
    }

    /**
     * 禁用安全组权限
     *
     * @param secGrpId 安全组ID
     */
    default void disableSecGrpAuthority(@Nonnull Long secGrpId) {
    }

    /**
     * 给角色分配安全组权限
     *
     * @param role   角色
     * @param secGrp 安全组
     */
    default void assignRoleSecGrpAuthority(@Nonnull Role role, @Nonnull SecGrp secGrp) {
    }

    /**
     * 回收角色已分配的安全组权限
     *
     * @param role   角色
     * @param secGrp 安全组
     */
    default void recycleRoleSecGrpAuthority(@Nonnull Role role, @Nonnull SecGrp secGrp) {
    }

    /**
     * 屏蔽指定的权限(权限ID与具体的类型相关，在调用的时候需要在外层调用出判断需要处理的服务)
     *
     * @param role         角色
     * @param authorityIds 指定的权限IDs
     * @see SecGrpAuthorityService#support(org.hzero.iam.domain.service.secgrp.enums.SecGrpAuthorityType)
     */
    default void shieldRoleAuthority(@Nonnull Role role, Set<Long> authorityIds) {
    }

    /**
     * 取消屏蔽指定的权限(权限ID与具体的类型相关，在调用的时候需要在外层调用出判断需要处理的服务)
     *
     * @param role         角色
     * @param authorityIds 指定的权限IDs
     * @see SecGrpAuthorityService#support(org.hzero.iam.domain.service.secgrp.enums.SecGrpAuthorityType)
     */
    default void cancelShieldRoleAuthority(@Nonnull Role role, Set<Long> authorityIds) {
    }

    /**
     * 给用户分配安全组权限
     *
     * @param user   用户
     * @param secGrp 安全组
     */
    default void assignUserSecGrpAuthority(@Nonnull User user, @Nonnull SecGrp secGrp) {
    }

    /**
     * 回收用户已分配的安全组权限
     *
     * @param user   用户
     * @param secGrp 安全组
     */
    default void recycleUserSecGrpAuthority(@Nonnull User user, @Nonnull SecGrp secGrp) {
    }
}
