package org.hzero.iam.infra.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;

import org.hzero.iam.api.dto.*;
import org.hzero.iam.domain.entity.Menu;
import org.hzero.iam.domain.entity.Permission;
import org.hzero.iam.domain.entity.RolePermission;
import org.hzero.iam.domain.vo.Lov;
import org.hzero.iam.domain.vo.RolePermissionVO;

/**
 * @author allen 2018/6/29
 */
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 查询树形菜单列表，返回列表需通过工具组装成树形结构
     * <p>
     * <ul>
     * 主要查询参数：
     * <li>tenantId 客户化菜单租户</li>
     * <li>scope 范围, STANDARD/CUSTOM/BOTH, 仅查询标准菜单/仅查询租户客户化菜单/二者同时查询</li>
     * <li>level 层级, 例如,全局、租户、项目等</li>
     * <li>type 类型, 例如,普通菜单、文件夹、根菜单等</li>
     * </ul>
     *
     * @param params 查询参数
     */
    List<Menu> selectMenusByCondition(MenuSearchDTO params);

    /**
     * 全量查询指定层级下的菜单数据，包含菜单多语言信息，权限集信息
     *
     * @param params 查询参数
     */
    List<Menu> selectMenusDetail(MenuSearchDTO params);

    /**
     * 查询菜单及其子角色菜单
     *
     * @param params 查询参数
     */
    List<Menu> selectSubMenus(MenuSearchDTO params);


    /**
     * 根据角色查询菜单列表，用于左侧菜单树展示
     * <p>
     * <lu>
     * <li>平台管理员/租户管理员角色能够查看所有菜单树</li>
     * <li>其他角色只能查看其分配了菜单权限集的菜单树</li> </lu>
     *
     * @param roleIds    角色合并的ID集合
     * @param tenantId   租户ID
     * @param lang       菜单语言
     * @param labels     标签IDs
     * @param unionLabel 是否按照标签并集查询(即包含所有标签)
     * @return 经过权限检测过后的菜单树
     */
    List<Menu> selectRoleMenus(@Param("roleIds") List<Long> roleIds, @Param("tenantId") Long tenantId,
                               @Param("lang") String lang, @Param("labels") Set<String> labels,
                               @Param("unionLabel") Boolean unionLabel);

    /**
     * 查询安全组关联的菜单，用于左侧菜单树展示
     *
     * @param roleIds     角色合并的ID集合
     * @param tenantId    租户ID
     * @param lang        菜单语言
     * @param labels      标签IDs
     * @param unionLabel 是否按照标签并集查询(即包含所有标签)
     * @return 经过权限检测过后的菜单树
     */
    List<Menu> selectSecGrpMenus(@Param("roleIds") List<Long> roleIds, @Param("tenantId") Long tenantId,
                                 @Param("lang") String lang, @Param("labels") Set<String> labels,
                                 @Param("unionLabel") Boolean unionLabel);

    /**
     * 查询目录列表 root、dir 类型
     *
     * @return 备选父级目录列表
     */
    List<Menu> selectMenuDirs(MenuSearchDTO menuParams);

    /**
     * 查询菜单权限集
     *
     * @param permissionSetParam menuId 不能为空
     */
    List<Menu> selectMenuPermissionSet(PermissionSetSearchDTO permissionSetParam);

    /**
     * 查询权限集下的权限
     *
     * @param permissionSetParam 权限集参数
     */
    List<Permission> selectPermissionSetPermissions(PermissionSetSearchDTO permissionSetParam);

    /**
     * 查询权限集下的LOV
     *
     * @param permissionSetParam 权限集参数
     */
    List<Lov> selectPermissionSetLovs(PermissionSetSearchDTO permissionSetParam);

    /**
     * 查询可分配的权限
     *
     * @param permissionSetParam 权限集参数
     */
    List<Permission> selectAssignablePermissions(PermissionSetSearchDTO permissionSetParam);

    /**
     * 查询可分配的Lov
     *
     * @param permissionSetParam 权限集参数
     */
    List<Lov> selectAssignableLovs(PermissionSetSearchDTO permissionSetParam);

    /**
     * 依据角色查询其可分配权限的权限集列表
     */
    List<Menu> selectRolePermissionSet(PermissionSetSearchDTO permissionSetParam);

    /**
     * 依据角色查询其可分配权限的权限集列表
     */
    List<Menu> listRolePermissionSet(PermissionSetSearchDTO permissionSetParam);

    List<RolePermissionVO> listRolePermission(PermissionSetSearchDTO permissionSetParam);

    /**
     * 查询权限集编码
     *
     * @param tenantId 租户ID
     * @param roleId   角色ID
     * @param menuCode 菜单编码
     * @return 权限集编码
     */
    List<String> selectPermissionSetCodes(@Param("tenantId") Long tenantId,
                                          @Param("roleId") Long roleId,
                                          @Param("menuCode") String menuCode);

    /**
     * 判断是否有权限
     *
     * @param roleIds 当前角色集合
     * @param codes   权限集编码
     */
    List<PermissionCheckDTO> checkPermissionSets(@Param("roleIds") List<Long> roleIds,
                                                 @Param("codes") List<String> codes);

    /**
     * 查询菜单对应的角色列表
     *
     * @param permissionIds 权限集
     * @param menu          角色限制
     * @return
     */
    List<AccessAuthDTO> listMenuAssignRole(@Param("menu") Menu menu, @Param("permissionIds") List<Long> permissionIds);

    /**
     * 查询角色权限
     *
     * @param roleId        角色id
     * @param permissionIds 权限集
     * @return
     */
    List<RolePermission> checkRolePermission(@Param("roleId") Long roleId,
                                             @Param("permissionIds") List<Long> permissionIds);

    /**
     * 安全组——查询可分配的权限集，并标识是否已分配
     *
     * @param searchDTO 查询参数
     * @return 权限集及权限集上层菜单列表
     */
    List<Menu> selectSecGrpAssignablePermissionSet(SecGrpPermissionSearchDTO searchDTO);

    /**
     * 安全组——查询已分配的权限集
     *
     * @param searchDTO 查询参数
     * @return 权限集及权限集上层菜单列表
     */
    List<Menu> selectSecGrpAssignedPermissionSet(SecGrpPermissionSearchDTO searchDTO);

    /**
     * 角色分配安全组——查询安全组的权限集，并标识是否屏蔽
     *
     * @param roleId   被非分配的角色ID
     * @param secGrpId 安全组ID
     * @return 权限集及权限集上层菜单列表
     */
    List<Menu> selectRoleSecGrpAssignablePermissionSet(@Param("roleId") Long roleId, @Param("secGrpId") Long secGrpId);

    /**
     * 查询租户客户化的菜单
     */
    List<Menu> selectTenantCustomMenu(MenuSearchDTO menuParams);

    /**
     * 查询菜单父级
     */
    List<Menu> parentMenu(@Param("idList") List<Long> idList);

    /**
     * 查询权限集
     *
     * @param permissionSetSearchDTO permissionSetSearchDTO
     * @return 权限集列表
     */
    List<Menu> selectPermissionSets(PermissionSetSearchDTO permissionSetSearchDTO);

    /**
     * 查询导出平台菜单数据
     *
     * @param menuSearchDTO 菜单数据
     * @return 查询结果
     */
    List<MenuSiteExportDTO> selectExportSiteMenuData(MenuSearchDTO menuSearchDTO);

    List<Menu> selectByCodeLike(@Param("list") List<String> notPass, @Param("menuLevel") String menuLevel);
}
