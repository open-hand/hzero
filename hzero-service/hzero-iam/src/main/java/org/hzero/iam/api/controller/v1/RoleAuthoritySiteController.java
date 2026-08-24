package org.hzero.iam.api.controller.v1;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.api.dto.RoleAuthorityDTO;
import org.hzero.iam.app.service.RoleAuthorityService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.RoleAuthority;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 角色数据权限定义 管理 API
 *
 * @author mingke.yan@hand-china.com 2018-08-07 16:30:26
 */
@Api(tags = SwaggerApiConfig.ROLE_AUTHORITY_SITE)
@RestController("roleAuthoritySiteController.v1")
@RequestMapping("/v1/roles/{roleId}/role-auths")
public class RoleAuthoritySiteController extends BaseController {

    @Autowired
    private RoleAuthorityService roleAuthorityService;

    @ApiOperation(value = "角色数据权限定义列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @CustomPageRequest
    @GetMapping("/{organizationId}")
    public ResponseEntity<Page<RoleAuthorityDTO>> list(
            @Encrypt @ApiParam(value = "角色ID",required = true) @PathVariable("roleId") Long roleId,
            @ApiParam(value = "租户ID",required = true) @PathVariable("organizationId") Long organizationId,
            @ApiParam("单据类型名称") @RequestParam(required = false) String docTypeName,
            @ApiParam("单据类型编码") @RequestParam(required = false) String docTypeCode,
            @ApiIgnore @SortDefault(value = RoleAuthority.FIELD_ROLE_AUTH_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(roleAuthorityService.pageList(pageRequest, roleId,organizationId,docTypeName,docTypeCode));
    }

    @ApiOperation(value = "批量新增或保存角色数据权限定义明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<List<RoleAuthorityDTO>> batchCreateOrUpdateRoleAuthority(@Encrypt @PathVariable Long roleId,@RequestBody List<RoleAuthorityDTO> roleAuthorityDtos) {
        roleAuthorityService.batchCreateOrUpdateRoleAuthority(roleId,roleAuthorityDtos);
        return Results.success(roleAuthorityDtos);
    }

    @ApiOperation(value = "删除角色数据权限定义")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<Void> deleteRoleAuthority(@PathVariable("roleId") @Encrypt Long roleId, @RequestParam @Encrypt Long roleAuthId) {
        roleAuthorityService.deleteRoleAuthority(roleAuthId);
        return Results.success();
    }


}
