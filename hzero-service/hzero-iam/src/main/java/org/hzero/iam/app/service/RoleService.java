package org.hzero.iam.app.service;

import java.util.List;
import java.util.Set;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.iam.api.dto.RoleDTO;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.RolePermission;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.vo.RoleVO;


/**
 * 角色应用服务，完全覆盖IAM原有的创建角色、修改角色的功能
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/20 10:38
 */
public interface RoleService {

    /**
     * 创建角色
     *
     * @param role      角色信息
     * @param inherited 继承创建时标注为 true
     * @param duplicate 复制创建时标注为 true
     * @return Role
     */
    Role createRole(Role role, boolean inherited, boolean duplicate);

    /**
     * 为租户创建租户管理员角色
     *
     * @param user        角色创建后所属用户，不可空 若当前无用户登录，则以该userId进行模拟登录
     * @param tenant      租户信息
     * @param parentRole  父级角色
     * @param roleTplCode 租户管理员的模板代码
     * @return 返回创建的角色
     */
    Role createRoleByRoleTpl(User user, Tenant tenant, Role parentRole, String roleTplCode);

    /**
     * 更新角色信息 <br/>
     *
     * @param role 角色
     * @return Role
     */
    Role updateRole(Role role);

    /**
     * 启用角色及其下的所有子角色
     *
     * @param roleId 角色ID
     */
    void enableRole(Long roleId);

    /**
     * 禁用角色及其下的所有子角色
     *
     * @param roleId 角色ID
     */
    void disableRole(Long roleId);

    /**
     * 分配权限集至角色
     *
     * @param roleId          角色ID
     * @param permissionSetId 权限集
     */
    void assignRolePermissionSets(Long roleId, Set<Long> permissionSetId, String type);

    /**
     * 回收权限集
     *
     * @param roleId           角色ID
     * @param permissionSetIds 权限集
     */
    void recycleRolePermissionSets(Long roleId, Set<Long> permissionSetIds, String type);

    /**
     * 直接分配权限到角色上，不考虑子孙角色
     *
     * @param rolePermission 角色权限
     */
    void directAssignRolePermission(RolePermission rolePermission);

    /**
     * 查询租户管理员角色
     *
     * @param tenantId 租户ID
     * @return 租户管理员角色列表
     */
    List<Role> listTenantAdmin(Long tenantId);

    /**
     * 分页查询数据结构
     *
     * @param roleVO 查询条件
     */
    List<RoleDTO> listTreeList(RoleVO roleVO);

    /**
     * 查询当前用户自己的角色
     */
    List<Role> selectUserRole(Long organizationId, Long userId);

    /**
     * 分页查询当前用户可管理的所有(子孙)角色
     */
    Page<RoleVO> selectSelfManageableRoles(RoleVO params, PageRequest pageRequest);

    /**
     * 查询角色详情信息
     *
     * @param roleId 角色Id
     */
    RoleVO selectRoleDetails(Long roleId);

    /**
     * 查询可管理的角色，根据父级角色查询
     */
    Page<RoleVO> selectManageableRoles(RoleVO params, PageRequest pageRequest);

    /**
     * 查询成员可分配的角色
     */
    Page<RoleVO> selectMemberAssignableRoles(RoleVO params, PageRequest pageRequest);

    /**
     * 角色管理树形角色查询
     *
     * @param params      查询参数
     * @param pageRequest 分页参数
     * @return Page
     */
    Page<RoleVO> selectTreeManageableRoles(RoleVO params, PageRequest pageRequest);
}
