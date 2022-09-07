package org.hzero.iam.api.controller.v1;

import java.util.List;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.app.service.UserGroupAssignService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.UserGroup;
import org.hzero.iam.domain.entity.UserGroupAssign;
import org.hzero.iam.domain.repository.UserGroupAssignRepository;
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
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 用户组分配 管理 API 平台级
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-15 16:06:11
 */
@Api(tags = {SwaggerApiConfig.USER_GROUP_ASSIGN_SITE})
@RestController("userGroupAssignSiteController.v1")
@RequestMapping("/v1")
public class UserGroupSiteAssignController extends BaseController {

    @Autowired
    private UserGroupAssignRepository userGroupAssignRepository;
    @Autowired
    private UserGroupAssignService userGroupAssignService;

    @ApiOperation(value = "用户组分配列表")
    @Permission(level = ResourceLevel.SITE)
    @ApiImplicitParam(name = "userId", value = "用户Id", paramType = "path")
    @CustomPageRequest
    @GetMapping("/{userId}/user-group-assigns")
    public ResponseEntity<Page<UserGroupAssign>> pageUserGroupAssign(
            @PathVariable(name = "userId") @Encrypt Long userId,
            @Encrypt UserGroupAssign userGroupAssign,
            @ApiIgnore @SortDefault(value = UserGroupAssign.FIELD_USER_GROUP_ID,
                    direction = Sort.Direction.DESC) PageRequest pageRequest) {
        userGroupAssign.setUserId(userId);
        return Results.success(userGroupAssignRepository.pageUserGroupAssign(pageRequest, userGroupAssign));
    }

    @ApiOperation(value = "用户分配列表")
    @Permission(level = ResourceLevel.SITE)
    @ApiImplicitParams({@ApiImplicitParam(name = "userGroupId", value = "用户组Id", paramType = "path", required = true)})
    @CustomPageRequest
    @GetMapping("/user-groups/{userGroupId}/users")
    public ResponseEntity<Page<UserGroupAssign>> pageUserGroupAssignUsers(
            @PathVariable(name = "userGroupId") @Encrypt Long userGroupId,
            @Encrypt UserGroupAssign userGroupAssign,
            @ApiIgnore @SortDefault(value = UserGroupAssign.FIELD_USER_GROUP_ID,
                    direction = Sort.Direction.DESC) PageRequest pageRequest) {
        userGroupAssign.setUserGroupId(userGroupId);
        return Results.success(userGroupAssignRepository.pageUserGroupAssign(pageRequest, userGroupAssign));
    }

    @ApiOperation(value = "创建用户组分配")
    @Permission(level = ResourceLevel.SITE)
    @ApiImplicitParam(name = "userId", value = "用户Id", paramType = "path")
    @PostMapping("/{userId}/user-group-assigns")
    public ResponseEntity<List<UserGroupAssign>> createUserGroupAssign(
            @PathVariable @Encrypt Long userId,
            @RequestBody @Encrypt List<UserGroupAssign> userGroupAssignList) {
        userGroupAssignList.forEach((userGroupAssign) -> userGroupAssign.setUserId(userId));
        this.validObject(userGroupAssignList);
        return Results.success(userGroupAssignService.createOrUpdateUserGroupAssign(userGroupAssignList));
    }

    @ApiOperation(value = "创建用户分配")
    @Permission(level = ResourceLevel.SITE)
    @ApiImplicitParams({@ApiImplicitParam(name = "userGroupId", value = "用户组Id", paramType = "path"),
            @ApiImplicitParam(name = "tenantId", value = "租户Id", paramType = "query")})
    @PostMapping("/user-groups/{userGroupId}/users")
    public ResponseEntity<List<UserGroupAssign>> createUserGroupAssignUsers(
            @RequestParam("tenantId") Long tenantId,
            @RequestBody @Encrypt List<UserGroupAssign> userGroupAssignList,
            @PathVariable("userGroupId") @Encrypt Long userGroupId) {
        userGroupAssignList.forEach((userGroupAssign) -> {
            userGroupAssign.setUserGroupId(userGroupId);
            userGroupAssign.setTenantId(tenantId);
        });
        return Results.success(userGroupAssignService.createOrUpdateUserGroupAssign(userGroupAssignList));
    }

    @ApiOperation(value = "删除用户组分配")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping("/{userId}/user-group-assigns")
    public ResponseEntity removeUserGroupAssign(
            @RequestBody @Encrypt List<UserGroupAssign> userGroupAssignList,
            @PathVariable("userId") @Encrypt Long userId) {
        SecurityTokenHelper.validToken(userGroupAssignList);
        userGroupAssignService.removeUserGroupAssign(userGroupAssignList);
        return Results.success();
    }

    @ApiOperation(value = "删除用户分配")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping("/user-groups/{userGroupId}/users")
    public ResponseEntity removeUserGroupAssignUsers(
            @RequestBody @Encrypt List<UserGroupAssign> userGroupAssignList,
            @PathVariable("userGroupId") @Encrypt Long userGroupId) {
        SecurityTokenHelper.validToken(userGroupAssignList);
        userGroupAssignService.removeUserGroupAssign(userGroupAssignList);
        return Results.success();
    }

    @ApiOperation(value = "查询排除已经分配的用户组列表")
    @Permission(level = ResourceLevel.SITE)
    @CustomPageRequest
    @GetMapping("/{userId}/user-group-assigns/exclude-groups")
    public ResponseEntity<Page<UserGroup>> listExcludeUserGroups(
            @PathVariable(name = "userId") @Encrypt Long userId,
            @Encrypt UserGroupAssign userGroupAssign, PageRequest pageRequest) {
        userGroupAssign.setUserId(userId);
        return Results.success(userGroupAssignRepository.selectExcludeUserGroups(userGroupAssign, pageRequest));
    }

    @ApiOperation(value = "查询排除已经分配的用户列表")
    @Permission(level = ResourceLevel.SITE)
    @ApiImplicitParams({@ApiImplicitParam(name = "tenantId", value = "租户Id", paramType = "query"),
            @ApiImplicitParam(name = "userGroupId", value = "用户组Id", paramType = "path")})
    @CustomPageRequest
    @GetMapping("/user-groups/{userGroupId}/users/exclude-users")
    public ResponseEntity<Page<UserGroupAssign>> listExcludeUsers(
            @PathVariable(name = "userGroupId") @Encrypt Long userGroupId,
            @Encrypt UserGroupAssign userGroupAssign, PageRequest pageRequest) {
        userGroupAssign.setUserId(userGroupId);
        return Results.success(userGroupAssignRepository.selectExcludeUsers(userGroupAssign, pageRequest));
    }

}
