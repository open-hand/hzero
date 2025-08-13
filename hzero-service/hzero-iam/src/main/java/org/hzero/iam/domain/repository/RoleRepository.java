package org.hzero.iam.domain.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.choerodon.core.domain.Page;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.iam.api.dto.*;
import org.hzero.iam.domain.entity.*;
import org.hzero.iam.domain.vo.RoleVO;
import org.hzero.iam.infra.constant.HiamMemberType;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 角色资源库
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/20 11:25
 */
public interface RoleRepository extends BaseRepository<Role> {

    /**
     * <pre>
     * 查询当前用户可管理的所有角色，不区分层级，默认先查询已分配给当前用户的所有角色，然后查询其子孙角色。
     * 已分配的角色中，如果角色有分配创建角色的功能（判断角色是否有关联创建角色的权限集 <code>ps.code like '%role.ps.create'</code>），则标识为管理角色（<code>isAdminRole=true</code>）。
     * 每个角色需设置其拥有创建角色功能的上级角色，以便于在复制、继承时自动带出上级角色ID
     * </pre>
     */
    Page<RoleVO> selectSelfManageableRoles(RoleVO params, PageRequest pageRequest);

    /**
     * 查询当前用户可管理的所有角色（简要信息）
     *
     * @param params 参数
     */
    List<RoleVO> selectSelfAllManageableRoles(RoleVO params);

    /**
     * 查询指定用户可管理的所有角色（简要信息）
     *
     * @param params 参数
     * @param user   用户
     */
    List<RoleVO> selectUserManageableRoles(RoleVO params, User user);

    /**
     * 在当前登录用户管理的角色下，查询用户可分配的角色
     *
     * @param params 通过 excludeUserIds 传入当前分配用户，通过 excludeRoleIds 传入排除的角色
     */
    Page<RoleVO> selectSelfAssignableRoles(RoleVO params, PageRequest pageRequest);

    /**
     * 查询角色详细信息，不限制租户ID，本身就是跨租户的
     *
     * @param roleId 角色ID
     * @return RoleVO
     */
    RoleVO selectRoleDetails(Long roleId);

    /**
     * 查询用户的管理角色，即拥有创建角色功能的角色
     *
     * @param params      参数
     * @param pageRequest 分页
     * @return 用户的管理角色
     */
    List<RoleVO> selectUserAdminRoles(RoleVO params, PageRequest pageRequest);

    /**
     * 查询用户分配的角色，并返回角色是否是管理角色等标识
     *
     * @param params      参数
     * @param pageRequest 分页
     * @return 用户的管理角色
     */
    Page<RoleVO> selectSelfAssignedRoles(RoleVO params, PageRequest pageRequest);

    /**
     * 树形查询自己已分配的角色
     *
     * @param params 查询参数
     * @return 角色
     */
    Page<RoleVO> selectSelfAssignedRolesForTree(RoleVO params, PageRequest pageRequest);

    /**
     * 查询用户的管理角色，即拥有创建角色功能的角色
     *
     * @return 用户的管理角色
     */
    List<RoleVO> selectUserAdminRoles(RoleVO params);

    /**
     * 查询用户当前租户下的角色
     *
     * @param notMerge 不为空且等于 true 时，始终不合并角色
     */
    List<RoleVO> selectSelfCurrentTenantRoles(@Nullable Boolean notMerge);

    /**
     * 查询当前角色
     */
    RoleVO selectCurrentRole();

    /**
     * 分页查询租户角色
     *
     * @param params 参数
     */
    Page<RoleVO> selectSimpleRolesWithTenant(RoleVO params, PageRequest pageRequest);

    /**
     * 查询角色简要信息
     *
     * @param params 参数
     */
    List<Role> selectSimpleRolesWithTenant(RoleVO params);

