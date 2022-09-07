package org.hzero.iam.infra.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.api.dto.RolePermissionCheckDTO;
import org.hzero.iam.api.dto.RolePermissionWithDTO;
import org.hzero.iam.domain.entity.RolePermission;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 角色权限(集)
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/21 20:39
 */
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    /**
     * 查询角色权限集
     */
    List<RolePermission> selectRolePermissionSets(RolePermission params);

    /**
     * 检查权限集是否在特定范围内是否可用
     *
     * @param rolePermissionCheckDTO 角色权限集参数
     * @return 检查结果
     */
    Integer checkPermission(RolePermissionCheckDTO rolePermissionCheckDTO);

    /**
     * 查询角色信息，并且判断角色是否有查询的权限
     *
     * @param roleIds         角色ID列表
     * @param permissionSetId 权限集ID
     * @param menuType        菜单类型
     * @return 角色信息，并且判断角色是否有查询的权限
     */
    List<RolePermissionWithDTO> selectRoleWithPermission(@Param("roleIds") List<Long> roleIds,
                                                         @Param("permissionSetId") long permissionSetId,
                                                         @Param("menuType") String menuType);

    void batchInsertBySql(List<RolePermission> permissionSets);

    void batchDeleteBySql(@Param("rolePermissionIds") Set<Long> rolePermissionIds);

    void batchUpdateBySql(@Param("rolePermissionIds") Set<Long> rolePermissionIds,
                          @Param("createFlag") String createFlag,
                          @Param("inheritFlag") String inheritFlag);

    /**
     *  查询传入菜单下包含的权限集分配到角色的数据
     *
     * @param menuId 菜单Id
     * @param tenantId 租户Id
     * @return 查询结果
     */
    List<RolePermission> selectRoleAssigns(@Param("menuId") Long menuId, @Param("tenantId") Long tenantId);

    /**
     * 查询传入菜单下包含的权限集分配到角色的数据
     *
     * @param menuId 菜单Id
     * @param tenantId 租户Id
     * @param tplCode 租户管理员模板编码
     * @return 查询结果
     */
    List<RolePermission> selectTenantCustomRoleAssign(@Param("menuId") Long menuId, @Param("tenantId") Long tenantId, @Param("tplCode") String tplCode);
}
