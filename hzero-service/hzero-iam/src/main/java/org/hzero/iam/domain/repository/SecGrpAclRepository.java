package org.hzero.iam.domain.repository;

import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.hzero.iam.api.dto.SecGrpPermissionSearchDTO;
import org.hzero.iam.domain.entity.Menu;
import org.hzero.iam.domain.entity.SecGrpAcl;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 安全组访问权限资源库
 *
 * @author bojiangzhou 2020/02/17
 * @author xingxing.wu@hand-china.com 2019-10-17 11:26:19
 */
public interface SecGrpAclRepository extends BaseRepository<SecGrpAcl> {

    /**
     * 查询安全组可分配的权限集
     *
     * @param tenantId 租户ID
     * @param secGrpId 安全组ID
     * @return 返回权限集树形结构
     */
    List<Menu> listSecGrpAssignableAcl(@Nullable Long tenantId, @NotNull Long secGrpId);

    /**
     * 查询安全组已分配的权限集
     *
     * @param tenantId 租户ID
     * @param secGrpId 安全组ID
     * @return 返回权限集树形结构
     */
    List<Menu> listSecGrpAssignedAcl(@Nullable Long tenantId, @NotNull Long secGrpId, SecGrpPermissionSearchDTO queryDTO);

    /**
     * 查询分配给指定角色的安全组访问权限权限集（包含权限集的上层菜单），并标志屏蔽状态
     *
     * @param roleId   被非分配的角色ID
     * @param secGrpId 安全组ID
     * @return 权限集及权限集上层菜单的树形结构
     */
    List<Menu> listRoleSecGrpAcl(Long roleId, Long secGrpId);

    /**
     * 查询安全组下所有的可用的访问权限（过滤掉被回收的）
     */
    List<SecGrpAcl> selectAclInGrp(Long secGrpId);

    /**
     * 查询非admin角色所有可使用的安全组访问权限--角色被分配的安全组包含的访问权限，排除被屏蔽的，以及原安全组被回收的
     *
     * @param roleId 查询角色ID
     */
    List<SecGrpAcl> selectAclInRole(Long roleId);

    /**
     * 查询角色能从指定安全组中获取到的有效访问权限
     *
     * @param roleId    角色ID
     * @param secGrpIds 安全组ID列表
     */
    List<SecGrpAcl> selectRoleSecGrpAcls(Long roleId, List<Long> secGrpIds);

    /**
     * 查询指定角色及其子角色自建的且绑定了指定权限集的安全组访问权限
     *
     * @param roleId            角色ID
     * @param permissionIds     权限集ID
     * @param includeRevokeFlag 是否包含被回收的
     * @return 安全组访问权限列表
     */
    List<SecGrpAcl> selectSecGrpAclBindPermissionIdInRoleAndSubRole(Long roleId, List<Long> permissionIds, Integer includeRevokeFlag);

    /**
     * 查询指定角色自建的且绑定了指定权限集的安全组访问权限(包含被回收的)
     *
     * @param roleId            角色ID
     * @param permissionIds     权限集ID
     * @param includeRevokeFlag 是否包含被回收的
     * @return 安全组访问权限列表
     */
    List<SecGrpAcl> selectSecGrpAclBindPermissionIdInRole(Long roleId, List<Long> permissionIds, Integer includeRevokeFlag);

    /**
     * 通过SQL批量插入
     */
    void batchAdd(List<SecGrpAcl> acls);

    /**
     * 通过SQL批量删除
     *
     * @param secGrpId      安全组ID
     * @param permissionIds 权限ID
     */
    void batchRemove(Long secGrpId, Set<Long> permissionIds);

    /**
     * 查询角色的其它安全组(分配的安全组)不包含的权限
     *
     * @param roleId          角色ID
     * @param excludeSecGrpId 排除的安全组
     * @param permissionIds   权限ID
     */
    Set<Long> listRoleNotIncludedAcl(Long roleId, Long excludeSecGrpId, Set<Long> permissionIds);

    /**
     * 查询角色创建的安全组中需回收权限的安全组及权限
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID
     */
    List<SecGrpAcl> listRoleCreatedSecGrpAcl(Long roleId, Set<Long> permissionIds);
}
