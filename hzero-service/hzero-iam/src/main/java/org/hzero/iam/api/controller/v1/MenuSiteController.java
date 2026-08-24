package org.hzero.iam.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.hzero.iam.api.dto.MenuCopyDataDTO;
import org.hzero.iam.api.dto.MenuSearchDTO;
import org.hzero.iam.api.dto.MenuSiteExportDTO;
import org.hzero.iam.api.dto.PermissionSetSearchDTO;
import org.hzero.iam.app.service.MenuService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.Menu;
import org.hzero.iam.domain.repository.MenuRepository;
import org.hzero.iam.domain.vo.Lov;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.constant.HiamResourceLevel;
import org.hzero.iam.infra.constant.PermissionType;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author bojiangzhou 2019/04/11 平台维护所有菜单，租户只能维护客制化菜单
 * @author bojiangzhou 2019/01/18 改造权限集，优化部分代码结构
 * @author allen 2018/7/2
 */
@Api(tags = SwaggerApiConfig.MENU_SITE)
@RestController("menuSiteController.v1")
@RequestMapping("/hzero/v1/menus")
public class MenuSiteController extends BaseController {

    @Autowired
    private MenuService menuService;
    @Autowired
    private MenuRepository menuRepository;

    //
    // 菜单复制
    // ------------------------------------------------------------------------------

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("平台层-客户化菜单复制数据查询")
    @GetMapping("/copy")
    public ResponseEntity<List<Menu>> listCustomMenuForCopy(@Encrypt @RequestParam(name = "rootMenuId") Long rootMenuId,
                                                            @RequestParam(name = "level") String level) {
        List<Menu> menuTreeForExport = menuRepository.selectMenuTreeForCopy(BaseConstants.DEFAULT_TENANT_ID, HiamResourceLevel.SITE, HiamResourceLevel.valueOf(level), rootMenuId);
        return Results.success(menuTreeForExport);
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("平台层-新增复制的租户客户化菜单")
    @PostMapping("/copy-insert")
    public ResponseEntity<List<Menu>> insertCustomMenuCopied(@RequestBody MenuCopyDataDTO menuCopyData,
                                                             @RequestParam(name = "level") String level) {
        menuCopyData.setTargetTenantId(BaseConstants.DEFAULT_TENANT_ID);
        menuCopyData.setSourceTenantId(BaseConstants.DEFAULT_TENANT_ID);
        menuService.insertMenuForCopy(HiamResourceLevel.SITE, HiamResourceLevel.valueOf(level), menuCopyData);
        return Results.success();
    }


    //
    // 菜单查询
    // ------------------------------------------------------------------------------

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("平台层-获取菜单目录")
    @GetMapping("/dir")
    @ProcessLovValue(targetField = {BaseConstants.FIELD_BODY})
    public ResponseEntity<Page<Menu>> listMenuDir(@ModelAttribute MenuSearchDTO menuParams, PageRequest pageRequest) {
        return Results.success(menuRepository.selectMenuDirsInSite(menuParams, pageRequest));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("平台层-菜单管理-获取树形菜单")
    @GetMapping("/manage-tree")
    public ResponseEntity<List<Menu>> listManageMenuTree(@ModelAttribute MenuSearchDTO menuParams) {
        // 展示平台层级组织层菜单
        menuParams.setManageFlag(Boolean.TRUE);
        return Results.success(menuRepository.selectMenuTreeInSite(menuParams));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("平台层-菜单管理-获取租户客户化树形菜单")
    @GetMapping("/manage-tree/custom/{tenantId}")
    public ResponseEntity<List<Menu>> listTenantCustomMenuTree(@PathVariable Long tenantId,
                                                               @ModelAttribute MenuSearchDTO menuParams) {
        // 展示平台层级组织层菜单
        menuParams.setManageFlag(Boolean.TRUE);
        return Results.success(menuRepository.selectTenantCustomMenuTree(tenantId, menuParams));
    }

    //
    // 菜单操作相关
    // ------------------------------------------------------------------------------

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("全局层创建目录")
    @PostMapping("/create")
    public ResponseEntity<Menu> create(@RequestBody Menu menu) {
        menu.setTenantId(Constants.SITE_TENANT_ID);
        validObject(menu, Menu.Valid.class);
        return Results.success(menuService.createMenuInSite(menu));
    }

    /**
     * @deprecated 登录可访问API，删除平台级API
     */
    @Deprecated
    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @ApiOperation(value = "菜单code重名校验")
    @PostMapping(value = "/check-duplicate")
    public ResponseEntity<Void> checkDuplicate(@RequestBody Menu menu) {
        menuService.checkDuplicate(menu);
        return Results.success();
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("全局层更新目录")
    @PostMapping("/update")
    public ResponseEntity<Void> update(@RequestBody Menu menu) {
        validObject(menu, Menu.Valid.class);
        SecurityTokenHelper.validToken(menu);
        menuService.update(menu);
        return Results.success();
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "平台层删除标准菜单")
    @DeleteMapping(value = "/{menuId}")
    public ResponseEntity<Void> deleteById(@Encrypt @PathVariable("menuId") Long menuId) {
        menuService.deleteById(Constants.SITE_TENANT_ID, menuId);
        return Results.success();
    }

    /**
     * 启用菜单及其子菜单
     */
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "平台层启用菜单及其子菜单")
    @PutMapping(value = "/enable")
    public ResponseEntity<Void> enable(@RequestBody Menu menu) {
        SecurityTokenHelper.validToken(menu);
        menuService.enableMenu(Constants.SITE_TENANT_ID, menu.getId());
        return Results.success();
    }

    /**
     * 启用菜单及其子菜单
     */
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "平台层禁用菜单及其子菜单")
    @PutMapping(value = "/disable")
    public ResponseEntity<Void> disable(@RequestBody Menu menu) {
        SecurityTokenHelper.validToken(menu);
        menuService.disableMenu(Constants.SITE_TENANT_ID, menu.getId());
        return Results.success();
    }

    //
    // 权限集相关
    // ------------------------------------------------------------------------------

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "查询菜单关联的权限集")
    @GetMapping(value = "/{menuId}/permission-set")
    @ProcessLovValue(targetField = {BaseConstants.FIELD_BODY})
    public ResponseEntity<List<Menu>> listMenuPermissionSet(@Encrypt @PathVariable("menuId") Long menuId,
                                                            @RequestParam(required = false) Long tenantId,
                                                            @ModelAttribute PermissionSetSearchDTO permissionSetParam) {
        tenantId = Optional.ofNullable(tenantId).orElse(Constants.SITE_TENANT_ID);
        return Results.success(menuRepository.selectMenuPermissionSet(tenantId, menuId, permissionSetParam));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "分页查询权限集下的权限")
    @GetMapping(value = "/permission-set/{permissionSetId}/permissions")
    public ResponseEntity<Page<org.hzero.iam.domain.entity.Permission>> listPsPermission(
            @Encrypt @PathVariable("permissionSetId") Long permissionSetId,
            @RequestParam(required = false) Long tenantId,
            String code, String condition, PageRequest pageRequest) {
        tenantId = Optional.ofNullable(tenantId).orElse(Constants.SITE_TENANT_ID);
        PermissionSetSearchDTO permissionSetParam = new PermissionSetSearchDTO(tenantId, permissionSetId, condition);
        permissionSetParam.setCode(code);
        return Results.success(menuRepository.selectPermissionSetPermissions(permissionSetParam, pageRequest));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "分页查询权限集下的LOV")
    @GetMapping(value = "/permission-set/{permissionSetId}/lovs")
    public ResponseEntity<Page<Lov>> listPsLov(
            @Encrypt @PathVariable("permissionSetId") Long permissionSetId,
            @RequestParam(required = false) Long tenantId,
            String condition, PageRequest pageRequest) {
        tenantId = Optional.ofNullable(tenantId).orElse(Constants.SITE_TENANT_ID);
        PermissionSetSearchDTO permissionSetParam = new PermissionSetSearchDTO(tenantId, permissionSetId, condition);
        return Results.success(menuRepository.selectPermissionSetLovs(permissionSetParam, pageRequest));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "分页查询可分配的权限")
    @GetMapping(value = "/{permissionSetId}/assignable-permissions")
    public ResponseEntity<Page<org.hzero.iam.domain.entity.Permission>> listAssignablePermissions(
            @Encrypt @PathVariable("permissionSetId") Long permissionSetId,
            String code, String condition, PageRequest pageRequest) {

        PermissionSetSearchDTO permissionSetParam =
                new PermissionSetSearchDTO(Constants.SITE_TENANT_ID, permissionSetId, condition);
        permissionSetParam.setCode(code);
        return Results.success(menuRepository.selectAssignablePermissions(permissionSetParam, pageRequest));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "分页查询可分配的LOV")
    @CustomPageRequest
    @GetMapping(value = "/{permissionSetId}/assignable-lovs")
    public ResponseEntity<Page<Lov>> listAssignableLovs(
            @Encrypt @PathVariable("permissionSetId") Long permissionSetId,
            String condition, PageRequest pageRequest) {

        PermissionSetSearchDTO permissionSetParam =
                new PermissionSetSearchDTO(Constants.SITE_TENANT_ID, permissionSetId, condition);
        return Results.success(menuRepository.selectAssignableLovs(permissionSetParam, pageRequest));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "为权限集分配权限(包括Lov)")
    @PostMapping(value = "/{menuId}/permission-set/assign-permissions")
    public ResponseEntity<Void> assignPsPermissions(@Encrypt @PathVariable("menuId") Long menuId,
                                                    @RequestParam PermissionType permissionType,
                                                    @RequestBody String[] permissionCodes) {

        menuService.assignPsPermissions(menuId, permissionType, permissionCodes);
        return Results.success();
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "回收权限集的权限(包括Lov)")
    @PostMapping(value = "/{menuId}/permission-set/recycle-permissions")
    public ResponseEntity<Void> recyclePsPermissions(@Encrypt @PathVariable("menuId") Long menuId,
                                                     @RequestBody String[] permissionCodes,
                                                     PermissionType permissionType) {
        menuService.recyclePsPermissions(menuId, permissionCodes, permissionType);
        return Results.success();
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "为权限集分配权限(包括Lov)")
    @PostMapping(value = "/permission-set/assign-permissions")
    public ResponseEntity<Void> assignPsPermissionsWithCode(@ApiParam("权限集编码") @RequestParam String code,
                                                            @ApiParam("层级:site/organization") @RequestParam String level,
                                                            @ApiParam("权限类型：permission/lov") @RequestParam PermissionType permissionType,
                                                            @RequestBody String[] permissionCodes) {

        menuService.assignPsPermissions(BaseConstants.DEFAULT_TENANT_ID, code, level, permissionType, permissionCodes);
        return Results.success();
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "回收权限集的权限(包括Lov)")
    @PostMapping(value = "/permission-set/recycle-permissions")
    public ResponseEntity<Void> recyclePsPermissionsWithCode(@ApiParam("权限集编码") @RequestParam String code,
                                                             @ApiParam("层级:site/organization") @RequestParam String level,
                                                             @RequestBody String[] permissionCodes,
                                                             PermissionType permissionType) {
        menuService.recyclePsPermissions(BaseConstants.DEFAULT_TENANT_ID, code, level, permissionCodes, permissionType);
        return Results.success();
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "导出菜单数据")
    @GetMapping(value = "/export-site-data")
    @ExcelExport(MenuSiteExportDTO.class)
    public ResponseEntity<List<MenuSiteExportDTO>> exportSiteMenuData(ExportParam exportParam, MenuSearchDTO menuSearchDTO, HttpServletResponse response) {
        return Results.success(menuService.exportSiteMenuData(menuSearchDTO));
    }

    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @ApiOperation(value = "系统菜单列表")
    @GetMapping
    public ResponseEntity<Map<Long, String>> queryMenus(Menu menu) {
        // 查询菜单
        return Results.success(this.menuService.queryMenus(menu));
    }
}
