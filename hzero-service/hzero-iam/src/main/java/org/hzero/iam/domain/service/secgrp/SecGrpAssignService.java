package org.hzero.iam.domain.service.secgrp;

import java.util.List;

/**
 * 安全组分配接口
 *
 * @author xingxingwu.hand-china.com 2019/12/10 10:50
 */
public interface SecGrpAssignService {

    /**
     * 角色分配安全组
     *
     * @param roleId    角色ID
     * @param secGrpIds 安全组ID列表
     */
    void roleAssignSecGrp(Long roleId, List<Long> secGrpIds);

    /**
     * 角色取消分配安全组
     *
     * @param roleId    角色ID
     * @param secGrpIds 安全组ID列表
     */
    void roleRecycleSecGrp(Long roleId, List<Long> secGrpIds);

    /**
     * 安全组分配给角色
     *
     * @param secGrpId 安全组ID
     * @param roleIds  角色Id列表
     */
    void secGrpAssignRole(Long secGrpId, List<Long> roleIds);

    /**
     * 安全组取消分配给角色
     *
     * @param secGrpId 安全组ID
     * @param roleIds  角色列表
     */
    void secGrpRecycleRole(Long secGrpId, List<Long> roleIds);

    /**
     * 角色屏蔽安全组权限
     *
     * @param roleId        角色ID
     * @param secGrpId      安全组ID
     * @param authorityId   权限Id
     * @param authorityType 权限类型
     */
    void shieldRoleSecGrpAuthority(Long roleId, Long secGrpId, Long authorityId, String authorityType);

    /**
     * 角色取消屏蔽安全组权限
     *
     * @param roleId        角色ID
     * @param secGrpId      安全组ID
     * @param authorityId   权限Id
     * @param authorityType 权限类型
     */
    void cancelShieldRoleSecGrpAuthority(Long roleId, Long secGrpId, Long authorityId, String authorityType);

    /**
     * 用户分配安全组
     *
     * @param userId    用户Id
     * @param secGrpIds 安全组Id
     */
    void userAssignSecGrp(Long userId, List<Long> secGrpIds);

    /**
     * 用户取消安全组
     *
     * @param userId    用户ID
     * @param secGrpIds 安全组ID
     */
    void userRecycleSecGrp(Long userId, List<Long> secGrpIds);
}
