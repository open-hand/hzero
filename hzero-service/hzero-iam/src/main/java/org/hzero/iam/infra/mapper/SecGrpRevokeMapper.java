package org.hzero.iam.infra.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;

import org.hzero.iam.domain.entity.SecGrpRevoke;

/**
 * 安全组权限回收Mapper
 *
 * @author xingxing.wu@hand-china.com  2019-10-31 14:00:03
 */
public interface SecGrpRevokeMapper extends BaseMapper<SecGrpRevoke> {

    /**
     * 查询针对指定安全组下指定的权限类型，角色被屏蔽的权限
     *
     * @param secGrpId      安全组ID
     * @param shieldRoleId  被屏蔽的决角色ID
     * @param authorityType 权限类型
     * @return
     */
    List<SecGrpRevoke> selectShieldedAuthority(@Param("secGrpId") Long secGrpId,
                                               @Param("shieldRoleId") Long shieldRoleId,
                                               @Param("authorityType") String authorityType);


    void batchInsertBySql(List<SecGrpRevoke> revokes);

    void batchDeleteBySql(@Param("roleId") Long roleId,
                          @Param("secGrpId") Long secGrpId,
                          @Param("authorityIds") Set<Long> authorityIds,
                          @Param("revokeType") String revokeType,
                          @Param("authorityType") String authorityType);
}
