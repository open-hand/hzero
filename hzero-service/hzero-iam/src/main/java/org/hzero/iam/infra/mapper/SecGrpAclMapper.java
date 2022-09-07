package org.hzero.iam.infra.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;

import org.hzero.iam.domain.entity.SecGrpAcl;

/**
 * 安全组访问权限Mapper
 *
 * @author xingxing.wu@hand-china.com 2019-10-17 11:26:19
 */
public interface SecGrpAclMapper extends BaseMapper<SecGrpAcl> {

    /**
     * 查询安全组下所有的可用的访问权限（过滤掉被回收的）
     */
    List<SecGrpAcl> selectAclInGrp(@Param("secGrpId") Long secGrpId);

    /**
     * 查询角色所有可使用的安全组访问权限
     *
     * @param roleId 查询角色ID
     */
    List<SecGrpAcl> selectAclInRole(@Param("roleId") Long roleId);

    /**
     * 查询角色能从指定安全组中获取到的有效访问权限
     *
     * @param roleId    角色ID
     * @param secGrpIds 安全组ID列表
     */
    List<SecGrpAcl> selectRoleSecGrpAcls(@Param("roleId") Long roleId, @Param("secGrpIds") List<Long> secGrpIds);

    /**
     * 查询角色能从指定安全组中获取到的有效访问权限
     *
     * @param roleId          角色ID
     * @param excludeSecGrpId 排除的安全组ID
     * @param permissionIds   权限ID
     */
    List<SecGrpAcl> selectRoleOtherSecGrpAcls(@Param("roleId") Long roleId,
                                              @Param("excludeSecGrpId") Long excludeSecGrpId,
                                              @Param("permissionIds") Set<Long> permissionIds);

    /**
     * 查询指定角色及其子角色自建的且绑定了指定权限集的安全组访问权限
     *
     * @param roleId            角色ID
     * @param permissionIds     权限集ID
     * @param includeRevokeFlag 是否包含被回收的
     * @return 安全组访问权限列表
     */
    List<SecGrpAcl> selectSecGrpAclBindPermissionIdInRoleAndSubRole(@Param("roleId") Long roleId,
                                                                    @Param("permissionIds") List<Long> permissionIds,
                                                                    @Param("includeRevokeFlag") Integer includeRevokeFlag);

    /**
     * 查询指定角色自建的且绑定了指定权限集的安全组访问权限
     *
     * @param roleId            角色ID
     * @param permissionIds     权限集ID
     * @param includeRevokeFlag 是否包含被回收的
     * @return 安全组访问权限列表
     */
    List<SecGrpAcl> selectSecGrpAclBindPermissionIdInRole(@Param("roleId") Long roleId,
                                                          @Param("permissionIds") List<Long> permissionIds,
                                                          @Param("includeRevokeFlag") Integer includeRevokeFlag);

    void batchInsertBySql(List<SecGrpAcl> acls);

    void batchDeleteBySql(@Param("secGrpId") Long secGrpId, @Param("permissionIds") Set<Long> permissionIds);

    List<SecGrpAcl> selectRoleCreatedSecGrpAcl(@Param("roleId")Long roleId, @Param("permissionIds") Set<Long> permissionIds);
}
