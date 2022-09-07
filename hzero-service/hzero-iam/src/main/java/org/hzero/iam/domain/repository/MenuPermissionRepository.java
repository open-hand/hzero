package org.hzero.iam.domain.repository;

import java.util.List;

import org.hzero.iam.domain.entity.MenuPermission;
import org.hzero.mybatis.base.BaseRepository;

/**
 * @author allen 2018/7/5
 */
public interface MenuPermissionRepository extends BaseRepository<MenuPermission> {

    /**
     * 删除权限集权限
     */
    int deleteByIdAndCodes(Long menuId, List<String> permissionCodes);

    /**
     * 删除菜单下所有的集权限
     *
     * @param menuId 菜单Id
     */
    void deleteByMenuId(Long menuId);

    /**
     * 删除服务下，不存在的权限编码
     *
     * @param serviceName           服务名
     * @param deprecatedPermissions 过期的权限编码
     */
    int deleteByPermissionCodes(String serviceName, List<String> deprecatedPermissions);

    /**
     * 查询菜单-权限关联数据
     */
    List<MenuPermission> selectAllMenuPermissions();

    /**
     * 删除菜单-权限关联数据
     */
    void deleteAllMenuPermissions();

    void batchInsertBySql(List<MenuPermission> list);
}
