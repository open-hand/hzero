package org.hzero.iam.domain.repository;

import java.util.List;
import java.util.Set;

import org.hzero.iam.domain.service.secgrp.enums.SecGrpAuthorityRevokeType;
import org.hzero.iam.domain.service.secgrp.enums.SecGrpAuthorityType;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.iam.domain.entity.SecGrpRevoke;

/**
 * 安全组权限回收资源库
 *
 * @author xingxing.wu@hand-china.com  2019-10-31 14:00:03
 */
public interface SecGrpRevokeRepository extends BaseRepository<SecGrpRevoke> {

    /**
     * 查询针对指定安全组下指定的权限类型，角色被屏蔽的权限
     *
     * @param secGrpId            安全组ID
     * @param shieldRoleId        被屏蔽的决角色ID
     * @param secGrpAuthorityType 权限类型
     * @return
     */
    List<SecGrpRevoke> selectShieldedAuthority(Long secGrpId,
                                               Long shieldRoleId,
                                               SecGrpAuthorityType secGrpAuthorityType);

    /**
     * 批量插入
     *
     * @param revokes 权限回收
     */
    void batchAdd(List<SecGrpRevoke> revokes);

    /**
     * 批量删除
     *
     * @param roleId        角色ID
     * @param secGrpId      安全组ID
     * @param authorityIds  权限ID
     * @param revokeType    回收类型
     * @param authorityType 权限类型
     */
    void batchRemove(Long roleId, Long secGrpId, Set<Long> authorityIds, SecGrpAuthorityRevokeType revokeType, SecGrpAuthorityType authorityType);
}
