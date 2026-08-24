package org.hzero.iam.api.controller.v1;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.api.dto.*;
import org.hzero.iam.app.service.MenuService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.Menu;
import org.hzero.iam.domain.repository.MenuRepository;
import org.hzero.iam.domain.vo.Lov;
import org.hzero.iam.infra.constant.HiamResourceLevel;
import org.hzero.iam.infra.constant.PermissionType;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;


/**
 * @author bojiangzhou 2019/04/11 平台维护所有菜单，租户只能维护客制化菜单
 * @author allen 2018/7/2
 */
@Api(tags = SwaggerApiConfig.MENU)
@RestController("menuController.v1")
@RequestMapping("/hzero/v1")
public class MenuController extends BaseController {

    @Autowired
    private MenuService menuService;
    @Autowired
    private MenuRepository menuRepository;

    //
    // 客制化菜单导入导出
    // ------------------------------------------------------------------------------

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("租户客户化菜单导入")
    @PostMapping("/{organizationId}/menus/custom-menu-import")
    public ResponseEntity<Void> customMenuImport(@PathVariable(name = "organizationId") Long organizationId,
                                                 @RequestParam(name = "customMenuFile") MultipartFile customMenuFile) throws IOException {
        List<Menu> menus = menuService.handleCustomMenuImportData(organizationId, customMenuFile);
        menuService.importMenuTree(organizationId, menus);
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("租户客户化菜单导出")
    @PostMapping("/{organizationId}/menus/custom-menu-export")
    public ResponseEntity<Void> customMenuExport(@PathVariable("organizationId") Long organizationId, @RequestBody List<Menu> menuTreeList, HttpServletResponse response) throws IOException {
        menuService.handleCustomMenuExportData(organizationId, menuTreeList, response);
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("租户层-客户化菜单导出数据查询")
    @GetMapping("/{organizationId}/menus/custom-menu-export-tree")
    public ResponseEntity<List<Menu>> listCustomMenuExportTree(@PathVariable("organizationId") Long tenantId) {
        List<Menu> menuTreeForExport = menuRepository.selectMenuTreeForExport(tenantId);
        return Results.success(menuTreeForExport);
    }

    //
    // 菜单复制
    // ------------------------------------------------------------------------------

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("租户层-客户化菜单复制数据查询")
    @GetMapping("/{organizationId}/menus/copy")
    public ResponseEntity<List<Menu>> listCustomMenuForCopy(@PathVariable("organizationId") Long tenantId,
                                                            @Encrypt @RequestParam(name = "rootMenuId") Long rootMenuId) {
        List<Menu> menuTreeForExport = menuRepository.selectMenuTreeForCopy(tenantId, HiamResourceLevel.ORGANIZATION, HiamResourceLevel.ORGANIZATION, rootMenuId);
        return Results.success(menuTreeForExport);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("租户层-新增复制的租户客户化菜单")
    @PostMapping("/{organizationId}/menus/copy-insert")
    public ResponseEntity<Void> insertCustomMenuCopied(@PathVariable(name = "organizationId") Long organizationId,
                                                       @RequestBody MenuCopyDataDTO menuCopyData) {
        menuCopyData.setTargetTenantId(organizationId);
        menuCopyData.setSourceTenantId(organizationId);
        menuService.insertMenuForCopy(HiamResourceLevel.ORGANIZATION, HiamResourceLevel.ORGANIZATION, menuCopyData);
        return Results.success();
    }

    //
    // 菜单查询
    // ------------------------------------------------------------------------------

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("租户层-菜单管理-获取平台目录和客制化目录")
    @GetMapping("/{organizationId}/menus/dir")
    @ProcessLovValue(targetField = {BaseConstants.FIELD_BODY})
    public ResponseEntity<Page<Menu>> listMenuDir(@PathVariable(name = "organizationId") Long tenantId,
                                                  @ModelAttribute MenuSearchDTO menuParams,
                                                  PageRequest pageRequest) {
        return Results.success(menuRepository.selectMenuDirsInTenant(menuParams, pageRequest));
    }

    /**
     * 租户层-菜单管理-获取树形菜单
     *
     * @param tenantId 租户ID, 查询租户客户化菜单时需要
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("租户层-菜单管理-获取树形菜单")
    @GetMapping("/{organizationId}/menus/manage-tree")
    public ResponseEntity<List<Menu>> listManageMenuTree(@PathVariable(name = "organizationId") Long tenantId,
                                                         @ModelAttribute MenuSearchDTO menuParams) {
        menuParams.setManageFlag(Boolean.TRUE);
        return Results.success(menuRepository.selectMenuTreeInTenant(menuParams));
    }

    /**
     * 菜单展示-获取导航栏树形菜单
     * <p>
     * 展示当前角色可用的菜单
     */
    @ApiOperation("菜单展示-获取导航栏树形菜单")
    @Permission(permissionLogin = true)
    @GetMapping("/menus/tree")
    public ResponseEntity<List<Menu>> listNavMenuTree(MenuTreeQueryDTO menuTreeQueryDTO) {
        return Results.success(menuRepository.selectRoleMenuTree(menuTreeQueryDTO));
    }

    @Permission(permissionLogin = true)
    @ApiOperation(value = "租户层-根据唯一性查询菜单")
    @GetMapping(value = "/menu")
    public ResponseEntity<Menu> queryMenu(@ApiParam("租户ID，默认0") @RequestParam(required = false) Long tenantId,
                                          @ApiParam("菜单编码") @RequestParam("code") String code,
                                          @ApiParam("菜单层级") @RequestParam("level") String level) {
        return Results.success(menuRepository.queryMenu(tenantId, code, level));
    }

    //
    // 菜单操作相关
    // ------------------------------------------------------------------------------

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("租户层-创建菜单")
    @PostMapping("/{organizationId}/menus/create")
    public ResponseEntity<Menu> create(@PathVariable(name = "organizationId") Long tenantId,
                                       @RequestBody @Valid Menu menu) {
        menu.setTenantId(tenantId);
        menu.setLevel(HiamResourceLevel.ORGANIZATION.value());
        validObject(menu, Menu.Valid.class);
        return Results.success(menuService.createMenuInTenant(tenantId, menu));
    }

    /**
     * 菜单重名校验<br/>
     * <p>
     * 菜单唯一性索引属性(tenant_id, code, level)
     * MenuDTO参数中code/level/type字段必须存在相应值
     */
    @Permission(permissionLogin = true)
    @ApiOperation(value = "菜单 code 重名校验")
    @PostMapping(value = "/{organizationId}/menus/check-duplicate")
    public ResponseEntity<Void> checkDuplicate(@PathVariable(name = "organizationId") Long tenantId,
                                               @RequestBody Menu menu) {
        menu.setTenantId(tenantId);
        menuService.checkDuplicate(menu);
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("租户层-更新菜单")
    @PostMapping("/{organizationId}/menus/update")
    public ResponseEntity<Menu> update(@PathVariable(name = "organizationId") Long tenantId,
                                       @RequestBody Menu menu) {
        menu.setTenantId(tenantId);
        menu.setLevel(HiamResourceLevel.ORGANIZATION.value());
        validObject(menu, Menu.Valid.class);
        SecurityTokenHelper.validToken(menu);
        return Results.success(menuService.update(menu));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "租户层-删除标准菜单")
    @DeleteMapping(value = "/{organizationId}/menus/{menuId}")
    public ResponseEntity<Void> deleteById(@PathVariable(name = "organizationId") Long tenantId,
                                           @Encrypt @PathVariable("menuId") Long menuId) {
        menuService.deleteById(tenantId, menuId);
        return Results.success();
    }

    /**
     * 启用菜单及其子菜单
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "租户层-启用菜单及其子菜单")
    @PutMapping(value = "/{organizationId}/menus/enable")
    public ResponseEntity<Void> enable(@PathVariable(name = "organizationId") Long tenantId,
                                       @RequestBody Menu menu) {
        SecurityTokenHelper.validToken(menu);
        menuService.enableMenu(tenantId, menu.getId());
        return Results.success();
    }

    /**
     * 启用菜单及其子菜单
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "租户层-禁用菜单及其子菜单")
    @PutMapping(value = "/{organizationId}/menus/disable")
    public ResponseEntity<Void> disable(@PathVariable(name = "organizationId") Long tenantId,
                                        @RequestBody Menu menu) {
        SecurityTokenHelper.validToken(menu);
        menuService.disableMenu(tenantId, menu.getId());
        return Results.success();
    }

    //
    // 权限集相关
    // ------------------------------------------------------------------------------

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "租户层-查询菜单关联的权限集")
    @GetMapping(value = "/{organizationId}/menus/{menuId}/permission-set")
    @ProcessLovValue(targetField = {BaseConstants.FIELD_BODY})
    public ResponseEntity<List<Menu>> listMenuPermissionSet(@PathVariable(name = "organizationId") Long tenantId,
                                                            @Encrypt @PathVariable("menuId") Long menuId,
                                                            @ModelAttribute PermissionSetSearchDTO permissionSetParam) {
        return Results.success(menuRepository.selectMenuPermissionSet(tenantId, menuId, permissionSetParam));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "分页查询权限集下的权限")
    @GetMapping(value = "/{organizationId}/menus/permission-set/{permissionSetId}/permissions")
    public ResponseEntity<Page<org.hzero.iam.domain.entity.Permission>> listPsPermission(
            @PathVariable(name = "organizationId") Long tenantId,
            @Encrypt @PathVariable("permissionSetId") Long permissionSetId,
            @Encrypt PermissionSetSearchDTO permissionSetParam, PageRequest pageRequest) {

        permissionSetParam.setTenantId(tenantId);
        permissionSetParam.setPermissionSetId(permissionSetId);
        return Results.success(menuRepository.selectPermissionSetPermissions(permissionSetParam, pageRequest));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "分页查询权限集下的LOV")
    @GetMapping(value = "/{organizationId}/menus/permission-set/{permissionSetId}/lovs")
    public ResponseEntity<Page<Lov>> listPsLov(
            @PathVariable(name = "organizationId") Long tenantId,
            @Encrypt @PathVariable("permissionSetId") Long permissionSetId,
            String condition, PageRequest pageRequest) {
        PermissionSetSearchDTO permissionSetParam = new PermissionSetSearchDTO(tenantId, permissionSetId, condition);
        return Results.success(menuRepository.selectPermissionSetLovs(permissionSetParam, pageRequest));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "分页查询租户可分配的权限")
    @GetMapping(value = "/{organizationId}/menus/{permissionSetId}/assignable-permissions")
    public ResponseEntity<Page<org.hzero.iam.domain.entity.Permission>> listAssignablePermissions(
            @PathVariable(name = "organizationId") Long tenantId,
            @Encrypt @PathVariable("permissionSetId") Long permissionSetId,
            String code, String condition, PageRequest pageRequest) {

        PermissionSetSearchDTO permissionSetParam = new PermissionSetSearchDTO(tenantId, permissionSetId, condition);
        permissionSetParam.setCode(code);
        return Results.success(menuRepository.selectTenantAssignablePermissions(permissionSetParam, pageRequest));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "分页查询可分配的LOV")
    @GetMapping(value = "/{organizationId}/menus/{permissionSetId}/assignable-lovs")
    public ResponseEntity<Page<Lov>> listAssignableLovs(
            @PathVariable(name = "organizationId") Long tenantId,
            @Encrypt @PathVariable("permissionSetId") Long permissionSetId,
            String condition, PageRequest pageRequest) {

        PermissionSetSearchDTO permissionSetParam = new PermissionSetSearchDTO(tenantId, permissionSetId, condition);
        return Results.success(menuRepository.selectAssignableLovs(permissionSetParam, pageRequest));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "为权限集分配权限(包括Lov)")
    @PostMapping(value = "/{organizationId}/menus/{menuId}/permission-set/assign-permissions")
    public ResponseEntity<Void> assignPsPermissions(@PathVariable("organizationId") Long tenantId,
                                                    @Encrypt @PathVariable("menuId") Long menuId,
                                                    @RequestParam PermissionType permissionType,
                                                    @RequestBody String[] permissionCodes) {

        menuService.assignPsPermissions(menuId, permissionType, permissionCodes);
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "回收权限集的权限(包括Lov)")
    @PostMapping(value = "/{organizationId}/menus/{menuId}/permission-set/recycle-permissions")
    public ResponseEntity<Void> recyclePsPermissions(@PathVariable("organizationId") Long tenantId,
                                                     @Encrypt @PathVariable("menuId") Long menuId,
                                                     @RequestBody String[] permissionCodes,
                                                     PermissionType permissionType) {
        menuService.recyclePsPermissions(menuId, permissionCodes, permissionType);
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "为权限集分配权限(包括Lov)")
    @PostMapping(value = "/{organizationId}/permission-set/assign-permissions")
    public ResponseEntity<Void> assignPsPermissionsWithCode(@PathVariable("organizationId") Long tenantId,
                                                            @ApiParam("权限集编码") @RequestParam String code,
                                                            @ApiParam("层级:site/organization") @RequestParam String level,
                                                            @ApiParam("权限类型：permission/lov") @RequestParam PermissionType permissionType,
                                                            @RequestBody String[] permissionCodes) {

        menuService.assignPsPermissions(tenantId, code, level, permissionType, permissionCodes);
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "回收权限集的权限(包括Lov)")
    @PostMapping(value = "/{organizationId}/permission-set/recycle-permissions")
    public ResponseEntity<Void> recyclePsPermissionsWithCode(@PathVariable("organizationId") Long tenantId,
                                                             @ApiParam("权限集编码") @RequestParam String code,
                                                             @ApiParam("层级:site/organization") @RequestParam String level,
                                                             @RequestBody String[] permissionCodes,
                                                             PermissionType permissionType) {
        menuService.recyclePsPermissions(tenantId, code, level, permissionCodes, permissionType);
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiOperation("当前用户获取菜单下可访问的权限集编码")
    @GetMapping("/{organizationId}/menus/permissions")
    public ResponseEntity<List<String>> listMenuPermissionSets(@PathVariable(name = "organizationId") Long tenantId,
                                                               @RequestParam(value = "code") String code) {
        return Results.success(menuRepository.selectAccessiblePermissionSets(tenantId, code));
    }

    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiOperation("当前用户是否拥有权限集")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "codes", value = "权限集编码")
    })
    @PostMapping("/menus/check-permissions")
    public ResponseEntity<List<PermissionCheckDTO>> checkPermissions(@RequestBody List<String> codes) {
        return Results.success(menuRepository.checkPermissionSets(codes));
    }

}
