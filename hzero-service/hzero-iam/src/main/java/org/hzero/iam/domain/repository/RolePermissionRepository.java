package org.hzero.iam.domain.repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.hzero.iam.api.dto.RolePermissionWithDTO;
import org.hzero.iam.domain.entity.Menu;
import org.hzero.iam.domain.entity.RolePermission;
import org.hzero.iam.infra.constant.Operation;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 角色权限(集)关系资源库
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/20 16:08
 */
public interface RolePermissionRepository extends BaseRepository<RolePermission> {

    /**
     * 查询角色可分配的权限集
     *
     * @param params 查询参数
     * @return List of RolePermission
     */
    List<RolePermission> selectRolePermissionSets(RolePermission params);

    /**
     * 查询角色信息，并且判断角色是否有查询的权限
     *
     * @param roleIds         角色ID列表
     * @param permissionSetId 权限集ID
     * @param menuType        菜单类型
     * @return 角色信息，并且判断角色是否有查询的权限
     */
    List<RolePermissionWithDTO> selectRoleWithPermission(List<Long> roleIds, long permissionSetId, String menuType);

    /**
     * 批量插入角色权限
     *
     * @param permissionSets 权限集
     */
    void batchInsertBySql(List<RolePermission> permissionSets);

    /**
     * 批量删除角色权限
     *
     * @param permissionSets 权限集
     */
    void batchDeleteBySql(List<RolePermission> permissionSets);

    /**
     * 批量更新角色权限
     *
     * @param permissionSets 权限集
     */
    void batchUpdateBySql(List<RolePermission> permissionSets);

    /**
     * 批量保存角色权限，通过 {@link RolePermission#getOperation()} 判断操作的类型
     *
     * @param stream 数据流
     * @return 按 Operation 分组后的数据
     */
    Map<Operation, List<RolePermission>> batchSaveRolePermission(Stream<RolePermission> stream);

    /**
     * 判断是否存在角色分配了传入菜单下面包含的权限集信息
     *
     * @param menu 菜单参数
     * @param tenantId 租户Id
     * @return List<RolePermission>
     */
    List<RolePermission> getRoleAssignPermissionSet(Menu menu, Long tenantId);
}
