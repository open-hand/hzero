package org.hzero.iam.domain.repository;

import java.util.List;
import java.util.function.Function;
import javax.annotation.Nonnull;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.iam.api.dto.*;
import org.hzero.iam.domain.entity.Menu;
import org.hzero.iam.domain.entity.Permission;
import org.hzero.iam.domain.vo.Lov;
import org.hzero.iam.infra.constant.HiamResourceLevel;
import org.hzero.mybatis.base.BaseRepository;

/**
 * @author bojiangzhou
 * @author allen 2018/6/29
 */
public interface MenuRepository extends BaseRepository<Menu> {

    /**
     * 平台层：查询树形菜单，可查询所有标准菜单
     *
     * @param menuParams 查询参数
     * @return 返回树形结构的菜单列表
     */
    List<Menu> selectMenuTreeInSite(MenuSearchDTO menuParams);

    /**
     * 租户层：查询树形菜单，仅查询租户客制化的菜单
     *
     * @param menuParams 查询参数
     * @return 返回树形结构的菜单列表
     */
    List<Menu> selectMenuTreeInTenant(MenuSearchDTO menuParams);

    /**
     * 查询角色的菜单栏
     * <p>
     * <lu>
     * <li>平台管理员/租户管理员角色能够查看所有菜单树</li>
     * <li>其他角色只能查看其分配了菜单权限集/权限明细的菜单树</li>
     * </lu>
     *
     * @param menuTreeQueryDTO 菜单栏查询条件对象
     * @return 经过权限检测过后的菜单树
     */
    List<Menu> selectRoleMenuTree(MenuTreeQueryDTO menuTreeQueryDTO);

    /**
     * 平台层：查询菜单目录
     */
    Page<Menu> selectMenuDirsInSite(MenuSearchDTO menuParams, PageRequest pageRequest);

    /**
     * 租户层：查询租户菜单目录，限制租户层级，可查询出租户客制化的目录和标准的租户层目录
     */
    Page<Menu> selectMenuDirsInTenant(MenuSearchDTO menuParams, PageRequest pageRequest);

    /**
     * 查询菜单关联的权限集
     */
    List<Menu> selectMenuPermissionSet(Long tenantId, Long menuId, PermissionSetSearchDTO permissionSetParam);

    /**
     * 查询菜单关联的权限集
     */
    Menu queryMenu(Long tenantId, String code, String level);

    /**
     * 查询权限集下的权限
     *
     * @param permissionSetParam 查询参数
     */
    Page<Permission> selectPermissionSetPermissions(PermissionSetSearchDTO permissionSetParam, PageRequest pageRequest);

    /**
     * 查询权限集下的权限
     *
     * @param permissionSetParam 查询参数
     */
    Page<Lov> selectPermissionSetLovs(PermissionSetSearchDTO permissionSetParam, PageRequest pageRequest);

    /**
     * 查询某个权限集可分配的权限，排除自身权限
     *
     * @param permissionSetParam 查询参数
     */
    Page<Permission> selectAssignablePermissions(PermissionSetSearchDTO permissionSetParam, PageRequest pageRequest);

    /**
     * 查询某个权限集可分配的LOV
     *
     * @param permissionSetParam 查询参数
     */
    Page<Lov> selectAssignableLovs(PermissionSetSearchDTO permissionSetParam, PageRequest pageRequest);

    /**
     * 查询角色的权限集，并标识权限集的勾选状态
     *
     * @param currentRoleId      当前用户角色ID
     * @param allocateRoleId     待分配角色ID
     * @param permissionSetParam 查询参数
     * @return 树形结构的权限集
     */
    List<Menu> selectRolePermissionSet(Long currentRoleId, Long allocateRoleId, PermissionSetSearchDTO permissionSetParam);

    /**
     * 查询当前用户菜单下可访问的权限集编码列表
     *
     * @param tenantId 租户ID
     * @param code     菜单编码
     * @return 可访问的权限集编码
     */
    List<String> selectAccessiblePermissionSets(Long tenantId, String code);

    /**
     * 查询当前用户菜单下可访问的权限集编码列表
     *
     * @param codes 权限集编码
     * @return 可访问的权限集编码
     */
    List<PermissionCheckDTO> checkPermissionSets(List<String> codes);

    /**
     * 查询当前用户菜单下可访问的权限集编码列表
     *
     * @param codes 权限集编码
     * @param checkSupplier 检查权限，返回检查结果
     * @return 可访问的权限集编码
     */
    List<PermissionCheckDTO> checkPermissionSets(List<String> codes, Function<List<String>, List<PermissionCheckDTO>> checkSupplier);

    /**
     * 租户层：查询客户化菜单详情信息，树形结构返回
     *
     * @param tenantId 租户ID
     * @return 返回树形结构的菜单列表
     */
    List<Menu> selectMenuTreeForExport(Long tenantId);

    /**
     * 通过唯一性索引查询菜单
     *
     * @param tenantId
     * @param code
     * @param level
     * @return
     */
    Menu selectMenuUnique(Long tenantId, String code, String level);

    /**
     * 查询可复制的查单树
     *
     * @param tenantId   租户ID
     * @param queryLevel 查询层级
     * @param level      菜单层级
     * @return 菜单详情树形
     */
    List<Menu> selectMenuTreeForCopy(Long tenantId, HiamResourceLevel queryLevel, HiamResourceLevel level, Long menuId);

    /**
     * 查询菜单已分配的角色列表
     *
     * @param menuId      菜单id
     * @param tenantId    租户id
     * @param menu        其他查询条件
     * @param pageRequest 分页条件
     * @return 返回已分配的角色列表
     */
    Page<AccessAuthDTO> pageMenuAssignRole(Long tenantId, Long menuId, Menu menu, PageRequest pageRequest);

    /**
     * 查询菜单树
     *
     * @param tenantId 租户id
     * @param menuId   菜单id
     * @return 返回菜单列表
     */
    List<Menu> queryMenuTree(Long tenantId, Long menuId);

    /**
     * 查询租户下客户化的菜单
     *
     * @param tenantId   租户ID
     * @param menuParams 查询参数
     * @return 租户客户化菜单
     */
    List<Menu> selectTenantCustomMenuTree(@Nonnull Long tenantId, MenuSearchDTO menuParams);

    /**
     * 查询菜单详情信息 包含菜单权限以及多语言数据 用于菜单的额复制和菜单的导出数据
     *
     * @param queryLevel 查询层级
     * @param level      菜单层级
     * @param menuIds    菜单ID
     * @return
     */
    List<Menu> selectMenuDetail(Long tenantId, HiamResourceLevel queryLevel, HiamResourceLevel level, List<Long> menuIds);

    /**
     * 分页查询可以分配给租户的权限
     *
     * @param permissionSetSearchDTO 查询参数
     * @return 权限集
     */
    Page<Permission> selectTenantAssignablePermissions(PermissionSetSearchDTO permissionSetSearchDTO, PageRequest pageRequest);

    /**
     * 分页查询权限集
     *
     * @return 权限集
     */
    Page<Menu> selectPermissionSets(PermissionSetSearchDTO permissionSetSearchDTO, PageRequest pageRequest);

    /**
     * 查询权限集下的所有权限
     *
     * @param permissionSetParam 权限集参数
     */
    List<Permission> selectPermissionSetPermissions(Long tenantId, Long menuId, PermissionSetSearchDTO permissionSetParam);

    /**
     * 平台层导出菜单数据
     *
     * @param menuSearchDTO 菜单查询条件
     * @return 导出数据
     */
    List<MenuSiteExportDTO> exportSiteMenuData(MenuSearchDTO menuSearchDTO);
}
