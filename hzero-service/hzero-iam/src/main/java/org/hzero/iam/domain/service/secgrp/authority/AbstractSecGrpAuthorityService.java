package org.hzero.iam.domain.service.secgrp.authority;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpAssign;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.SecGrpRepository;
import org.hzero.iam.infra.common.utils.AssertUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * 安全组权限接口抽象实现
 *
 * @param <T> 实际操作对象泛型
 * @author bo.he02@hand-china.com 2020/03/26 10:42
 */
public abstract class AbstractSecGrpAuthorityService<T> implements SecGrpAuthorityService<T> {
    /**
     * 安全组资源库对象
     */
    protected SecGrpRepository secGrpRepository;

    @Autowired
    public void setSecGrpRepository(SecGrpRepository secGrpRepository) {
        this.secGrpRepository = secGrpRepository;
    }

    @Override
    public void addSecGrpAuthority(@Nonnull Long secGrpId, @Nonnull List<T> authorities) {
        // 校验安全组
        SecGrp secGrp = this.validateSecGrp(secGrpId);

        // 添加安全组权限
        this.addSecGrpAuthority(secGrp, authorities);
    }

    @Override
    public void updateSecGrpAuthority(@Nonnull Long secGrpId, @Nonnull List<T> authorities) {
        // 校验安全组
        SecGrp secGrp = this.validateSecGrp(secGrpId);

        // 更新安全组权限
        this.updateSecGrpAuthority(secGrp, authorities);
    }

    @Override
    public void removeSecGrpAuthority(@Nonnull Long secGrpId, @Nonnull List<T> authorities) {
        // 校验安全组
        SecGrp secGrp = this.validateSecGrp(secGrpId);

        // 移除安全组权限
        this.removeSecGrpAuthority(secGrp, authorities);
    }

    @Override
    public void enableSecGrpAuthority(@Nonnull Long secGrpId) {
        // 校验安全组
        SecGrp secGrp = this.validateSecGrp(secGrpId);

        // 启用安全组
        this.enableSecGrpAuthority(secGrp);
    }

    @Override
    public void disableSecGrpAuthority(@Nonnull Long secGrpId) {
        // 校验安全组
        SecGrp secGrp = this.validateSecGrp(secGrpId);

        // 禁用安全组
        this.disableSecGrpAuthority(secGrp);
    }

    @Override
    public void shieldRoleAuthority(@Nonnull Role role, Set<Long> authorityIds) {
        // 回收安全组访问权限
        this.operateShieldRoleAuthority(role, authorityIds, this::removeSecGrpAuthority);
    }

    @Override
    public void cancelShieldRoleAuthority(@Nonnull Role role, Set<Long> authorityIds) {
        // 取消回收安全组访问权限
        this.operateShieldRoleAuthority(role, authorityIds, this::addSecGrpAuthority);
    }

    /**
     * 添加安全组权限核心逻辑
     *
     * @param secGrp      通过安全组校验的安全组
     * @param authorities 权限对象
     */
    protected void addSecGrpAuthority(@Nonnull SecGrp secGrp, @Nonnull List<T> authorities) {
    }

    /**
     * 更新安全组权限核心逻辑
     *
     * @param secGrp      安全组
     * @param authorities 要更新的权限
     */
    protected void updateSecGrpAuthority(@Nonnull SecGrp secGrp, @Nonnull List<T> authorities) {
    }

    /**
     * 移除安全组权限核心逻辑
     *
     * @param secGrp      通过安全组校验的安全组
     * @param authorities 权限对象
     */
    protected void removeSecGrpAuthority(@Nonnull SecGrp secGrp, @Nonnull List<T> authorities) {
    }

    /**
     * 启用安全组的核心逻辑
     *
     * @param secGrp 安全组
     */
    protected void enableSecGrpAuthority(@Nonnull SecGrp secGrp) {
    }

    /**
     * 禁用安全组的核心逻辑
     *
     * @param secGrp 安全组
     */
    protected void disableSecGrpAuthority(@Nonnull SecGrp secGrp) {
    }

    /**
     * 获取屏蔽的角色权限数据
     *
     * @param authorityIds 权限IDs
     * @return 权限数据
     */
    protected List<T> getShieldRoleAuthority(Set<Long> authorityIds) {
        return null;
    }

    /**
     * 屏蔽角色权限的数据查询逻辑
     *
     * @param role         处理的角色
     * @param authorityIds 权限ID
     * @param consumer     屏蔽角色后的权限处理逻辑
     */
    private void operateShieldRoleAuthority(@Nonnull Role role, Set<Long> authorityIds,
                                            @Nonnull BiConsumer<SecGrp, List<T>> consumer) {
        if (CollectionUtils.isNotEmpty(authorityIds)) {
            // 查询权限数据
            List<T> authorities = this.getShieldRoleAuthority(authorityIds);
            if (CollectionUtils.isEmpty(authorities)) {
                return;
            }

            // 获取角色创建的安全组
            List<SecGrp> secGrps = this.secGrpRepository.listRoleCreatedSecGrp(role.getId());
            if (CollectionUtils.isEmpty(secGrps)) {
                return;
            }

            // 处理角色创建的安全组
            for (SecGrp secGrp : secGrps) {
                // 处理安全组权限
                consumer.accept(secGrp, authorities);
            }
        }
    }

    /**
     * 校验安全组
     *
     * @param secGrpId 安全组ID
     * @return 当前安全组的详细信息
     */
    protected SecGrp validateSecGrp(Long secGrpId) {
        // 参数ID不能为空
        AssertUtils.notNull(secGrpId, BaseConstants.ErrorCode.DATA_INVALID);

        // 查询安全组
        SecGrp secGrp = this.secGrpRepository.selectByPrimaryKey(secGrpId);
        if (Objects.isNull(secGrp)) {
            // 安全组不存在
            AssertUtils.notNull(secGrpId, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }

        // 校验安全组的可操作性
        secGrp.checkOperable();

        // 返回安全组
        return secGrp;
    }

    /**
     * 用户数据构建安全组分配数据
     *
     * @param user 用户
     * @return 安全组分配数据
     */
    protected SecGrpAssign buildSecGrpAssign(@Nonnull User user) {
        // 构建结果
        SecGrpAssign secGrpAssign = new SecGrpAssign();

        // 设置参数
        secGrpAssign.setDimensionValue(user.getId());
        secGrpAssign.setTenantId(user.getOrganizationId());

        // 返回结果
        return secGrpAssign;
    }
}
