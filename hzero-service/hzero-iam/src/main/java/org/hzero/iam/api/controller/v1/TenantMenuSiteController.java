package org.hzero.iam.api.controller.v1;

import java.util.List;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.api.dto.MenuCopyDataDTO;
import org.hzero.iam.api.dto.MenuSearchDTO;
import org.hzero.iam.api.dto.PermissionSetSearchDTO;
import org.hzero.iam.app.service.MenuService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.Menu;
import org.hzero.iam.domain.repository.MenuRepository;
import org.hzero.iam.domain.vo.Lov;
import org.hzero.iam.infra.constant.HiamResourceLevel;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 租户菜单管理，只有平台层功能
 *
 * @author bojiangzhou 2019/11/29
 */
@Api(tags = SwaggerApiConfig.TENANT_MENU_SITE)
@RestController("tenantMenuSiteController.v1")
@RequestMapping("/v1/tenant-menus")
public class TenantMenuSiteController extends BaseController {

    @Autowired
    private MenuService menuService;
    @Autowired
    private MenuRepository menuRepository;

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("租户菜单管理-获取租户客户化树形菜单")
    @GetMapping("/{tenantId}")
    public ResponseEntity<List<Menu>> listTenantCustomMenuTree(@PathVariable Long tenantId,
                                                               @Encrypt MenuSearchDTO params) {
        params.setManageFlag(Boolean.FALSE);
        return Results.success(menuRepository.selectTenantCustomMenuTree(tenantId, params));
    }


    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("租户菜单管理-新增复制的租户客户化菜单")
    @PostMapping("/copy")
    public ResponseEntity<List<Menu>> insertCustomMenuCopied(@RequestBody @Encrypt MenuCopyDataDTO menuCopyData) {
        menuService.insertMenuForCopy(HiamResourceLevel.ORGANIZATION, HiamResourceLevel.ORGANIZATION, menuCopyData);
        return Results.success();
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("租户菜单管理-新增客户化菜单")
    @PostMapping("/add")
    public ResponseEntity<Void> insertCustomMenu(Long tenantId, @RequestBody @Encrypt Menu menu) {
        menuService.insertCustomMenu(tenantId, menu);
        return Results.success();
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("租户菜单管理-更新菜单")
    @PutMapping
    public ResponseEntity<Void> updateCustomMenu(Long tenantId, @RequestBody @Encrypt Menu menu) {
        validObject(menu, Menu.Valid.class);
        SecurityTokenHelper.validToken(menu);
        menuService.updateCustomMenu(tenantId, menu);
        return Results.success();
    }

    /**
     * 启用菜单及其子菜单
     */
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "租户菜单管理-启用菜单及其子菜单")
    @PutMapping(value = "/enable")
    public ResponseEntity<Void> enable(Long tenantId, @RequestBody @Encrypt Menu menu) {
        SecurityTokenHelper.validToken(menu);
        menuService.enableCustomMenu(tenantId, menu.getId());
        return Results.success();
    }

    /**
     * 启用菜单及其子菜单
     */
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "租户菜单管理-禁用菜单及其子菜单")
    @PutMapping(value = "/disable")
    public ResponseEntity<Void> disable(Long tenantId, @RequestBody @Encrypt Menu menu) {
        SecurityTokenHelper.validToken(menu);
        menuService.disableCustomMenu(tenantId, menu.getId());
        return Results.success();
    }

    // 权限集相关

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "分页查询租户可分配的权限")
    @GetMapping(value = "{permissionSetId}/assignable-permissions")
    public ResponseEntity<Page<org.hzero.iam.domain.entity.Permission>> listAssignablePermissions(
            Long tenantId,
            @PathVariable("permissionSetId") @Encrypt Long permissionSetId,
            String code, String condition, PageRequest pageRequest) {
        PermissionSetSearchDTO permissionSetParam = new PermissionSetSearchDTO(tenantId, permissionSetId, condition);
        permissionSetParam.setCode(code);
        return Results.success(menuRepository.selectTenantAssignablePermissions(permissionSetParam, pageRequest));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "分页查询可分配的LOV")
    @GetMapping(value = "/{permissionSetId}/assignable-lovs")
    public ResponseEntity<Page<Lov>> listAssignableLovs(
            Long tenantId,
            @PathVariable("permissionSetId") @Encrypt Long permissionSetId,
            String condition, PageRequest pageRequest) {
        PermissionSetSearchDTO permissionSetParam = new PermissionSetSearchDTO(tenantId, permissionSetId, condition);
        return Results.success(menuRepository.selectAssignableLovs(permissionSetParam, pageRequest));
    }
}
