package org.hzero.iam.api.controller.v1;

import java.util.List;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.api.dto.SecGrpPermissionSearchDTO;
import org.hzero.iam.app.service.SecGrpAclService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.Menu;
import org.hzero.iam.domain.repository.SecGrpAclRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 安全组访问权限 管理 API
 *
 * @author bojiangzhou 2020/02/19 代码优化
 * @author xingxing.wu@hand-china.com 2019-10-17 11:26:19
 */
@Api(tags = SwaggerApiConfig.SEC_GRP_ACL_SITE)
@RestController("secGrpAclSiteController.v1")
@RequestMapping("/v1/sec-grp-acls/{secGrpId}")
public class SecGrpAclSiteController extends BaseController {

    @Autowired
    private SecGrpAclService aclService;
    @Autowired
    private SecGrpAclRepository aclRepository;

    @ApiOperation(value = "平台层-安全组权限维护-查询可为指定安全组分配的访问权限列表树")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<List<Menu>> listAssignableAcl(
            @ApiParam(value = "安全组ID") @PathVariable("secGrpId") @Encrypt Long secGrpId) {
        return Results.success(aclRepository.listSecGrpAssignableAcl(null, secGrpId));
    }

    @ApiOperation(value = "租户层-安全组权限维护-查询已分配的权限集树")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/assigned")
    public ResponseEntity<List<Menu>> listAssignedAcl(
            @ApiParam(value = "安全组ID") @PathVariable("secGrpId") @Encrypt Long secGrpId,
            @Encrypt SecGrpPermissionSearchDTO queryDTO) {

        return Results.success(aclRepository.listSecGrpAssignedAcl(null, secGrpId, queryDTO));
    }

    @ApiOperation(value = "平台层-安全组权限维护-新增安全组访问权限")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<Void> create(
            @ApiParam(value = "安全组ID") @PathVariable("secGrpId") @Encrypt Long secGrpId,
            @RequestBody @Encrypt List<Long> permissionIds) {
        aclService.createSecGrpAcl(null, secGrpId, permissionIds);
        return Results.success();
    }

    @ApiOperation(value = "平台层-安全组权限维护-删除安全组访问权限")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity<Void> delete(
            @ApiParam(value = "安全组ID") @PathVariable("secGrpId") @Encrypt Long secGrpId,
            @RequestBody @Encrypt List<Long> permissionIds) {
        aclService.deleteSecGrpAcl(null, secGrpId, permissionIds);
        return Results.success();
    }

}
