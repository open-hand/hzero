package org.hzero.iam.api.controller.v1;

import java.util.List;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Results;
import org.hzero.iam.api.dto.MenuPermissionSetDTO;
import org.hzero.iam.api.dto.PermissionSetSearchDTO;
import org.hzero.iam.app.service.PermissionCheckService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.Menu;
import org.hzero.iam.domain.entity.PermissionCheck;
import org.hzero.iam.domain.repository.MenuRepository;
import org.hzero.iam.domain.repository.PermissionCheckRepository;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 缺失权限API
 *
 * @author KAIBING.JIANG@HAND-CHINA.COM
 */
@Api(tags = SwaggerApiConfig.PERMISSION_CHECK_SITE)
@RestController("permissionCheckSiteController.v1")
@RequestMapping("/v1/permission-check")
public class PermissionCheckSiteController {

    @Autowired
    private PermissionCheckRepository permissionCheckRepository;
    @Autowired
    private PermissionCheckService permissionCheckService;
    @Autowired
    private MenuRepository menuRepository;

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("分页查询缺失权限记录")
    @GetMapping
    public ResponseEntity<Page<PermissionCheck>> page(PermissionCheck permissionCheck,
                                                      @ApiIgnore @SortDefault(value =PermissionCheck.FIELD_PERMISSION_CHECK_ID , direction = Sort.Direction.DESC)  PageRequest pageRequest) {
        return Results.success(permissionCheckRepository.selectPermissionCheck(permissionCheck, pageRequest));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("缺失记录明细")
    @GetMapping("/{permissionCheckId}")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<PermissionCheck> detail(@Encrypt @PathVariable Long permissionCheckId) {
        return Results.success(permissionCheckRepository.selectPermissionDetail(permissionCheckId));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("清理缺失记录")
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearPermissionCheck(@RequestParam(required = false) String checkType, @RequestParam String clearType) {
        permissionCheckService.clearPermissionCheck(clearType, checkType);
        return Results.success();
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("批量删除缺失记录")
    @DeleteMapping
    public ResponseEntity<Void> batchDelete(List<PermissionCheck> list) {
        SecurityTokenHelper.validToken(list);
        permissionCheckRepository.batchDeleteByPrimaryKey(list);
        return Results.success();
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("查询可以添加的权限集")
    @GetMapping("/permission-set")
    public ResponseEntity<Page<Menu>> pageMenu(PermissionSetSearchDTO permissionSetSearchDTO,
                                               @ApiIgnore @SortDefault(value = Menu.FIELD_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(menuRepository.selectPermissionSets(permissionSetSearchDTO, pageRequest));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("将NOT_PASS的API批量分配到权限集，需传入权限编码和要分配的权限集ID")
    @PostMapping("/not-pass-api/batch-assign")
    public ResponseEntity<Void> assignNotPassApi(@RequestBody MenuPermissionSetDTO menuPermissionSetDTO) {
        permissionCheckService.assignNotPassApi(menuPermissionSetDTO);
        return Results.success();
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("刷新 MISMATCH 的API的服务，需传入权限编码")
    @PostMapping("/mismatch-api/batch-refresh")
    public ResponseEntity<Void> refreshMismatchApi(@RequestBody String[] serviceCodes) {
        permissionCheckService.refreshMismatchApi(serviceCodes);
        return Results.success();
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("将MENU_PERMISSION的API批量分配到菜单默认权限集，需传入权限编码和要分配的权限集ID")
    @PostMapping("/menu-permission-api/batch-assign")
    public ResponseEntity<Void> assignMenuPermissionApi(@RequestBody MenuPermissionSetDTO menuPermissionSetDTO) {
        permissionCheckService.assignMenuPermissionApi(menuPermissionSetDTO);
        return Results.success();
    }

}