    /**
     * 查询角色可被分配的权限菜单以及角色的权限集选择状态
     *
     * @param roleId             角色ID
     * @param permissionSetParam 权限集参数
     * @return 树形结构的权限集
     */
    List<Menu> selectRolePermissionSetTree(Long roleId, PermissionSetSearchDTO permissionSetParam);

    /**
     * 查询角色的所有父级角色
     *
     * @param roleId 角色ID
     * @return List
     */
    List<Role> selectParentRoles(Long roleId);

    /**
     * 查询角色的所有子角色及子孙角色
     *
     * @param roleId 角色ID
     * @return List
     */
    List<Role> selectAllSubRoles(Long roleId);

    /**
     * 查询内置角色，限制 is_built_in=1
     *
     * @param includeSuperAdmin 是否包含超级管理员(parentId=0)
     */
    List<Role> selectBuiltInRoles(boolean includeSuperAdmin);

    /**
     * 查询角色编码、层级
     *
     * @param roleId 角色ID
     * @return Role
     */
    Role selectRoleSimpleById(Long roleId);

    /**
     * 查询角色编码、层级，编码不唯一，可能返回多个
     *
     * @param roleCode 角色编码
     * @return Role
     */
    Role selectRoleSimpleByCode(String roleCode);

    /**
     * 根据编码查询唯一的角色
     *
     * @param roleCode 角色编码
     * @return Role
     */
    Role selectOneRoleByCode(String roleCode);

    /**
     * 查询角色编码、层级
     *
     * @param levelPath 角色唯一路径
     * @return Role
     */
    Role selectRoleSimpleByLevelPath(String levelPath);

    /**
     * 查询分配给用户的角色
     *
     * @param memberId userId/clientId
     */
    Page<RoleVO> selectMemberRoles(Long memberId, HiamMemberType memberType, MemberRoleSearchDTO memberRoleSearchDTO, PageRequest pageRequest);

    /**
     * 查询所有管理范围的角色
     */
    List<Long> selectAllManageableRoleIds(RoleVO params, CustomUserDetails self);

    /**
     * 查询分配给用户的角色
     *
     * @param memberId userId/clientId
     */
    List<RoleVO> selectMemberRoles(Long memberId, HiamMemberType memberType, MemberRoleSearchDTO memberRoleSearchDTO);

    /**
     * 查询角色的继承角色及子孙角色(继承树，向下)，且包含角色已分配的权限集<br/>
     *
     * @param inheritRoleId    继承自的角色(结果集中不包含此角色)
     * @param permissionSetIds 限定权限集范围
     * @param type             权限类型
     * @return 继承角色子树(不包含参数角色)，且包含角色可分配的权限集，角色格式化成树形结构返回
     */
    List<Role> selectInheritSubRoleTreeWithPermissionSets(Long inheritRoleId, Set<Long> permissionSetIds, String type);

    /**
     * 查询角色的子角色及子孙角色(创建树，向下)，且包含角色已分配的权限集<br/>
     *
     * @param parentRoleId     父级角色(结果集中不包含此角色)
     * @param permissionSetIds 限定权限集范围
     * @return 继承角色子树(不包含参数角色)，且包含角色可分配的权限集，角色格式化成树形结构返回
     */
    List<Role> selectCreatedSubRoleTreeWithPermissionSets(Long parentRoleId, Set<Long> permissionSetIds, String type);

    /**
     * 查询角色的父级角色
     *
     * @param roleId 角色ID
     * @return 父级角色
     */
    RoleVO selectAdminRole(Long roleId);

    List<RolePermission> selectRolePermissions(RolePermission params);

    /**
     * 查询租户管理员角色
     *
     * @param tenantId 租户ID
     * @return 租户管理员角色列表
     */
    List<Role> listTenantAdmin(Long tenantId);

    /**
     * 查询角色树
     *
     * @param params      查询条件
     * @param pageRequest 分条件
     * @return
     */
    Page<RoleDTO> selectUserManageableRoleTree(PageRequest pageRequest, RoleVO params);

