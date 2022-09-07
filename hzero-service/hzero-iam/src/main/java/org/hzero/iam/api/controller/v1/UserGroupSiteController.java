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
 * 平台级 - 用户组管理 API
 *
 * @author xiaoyu.zhao@hand-china.com 2019/01/14 20:55
 */
@Api(tags = {SwaggerApiConfig.USER_GROUP_SITE})
@RestController("userGroupSiteController.v1")
@RequestMapping("/v1/user-groups")
public class UserGroupSiteController extends BaseController {
    @Autowired
    private UserGroupRepository userGroupRepository;
    @Autowired
    private UserGroupService userGroupService;

    @ApiOperation(value = "用户组列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<Page<UserGroup>> pageUserGroupList(@Encrypt UserGroup userGroup, PageRequest pageRequest) {
        return Results.success(userGroupRepository.pageUserGroup(pageRequest, userGroup));
    }

    @ApiOperation(value = "用户组明细")
    @ApiImplicitParam(name = "userGroupId", value = "用户组Id", paramType = "path")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/details/{userGroupId}")
    public ResponseEntity<UserGroup> getUserGroupDetails(@PathVariable @Encrypt Long userGroupId) {
        return Results.success(userGroupRepository.selectUserGroupDetails(userGroupId));
    }

    @ApiOperation(value = "创建用户组")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<UserGroup> createUserGroup(@RequestBody @Encrypt UserGroup userGroup) {
        this.validObject(userGroup);
        return Results.success(userGroupService.createUserGroup(userGroup));
    }

    @ApiOperation(value = "修改用户组")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<UserGroup> updateUserGroup(@RequestBody @Encrypt UserGroup userGroup) {
        SecurityTokenHelper.validToken(userGroup);
        this.validObject(userGroup);
        return Results.success(userGroupService.updateUserGroup(userGroup));
    }

    @ApiOperation(value = "删除用户组")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity removeUserGroup(@RequestBody @Encrypt UserGroup userGroup) {
        SecurityTokenHelper.validToken(userGroup);
        userGroupService.removeUserGroup(userGroup);
        return Results.success();
    }
}
