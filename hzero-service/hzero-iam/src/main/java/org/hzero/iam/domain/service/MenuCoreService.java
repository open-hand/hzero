package org.hzero.iam.domain.service;

import org.hzero.iam.domain.entity.Menu;
import org.hzero.iam.infra.constant.HiamMenuType;
import org.hzero.iam.infra.constant.PermissionType;

/**
 * 菜单核心业务。菜单有多种类型，参考 {@link HiamMenuType}。实际上与页面相关的功能属于菜单类型，权限集也是菜单的一种类型。<br/>
 * 菜单和权限之间的关系：菜单-权限集、权限集-权限、角色-权限集(角色-权限)
 *
 * @author bojiangzhou 2019/01/17
 * @see HiamMenuType
 */
public interface MenuCoreService {

    /**
     * 创建菜单
     *
     * @param menu 菜单
     */
    Menu createMenu(Menu menu, boolean createDefaultPs);

    /**
     * 检查菜单是否已存在
     *
     * @param menu 菜单
     */
    void checkMenuExists(Menu menu);

    /**
     * 更新菜单
     *
     * @param menu 菜单
     */
    Menu updateMenu(Menu menu);

    /**
     * 根据菜单ID删除菜单
     *
     * @param tenantId 租户ID
     * @param menuId   菜单ID
     */
    void deleteMenuById(Long tenantId, Long menuId);

    /**
     * 启用或禁用菜单
     *
     * @param tenantId        租户ID
     * @param menuId          菜单ID
     * @param enableOrDisable 启用/禁用标识
     */
    void changeEnableFlag(Long tenantId, Long menuId, Integer enableOrDisable);

    /**
     * 为权限集分配权限
     *
     * @param permissionSetId 权限集ID
     * @param permissionType  权限类型
     * @param permissionCodes 权限编码
     */
    void assignPsPermissions(Long permissionSetId, PermissionType permissionType, String[] permissionCodes);

    /**
     * 回收权限集的权限
     *
     * @param permissionSetId 权限集ID
     * @param permissionCodes 权限ID
     * @param permissionType  权限类型
     */
    void recyclePsPermissions(Long permissionSetId, String[] permissionCodes, PermissionType permissionType);

    /**
     * 初始化菜单路径
     *
     * @param type 菜单类型
     * @return 更细条数
     */
    int initLevelPath(HiamMenuType type, boolean initAll);

}
