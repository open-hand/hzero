package org.hzero.platform.api.controller.v1;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.app.service.PermissionRangeService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.PermissionRange;
import org.hzero.platform.domain.repository.PermissionRangeRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 屏蔽范围 管理 API
 *
 * @author yunxiang.zhou01@hand-china.com 2018-07-23 15:01:44
 */
@Api(tags = PlatformSwaggerApiConfig.PERMISSION_RANGE_SITE)
@RestController("permissionRangeSiteController.v1")
@RequestMapping("/v1/permission-ranges")
public class PermissionRangeSiteController extends BaseController {

    @Autowired
    private PermissionRangeRepository permissionRangeRepository;
    @Autowired
    private PermissionRangeService permissionRangeService;

    @ApiOperation(value = "屏蔽范围列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity list(PermissionRange permissionRange,
                               @ApiIgnore @SortDefault(value = PermissionRange.FIELD_RANGE_ID,
                                       direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(permissionRangeRepository.selectPermissionRange(pageRequest, permissionRange));
    }

    @ApiOperation(value = "创建屏蔽范围")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity create(@RequestBody PermissionRange permissionRange) {
        validObject(permissionRange);
        return Results.success(permissionRangeRepository.insertPermissionRange(permissionRange));
    }

    @ApiOperation(value = "修改屏蔽范围")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity update(@RequestBody @Encrypt PermissionRange permissionRange) {
        SecurityTokenHelper.validTokenIgnoreInsert(permissionRange);
        return Results.success(permissionRangeService.updatePermissionRange(permissionRange));
    }

    @ApiOperation(value = "删除屏蔽范围")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    @ApiImplicitParams({@ApiImplicitParam(name = "rangeId", value = "范围ID", paramType = "path", required = true)})
    public ResponseEntity remove(@RequestBody @Encrypt PermissionRange permissionRange) {
        SecurityTokenHelper.validTokenIgnoreInsert(permissionRange);
        permissionRangeRepository.deletePermissionRange(permissionRange.getRangeId(), permissionRange.getTenantId());
        return Results.success();
    }

}
