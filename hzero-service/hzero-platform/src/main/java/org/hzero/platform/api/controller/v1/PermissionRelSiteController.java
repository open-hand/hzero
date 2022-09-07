package org.hzero.platform.api.controller.v1;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.PermissionRel;
import org.hzero.platform.domain.repository.PermissionRelRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 屏蔽范围规则关系 管理 API
 *
 * @author yunxiang.zhou01@hand-china.com 2018-08-29 15:19:45
 */
@Api(tags = PlatformSwaggerApiConfig.PERMISSION_REL_SITE)
@RestController("permissionRelSiteController.v1")
@RequestMapping("/v1/permission-rel")
public class PermissionRelSiteController extends BaseController {

    @Autowired
    private PermissionRelRepository permissionRelRepository;

    @ApiOperation(value = "屏蔽范围规则关系列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{rangeId}")
    public ResponseEntity list(@PathVariable @Encrypt Long rangeId) {
        return Results.success(permissionRelRepository.selectPermissionRuleByRangeId(rangeId));
    }

    @ApiOperation(value = "创建屏蔽范围规则关系")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity create(@RequestBody @Encrypt PermissionRel permissionRel) {
        this.validObject(permissionRel);
        permissionRelRepository.insertPermissionRel(permissionRel);
        return Results.success(permissionRel);
    }

    @ApiOperation(value = "修改屏蔽范围规则关系")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity update(@RequestBody @Encrypt PermissionRel permissionRel) {
        SecurityTokenHelper.validTokenIgnoreInsert(permissionRel);
        permissionRelRepository.updatePermissionRel(permissionRel);
        return Results.success(permissionRel);
    }

    @ApiOperation(value = "删除屏蔽范围规则关系")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity remove(@RequestBody @Encrypt PermissionRel permissionRel) {
        SecurityTokenHelper.validTokenIgnoreInsert(permissionRel);
        permissionRelRepository.deletePermissionRel(permissionRel.getPermissionRelId());
        return Results.success();
    }
}
