package org.hzero.iam.api.controller.v1;

import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.swagger.annotation.Permission;

import org.hzero.core.util.Results;
import org.hzero.iam.app.service.DocTypeService;
import org.hzero.iam.app.service.DomainService;
import org.hzero.iam.app.service.MenuService;
import org.hzero.iam.app.service.impl.RoleDataFixService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.infra.common.utils.UserUtils;

/**
 * 数据修复工具接口
 *
 * @author bojiangzhou
 */
@Api(tags = SwaggerApiConfig.TOOL_DATA_FIX)
@RequestMapping("/v1/tool/data-fix")
@RestController("toolDataFixController.v1")
public class ToolDataFixController {

    @Autowired
    private RoleDataFixService roleDataFixService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private DocTypeService docTypeService;
    @Autowired
    private DomainService domainService;


    /**
     * 1.code 只保留最后一段
     * 2.h_created_by_tenant_id 没有的根据 created_by 查询用户租户
     * 3.h_level_path
     * 4.h_inherit_level_path
     */
    @ApiOperation(value = "修复角色数据")
    @Permission(permissionLogin = true)
    @PostMapping("/role")
    public ResponseEntity<Map<String, Object>> fixRoleData() {
        UserUtils.checkSuperAdmin();
        return Results.success(roleDataFixService.fixRoleData());
    }

    @ApiOperation(value = "修复菜单数据")
    @Permission(permissionLogin = true)
    @PostMapping("/menu")
    public ResponseEntity<Map<String, Object>> fixMenuData(boolean initAll) {
        UserUtils.checkSuperAdmin();
        return Results.success(menuService.fixMenuData(initAll));
    }

    @ApiOperation(value = "修复单据权限数据")
    @Permission(permissionLogin = true)
    @PostMapping("/doc-type")
    public ResponseEntity<Void> fixDocTypeData() {
        UserUtils.checkSuperAdmin();
        docTypeService.fixDocTypeData();
        return Results.success();
    }

    @ApiOperation(value = "将1.4之前的单点登录域名配置修复到1.5版本")
    @Permission(permissionLogin = true)
    @PostMapping("/sso-domain")
    public ResponseEntity<Map<String, Object>> fixSsoData() {
        UserUtils.checkSuperAdmin();
        return Results.success(domainService.fixSsoDomain());
    }


}
