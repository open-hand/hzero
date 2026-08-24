package org.hzero.iam.app.service;

import org.hzero.iam.api.dto.MenuCopyDataDTO;
import org.hzero.iam.api.dto.MenuSearchDTO;
import org.hzero.iam.api.dto.MenuSiteExportDTO;
import org.hzero.iam.domain.entity.Menu;
import org.hzero.iam.infra.constant.HiamResourceLevel;
import org.hzero.iam.infra.constant.PermissionType;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author bojiangzhou 2019/01/18
 * @author allen 2018/7/2
 */
public interface MenuService {

    /**
     * 平台层：创建目录、菜单，平台层可创建平台级和租户级菜单，且为标准菜单
     *
     * @param menu 菜单
     */
    Menu createMenuInSite(Menu menu);

    /**
     * 租户层：创建目录、菜单，租户层只能创建客制化菜单，且只能为租户级菜单
     *
     * @param tenantId 租户ID
     * @param menu     菜单
     */
    Menu createMenuInTenant(Long tenantId, Menu menu);

    /**
     * 检查菜单目录代码是否重复
     *
     * @param menu 菜单
     */
    void checkDuplicate(Menu menu);

    /**
     * 更新菜单
     *
     * @param menu 菜单
     */
    Menu update(Menu menu);

    /**
     * 更新菜单
     *
     * @param menu 菜单
     */
    Menu updateCustomMenu(Long tenantId, Menu menu);

    /**
     * 删除菜单
     *
     * @param tenantId 租户ID
     * @param menuId   菜单ID
     */
    void deleteById(Long tenantId, Long menuId);

    /**
     * 启用菜单
     *
     * @param tenantId 租户ID
     * @param menuId   菜单ID
     */
    void enableMenu(Long tenantId, Long menuId);

    /**
     * 启用菜单
     *
     * @param tenantId 租户ID
     * @param menuId   菜单ID
     */
    void enableCustomMenu(Long tenantId, Long menuId);

    /**
     * 禁用菜单
     *
     * @param tenantId 租户ID
     * @param menuId   菜单ID
     */
    void disableMenu(Long tenantId, Long menuId);

    /**
     * 禁用菜单
     *
     * @param tenantId 租户ID
     * @param menuId   菜单ID
     */
    void disableCustomMenu(Long tenantId, Long menuId);

    /**
     * 为权限集分配权限
     *
     * @param permissionSetId 权限集ID
     * @param permissionType  权限类型:permission/lov
     * @param permissionCodes 权限编码，包括Lov编码
     */
    void assignPsPermissions(Long permissionSetId, PermissionType permissionType, String[] permissionCodes);

    /**
     * 为权限集分配权限
     *
     * @param tenantId        租户ID
     * @param code            权限集编码
     * @param level           权限集层级
     * @param permissionType  权限类型:permission/lov
     * @param permissionCodes 权限编码，包括Lov编码
     */
    void assignPsPermissions(Long tenantId, String code, String level, PermissionType permissionType, String[] permissionCodes);

    /**
     * 回收权限集的权限
     *
     * @param permissionSetId 权限集ID
     * @param permissionCodes 权限编码，包含Lov
     * @param permissionType  权限类型
     */
    void recyclePsPermissions(Long permissionSetId, String[] permissionCodes, PermissionType permissionType);

    /**
     * 回收权限集的权限
     *
     * @param tenantId        租户ID
     * @param code            权限集编码
     * @param level           权限集层级
     * @param permissionCodes 权限编码，包含Lov
     * @param permissionType  权限类型
     */
    void recyclePsPermissions(Long tenantId, String code, String level, String[] permissionCodes, PermissionType permissionType);

    /**
     * 修复菜单数据
     */
    Map<String, Object> fixMenuData(boolean initAll);

    /**
     * 处理客户化菜单导出的数据
     *
     * @param tenantId     租户ID
     * @param menuTreeList 待导出的树形结构数据
     * @param response     HttpServletResponse对象
     * @throws IOException 文件处理异常
     */
    void handleCustomMenuExportData(Long tenantId, List<Menu> menuTreeList, HttpServletResponse response) throws IOException;


    /**
     * 处理客户化菜单导入的数据
     *
     * @param tenantId       租户ID
     * @param customMenuFile 文件对
     * @return
     * @throws IOException 文件处理异常
     */
    List<Menu> handleCustomMenuImportData(Long tenantId, MultipartFile customMenuFile) throws IOException;

    /**
     * 执行菜单导入
     *
     * @param tenantId     租户ID
     * @param menuTreeList 处理好的菜单对象
     */
    void importMenuTree(Long tenantId, List<Menu> menuTreeList);

    /**
     * 插入复制的菜单
     *
     * @param queryLevel   操作层级，平台层可以查询租户层和平台层的标准菜单 租户层只能查询租户层的客户化菜单
     * @param level        菜单层级
     * @param menuCopyData 复制相关数据
     */
    void insertMenuForCopy(HiamResourceLevel queryLevel, HiamResourceLevel level, MenuCopyDataDTO menuCopyData);

    /**
     * 新建客制化菜单
     *
     * @param tenantId 租户Id
     * @param menu     菜单
     */
    void insertCustomMenu(Long tenantId, Menu menu);

    /**
     * 平台层导出菜单数据
     *
     * @param menuSearchDTO 菜单查询条件
     * @return 导出数据
     */
    List<MenuSiteExportDTO> exportSiteMenuData(MenuSearchDTO menuSearchDTO);

    /**
     * 查询所有菜单
     *
     * @param menu 查询条件对象
     * @return 所有菜单的信息 key -> value === menuId -> menuName
     */
    Map<Long, String> queryMenus(Menu menu);
}
