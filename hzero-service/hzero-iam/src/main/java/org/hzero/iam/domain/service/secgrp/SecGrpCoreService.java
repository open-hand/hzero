package org.hzero.iam.domain.service.secgrp;

import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.service.secgrp.enums.SecGrpAuthorityType;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 安全组领域服务
 *
 * @author bojiangzhou 2020/02/12
 * @author xingxingwu.hand-china.com 2019/10/17 12:03
 */
public interface SecGrpCoreService {

    /**
     * 根据传入的参数检测是否已存在安全组
     *
     * @param tenantId    租户ID
     * @param secGrpLevel 安全组层级
     * @param secGrpCode  安全组编码
     * @throws io.choerodon.core.exception.CommonException 如果已存在则抛出异常
     */
    void checkSecGrpExists(@NotNull Long tenantId, @NotNull String secGrpLevel, @NotNull String secGrpCode);

    /**
     * 初始化新建安全组的相关权限，包含字段权限，数据权限 及维度
     *
     * @param secGrp 新建的安全组
     */
    void initSecGrpAuthority(@NotNull SecGrp secGrp);

    /**
     * 复制源安全组的权限到目标安全组
     *
     * @param sourceSecGrps 源安全组列表
     * @param targetSecGrp  目标安全组
     */
    void copySecGrpAuthority(@NotNull List<SecGrp> sourceSecGrps, @NotNull SecGrp targetSecGrp);

    /**
     * 删除安全组
     *
     * @param secGrpId 安全组ID
     */
    void deleteSecGrp(@NotNull Long secGrpId);

    /**
     * 禁用安全组
     *
     * @param secGrpId 安全组ID
     */
    void disableSecGrp(@NotNull Long secGrpId);

    /**
     * 启用安全组
     *
     * @param secGrpId 安全组ID
     */
    void enableSecGrp(@NotNull Long secGrpId);

    /**
     * 给角色分配安全组，没有权限分配的安全组将抛出异常
     *
     * @param roleId    角色ID
     * @param secGrpIds 安全组ID集合
     */
    void assignRoleSecGrp(Long roleId, List<Long> secGrpIds);

    /**
     * 取消角色已分配的安全组
     *
     * @param roleId    角色ID
     * @param secGrpIds 安全组ID集合
     */
    void recycleRoleSecGrp(Long roleId, List<Long> secGrpIds);

    /**
     * 角色屏蔽安全组权限
     *
     * @param roleId        角色ID
     * @param secGrpId      安全组ID
     * @param authorityId   权限Id
     * @param authorityType 权限类型
     */
    void shieldRoleSecGrpAuthority(Long roleId, Long secGrpId, Long authorityId, SecGrpAuthorityType authorityType);

    /**
     * 角色取消屏蔽安全组权限
     *
     * @param roleId        角色ID
     * @param secGrpId      安全组ID
     * @param authorityId   权限Id
     * @param authorityType 权限类型
     */
    void cancelShieldRoleSecGrpAuthority(Long roleId, Long secGrpId, Long authorityId, SecGrpAuthorityType authorityType);

    /**
     * 给用户分配安全组，没有权限分配的安全组将抛出异常
     *
     * @param userId    用户ID
     * @param secGrpIds 安全组ID集合
     */
    void assignUserSecGrp(Long userId, List<Long> secGrpIds);

    /**
     * 取消用户已分配的安全组
     *
     * @param userId    用户ID
     * @param secGrpIds 安全组ID集合
     */
    void recycleUserSecGrp(Long userId, List<Long> secGrpIds);
}
