package org.hzero.iam.api.controller.v1;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.app.service.UserGroupService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.UserGroup;
import org.hzero.iam.domain.repository.UserGroupRepository;
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
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * 租户级 - 用户组管理 API
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-14 13:44:56
 */
@Api(tags = {SwaggerApiConfig.USER_GROUP})
@RestController("userGroupController.v1")
@RequestMapping("/v1/{organizationId}/user-groups")
public class UserGroupController extends BaseController {

    @Autowired
    private UserGroupRepository userGroupRepository;
    @Autowired
    private UserGroupService userGroupService;

    @ApiOperation(value = "用户组列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<UserGroup>> pageUserGroup(
            @PathVariable(name = "organizationId") Long organizationId,
            @Encrypt UserGroup userGroup, PageRequest pageRequest) {
        userGroup.setTenantId(organizationId);
        return Results.success(userGroupRepository.pageUserGroup(pageRequest, userGroup));
    }

    @ApiOperation(value = "用户组明细")
    @ApiImplicitParam(name = "userGroupId", value = "用户组Id", paramType = "path")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/details/{userGroupId}")
    public ResponseEntity<UserGroup> userGroupDetails(@PathVariable @Encrypt Long userGroupId) {
        return Results.success(userGroupRepository.selectUserGroupDetails(userGroupId));
    }

    @ApiOperation(value = "创建用户组")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<UserGroup> createUserGroup(@RequestBody @Encrypt UserGroup userGroup, @PathVariable Long organizationId) {
        userGroup.setTenantId(organizationId);
        this.validObject(userGroup);
        return Results.success(userGroupService.createUserGroup(userGroup));
    }

    @ApiOperation(value = "修改用户组")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<UserGroup> updateUserGroup(@RequestBody @Encrypt UserGroup userGroup, @PathVariable Long organizationId) {
        userGroup.setTenantId(organizationId);
        SecurityTokenHelper.validToken(userGroup);
        this.validObject(userGroup);
        return Results.success(userGroupService.updateUserGroup(userGroup));
    }

    @ApiOperation(value = "删除用户组")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity removeUserGroup(@RequestBody @Encrypt UserGroup userGroup) {
        SecurityTokenHelper.validToken(userGroup);
        userGroupService.removeUserGroup(userGroup);
        return Results.success();
    }
}
