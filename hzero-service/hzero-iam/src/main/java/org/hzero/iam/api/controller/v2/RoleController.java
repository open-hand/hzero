package org.hzero.iam.api.controller.v2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.app.service.RoleService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.vo.RoleVO;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 *
 * @author bojiangzhou 2020/08/06
 */
@Api(tags = SwaggerApiConfig.ROLE_V2)
@RestController("roleController.v2")
@RequestMapping("/hzero/v2")
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    //
    // 角色查询
    // ------------------------------------------------------------------------------

    @ApiOperation("角色管理 - 根据父级角色查询子孙角色，只返回行上需要展示的信息")
    @Permission(permissionLogin = true)
    @GetMapping("/roles/self/manageable-roles")
    public ResponseEntity<Page<RoleVO>> listManageableRoles(@Encrypt RoleVO params,
                                                                PageRequest pageRequest) {
        return Results.success(roleService.selectManageableRoles(params, pageRequest));
    }

    @ApiOperation("查询成员可分配的角色")
    @Permission(permissionLogin = true)
    @GetMapping("/roles/self/assignable-roles")
    public ResponseEntity<Page<RoleVO>> listMemberAssignableRoles(@Encrypt RoleVO params, PageRequest pageRequest) {
        return Results.success(roleService.selectMemberAssignableRoles(params, pageRequest));
    }

    @ApiOperation("角色管理 - 查询树形角色")
    @Permission(permissionLogin = true)
    @GetMapping("/roles/self/manageable-roles/tree")
    public ResponseEntity<Page<RoleVO>> treeManageableRoles(@Encrypt RoleVO params, PageRequest pageRequest) {
        return Results.success(roleService.selectTreeManageableRoles(params, pageRequest));
    }

}