    /**
     * 查询用户在租户下的角色列表
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @return 角色列表
     */
    List<Role> selectUserRole(Long tenantId, Long userId);

    /**
     * 查角色树
     *
     * @param params 查询条件
     * @return
     */
    List<RoleVO> selectUserManageableRoleTree(RoleVO params);

    /**
     * 检查权限集是否在特定范围内是否可用
     *
     * @param rolePermissionCheckDTO 角色权限集参数
     * @return 检查结果
     */
    boolean checkPermission(RolePermissionCheckDTO rolePermissionCheckDTO);

    /**
     * 查询角色的所有子集包含自己,树形返回
     *
     * @param roleId 角色ID
     * @return 角色子集
     */
    List<Role> selectAllSubRolesIncludeSelf(Long roleId);

    /**
     * 查询安全组可分配的角色
     *
     * @param secGrpId    安全组ID
     * @param roleId      当前安全组所属的角色
     * @param queryDTO    查询条件对象
     * @param pageRequest 分页对象
     * @return 查询结果
     */
    Page<RoleVO> selectSecGrpAssignableRole(Long secGrpId, Long roleId, RoleSecGrpDTO queryDTO, PageRequest pageRequest);

    /**
     * 分页查询角色信息
     *
     * @param roleIds     角色ID结合
     * @param pageRequest 分页
     * @return 角色
     */
    Page<RoleVO> selectByRoleIds(List<Long> roleIds, PageRequest pageRequest);

    /**
     * 查询内置的模板角色(三员插件有用到)
     *
     * @return 内置的模板角色
     */
    List<Role> selectBuiltInTemplateRole(String roleLabel);

    /**
     * 查询模板角色的多语言名称
     *
     * @param roleId 角色ID
     * @return 多语言名称
     */
    Map<String, String> selectTplRoleNameById(Long roleId);

    Long countSubRole(Long parentRoleId, Long roleId);

    /**
     * 批量更新角色启用禁用状态
     *
     * @param roleId        角色ID
     * @param enableFlag    启用/禁用状态
     * @param updateSubRole 是否更新子角色状态
     */
    void batchUpdateEnableFlag(Long roleId, Integer enableFlag, boolean updateSubRole);

    /**
     * 查询子孙角色中分配给自己的角色，包括当前角色
     *
     * @param roleId 角色
     * @param userId 用户
     */
    List<RoleVO> selectSubAssignedRoles(Long roleId, Long userId);

    /**
     * 根据角色标签查询角色
     *
     * @param tenantId   角色所属租户ID
     * @param roleLabels 角色标签
     */
    List<Role> selectRoleByLabel(@Nullable Long tenantId, @Nonnull Set<String> roleLabels, @Nullable String assignType);

    List<RoleVO> selectManageableRoles(RoleVO params);

    Page<RoleVO> pageManageableRoles(RoleVO params, PageRequest pageRequest);

    Page<RoleVO> pageMemberAssignableRoles(RoleVO params, PageRequest pageRequest);

    /**
     * 判断角色是否是自己已分配角色中权限最大的顶级角色
     */
    boolean isTopAdminRole(Long userId, Long roleId);

    /**
     * 查询成员已分配的角色
     *
     * @param memberId   成员ID
     * @param memberType 成员类型
     * @return 已分配的角色
     */
    List<RoleVO> selectAssignedRoles(Long memberId, String memberType);

    /**
     * 查询下一级角色
     */
    Page<RoleVO> selectNextSubRoles(RoleVO params, PageRequest pageRequest);

    /**
     * 查询成员不可删除分配的角色
     *
     * @param memberIdList 成员ID
     * @param self         当前用户
     * @return 返回不可删除分配的成员ID和角色ID
     */
    List<MemberRole> selectCantRemoveAssignedRoles(List<Long> memberIdList, CustomUserDetails self);
}
