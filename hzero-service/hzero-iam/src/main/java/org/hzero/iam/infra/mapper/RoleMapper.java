package org.hzero.iam.infra.mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;

import org.hzero.iam.api.dto.RoleSecGrpDTO;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.vo.RoleVO;

/**
 * 角色Mapper
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/20 11:31
 */
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 查询角色的所有父集
     *
     * @param roleId 角色ID
     * @return 角色父集
     */
    List<Role> selectAllParentRoles(@Param("roleId") Long roleId);

    /**
     * 查询角色的所有子集
     *
     * @param roleId 角色ID
     * @return 角色子集
     */
    List<Role> selectAllSubRoles(@Param("roleId") Long roleId);

    /**
     * 查询角色详细信息
     */
    RoleVO selectRoleDetails(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查询分配的角色
     *
     * @param params 参数
     * @return role list
     */
    List<RoleVO> selectMemberRoles(RoleVO params);

    /**
     * 查询 Root 用户的当前租户管理员角色
     *
     * @param params 参数
     * @return role list
     */
    List<RoleVO> selectRootMemberRoles(RoleVO params);

    /**
     * 查询角色的继承角色及子孙角色(继承树，向下)，且包含角色已分配的权限集<br/>
     *
     * @param inheritRoleId    继承自的角色(结果集中不包含此角色)
     * @param permissionSetIds 限定权限集范围
     * @param permissionType   权限类型
     * @return 继承角色子树(不包含参数角色)，且包含角色可分配的权限集
     */
    List<Role> selectInheritSubRolesWithPermissionSets(@Param("inheritRoleId") Long inheritRoleId,
                                                       @Param("permissionSetIds") Set<Long> permissionSetIds,
                                                       @Param("permissionType") String permissionType);

    /**
     * 查询角色的子角色及子孙角色(创建树，向下)，且包含角色已分配的权限集<br/>
     *
     * @param parentRoleId     父级角色(结果集中不包含此角色)
     * @param permissionSetIds 限定权限集范围
     * @param permissionType   权限类型
     * @return 父级角色子树(不包含参数角色)，且包含角色可分配的权限集
     */
    List<Role> selectCreatedSubRolesWithPermissionSets(@Param("parentRoleId") Long parentRoleId,
                                                       @Param("permissionSetIds") Set<Long> permissionSetIds,
                                                       @Param("permissionType") String permissionType);

    /**
     * 查询角色是否属于用户
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 1
     */
    int countUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 查询用户的管理角色
     *
     * @param params userId 必输
     */
    List<RoleVO> selectUserAdminRoles(RoleVO params);

    /**
     * 查询用户已分配的角色
     *
     * @param params 查询参数
     */
    List<RoleVO> selectUserAssignedRoles(RoleVO params);

    /**
     * 查询用户管理的角色，包含分配的角色及子孙角色
     *
     * @param params 查询参数， userId 必输
     * @return RoleVO
     */
    List<RoleVO> selectUserManageableRoles(RoleVO params);

    /**
     * 查询用户某个管理型角色下 的角色
     */
    List<RoleVO> selectUserManageableRolesOfAdminRole(RoleVO params);

    /**
     * 查询Root用户可分配的角色
     */
    List<RoleVO> selectRootUserAssignableRoles(RoleVO params);

    /**
     * 判断是否是管理型角色
     */
    RoleVO querySelfAdminRole(@Param("roleId") Long roleId, @Param("userId") Long userId, @Param("rootUser") boolean rootUser);

    /**
     * 根据参数查角色简要信息
     *
     * @param params 参数
     */
    List<Role> selectSimpleRoles(RoleVO params);

    /**
     * 查询父级管理角色
     *
     * @param params 参数
     */
    RoleVO selectAdminRole(RoleVO params);

    /**
     * 查询租户管理员角色
     *
     * @param tenantId 租户ID
     * @return 租户管理员角色列表
     */
    List<Role> selectTenantAdmin(@Param("tenantId") Long tenantId);

    /**
     * 查询树形
     *
     * @param params 查询参数
     */
    List<RoleVO> selectUserManageableRoleTree(RoleVO params);

    /**
     * 角色管理某个角色子角色数据查询
     *
     * @param roleId    角色id
     * @param userId    用户id
     * @param levelPath 路径
     */
    int queryUserManageableSonRoleList(@Param("roleId") Long roleId,
                                       @Param("userId") Long userId,
                                       @Param("levelPath") String levelPath);

    /**
     * 查询用户在租户下的角色列表
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @return 角色列表
     */
    List<Role> selectUserRole(@Param("tenantId") Long tenantId, @Param("userId") Long userId);

    /**
     * 查询角色的所有子集包含自己
     *
     * @param roleId 角色ID
     * @return 角色子集
     */
    List<Role> selectAllSubRolesIncludeSelf(@Param("roleId") Long roleId);

    /**
     * 查询直接分配了指定安全组的角色列表
     *
     * @param secGrpId 安全组ID
     */
    List<Role> selectDirectAssignedRolesOfSecGrp(@Param("secGrpId") Long secGrpId);

    /**
     * 查询安全组可分配的角色分列表（排除已经分配的角色）
     *
     * @param secGrpId 分配的安全组ID
     * @param roleId   当前安全组所属的角色
     * @param queryDTO 角色查询参数
     * @return 角色列表
     */
    List<RoleVO> selectSecGrpAssignableRole(@Param("secGrpId") Long secGrpId,
                                            @Param("roleId") Long roleId,
                                            @Param("query") RoleSecGrpDTO queryDTO);

    /**
     * 查询继承体系的子孙角色
     *
     * @param roleId 继承角色ID
     */
    List<Role> selectAllInheritedRole(@Param("roleId") Long roleId);

    /**
     * 查询创建体系的子孙角色
     *
     * @param roleId 父级角色ID
     */
    List<Role> selectAllCreatedRole(@Param("roleId") Long roleId);

    /**
     * 根据id查询角色
     *
     * @param roleIds 角色ID集合
     * @return 角色列表
     */
    List<RoleVO> selectByRoleIds(@Param("roleIds") List<Long> roleIds);

    /**
     * 查询全局模板角色
     *
     * @return 内置的租户层模板角色
     */
    List<Role> selectBuiltInTemplateRole(@Param("roleLabel") String roleLabel);

    List<Map<String, String>> selectTplRoleNameById(@Param("roleId") Long roleId);

    Long countSubRole(@Param("parentRoleId") Long parentRoleId, @Param("roleId") Long roleId);

    void batchUpdateEnableFlagBySql(@Param("roleId") Long roleId,
                                    @Param("enableFlag") Integer enableFlag,
                                    @Param("updateSubRole") boolean updateSubRole);

    List<RoleVO> selectSubAssignedRoles(@Param("roleId") Long roleId, @Param("userId") Long userId);

    List<Role> selectRoleByLabel(@Param("tenantId") Long tenantId, @Param("roleLabels") Set<String> roleLabels, @Param("assignType") String assignType);

    List<RoleVO> selectMemberAssignableRoles(RoleVO params);

    List<Long> selectAllManageRoleIds(RoleVO params);

    /**
     * 查询成员已分配的所有角色
     *
     * @param memberId   成员ID
     * @param memberType 成员类型
     * @return 已分配的角色
     */
    List<RoleVO> selectAssignedRoles(@Param("memberId") Long memberId, @Param("memberType") String memberType);

    /**
     * 树形查询用户已分配的角色，返回子孙角色的数量
     *
     * @param params 查询参数
     */
    List<RoleVO> selectUserAssignedRolesForTree(RoleVO params);

    /**
     * 根据父级角色查询下一级子角色
     *
     * @param params 查询参数，parentRoleId 必输
     * @return 下一级角色
     */
    List<RoleVO> selectNextSubRoles(RoleVO params);

    /**
     * 查询子角色，返回父级的直接子角色，或间接子角色
     *
     * @param params 必须包含 parentRoleId
     */
    List<RoleVO> selectSubRolesForTree(RoleVO params);

    /**
     * 查询用户不可删除分配的角色
     */
    List<MemberRole> selectMemberCantRemoveRoles(RoleVO params);
}
