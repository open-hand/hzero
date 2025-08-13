package org.hzero.iam.api.controller.v1;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

import org.hzero.core.util.Results;
import org.hzero.iam.api.dto.AccessAuthDTO;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.Menu;
import org.hzero.iam.domain.repository.MenuRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author jianbo.li
 * @date 2019/10/20 12:07
 */
@Api(tags = SwaggerApiConfig.ACCESS_AUTH_MANAGER)
@RestController("accessAuthManagerController.v1")
@RequestMapping("/v1/{organizationId}/access-auth-manager")
public class AccessAuthManagerController {

    @Autowired
    private MenuRepository menuRepository;

    @ApiOperation(value = "分页查询已菜单已分配的角色列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{menuId}/page-assign-role")
    @CustomPageRequest
    public ResponseEntity<Page<AccessAuthDTO>> pageMenuAssignedRole(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
                                                                    @Encrypt @PathVariable("menuId") Long menuId,
                                                                    @Encrypt Menu menu,
                                                                    @ApiIgnore PageRequest pageRequest) {
        return Results.success(menuRepository.pageMenuAssignRole(organizationId, menuId, menu, pageRequest));
    }

    @ApiOperation(value = "查询菜单权限树")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{menuId}/tree")
    @CustomPageRequest
    public ResponseEntity<List<Menu>> menuTree(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
                                               @Encrypt @PathVariable("menuId") Long menuId) {
        return Results.success(menuRepository.queryMenuTree(organizationId, menuId));
    }
}
