package org.hzero.iam.api.controller.v1;

import java.util.List;
import java.util.Set;

import org.hzero.boot.message.entity.Receiver;
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
 * 用户组分配 管理 API 租户级
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-15 16:06:11
 */
@Api(tags = {SwaggerApiConfig.USER_GROUP_ASSIGN})
@RestController("userGroupAssignController.v1")
@RequestMapping("/v1")
public class UserGroupAssignController extends BaseController {

    @Autowired
    private UserGroupAssignRepository userGroupAssignRepository;
    @Autowired
    private UserGroupAssignService userGroupAssignService;

    @ApiOperation(value = "用户组分配列表")
    @ApiImplicitParams({@ApiImplicitParam(name = "userId", value = "用户Id", paramType = "path")})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/{userId}/user-group-assigns")
    @CustomPageRequest
    public ResponseEntity<Page<UserGroupAssign>> pageUserGroupAssign(
            @PathVariable Long organizationId,
            @PathVariable @Encrypt Long userId,
            @Encrypt UserGroupAssign userGroupAssign,
            @ApiIgnore @SortDefault(value = UserGroupAssign.FIELD_USER_GROUP_ID,
                    direction = Sort.Direction.DESC) PageRequest pageRequest) {
        userGroupAssign.setTenantId(organizationId);
        userGroupAssign.setUserId(userId);
        return Results.success(userGroupAssignRepository.pageUserGroupAssign(pageRequest, userGroupAssign));
    }

    @ApiOperation(value = "用户分配列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiImplicitParams({@ApiImplicitParam(name = "userGroupId", value = "用户组Id", paramType = "path")})
    @CustomPageRequest
    @GetMapping("/{organizationId}/user-groups/{userGroupId}/users")
    public ResponseEntity<Page<UserGroupAssign>> pageUserGroupAssignUsers(
            @PathVariable(name = "userGroupId") @Encrypt Long userGroupId,
            @Encrypt UserGroupAssign userGroupAssign,
            @ApiIgnore @SortDefault(value = UserGroupAssign.FIELD_USER_GROUP_ID,
                    direction = Sort.Direction.DESC) PageRequest pageRequest) {
        userGroupAssign.setUserGroupId(userGroupId);
        return Results.success(userGroupAssignRepository.pageUserGroupAssign(pageRequest, userGroupAssign));
    }

    @ApiOperation(value = "获取用户组下的用户Id及用户所属租户Id-用于消息公告管理界面")
    @Permission(permissionWithin = true)
    @PostMapping("/user-groups/user-ids")
    public ResponseEntity<Set<Receiver>> listUserGroupAssignUsers(
            @RequestBody @Encrypt List<UserGroupAssign> userGroupAssigns,
            @RequestParam(required = false) List<String> typeCode) {
        return Results.success(userGroupAssignRepository.listUserGroupAssign(userGroupAssigns, typeCode));
    }

    @ApiOperation(value = "获取用户组下的用户对应三方平台ID")
    @Permission(permissionWithin = true)
    @PostMapping("/user-groups/open/user-ids")
    public ResponseEntity<Set<Receiver>> listHrUserGroupAssignUsers(
            @RequestBody @Encrypt List<UserGroupAssign> userGroupAssigns,
            String thirdPlatformType) {
        return Results.success(userGroupAssignService.listOpenUserGroupAssign(userGroupAssigns, thirdPlatformType));
    }

    @ApiOperation(value = "创建用户组分配")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiImplicitParams({@ApiImplicitParam(name = "organizationId", value = "租户Id", paramType = "path"),
            @ApiImplicitParam(name = "userId", value = "用户Id", paramType = "path")})
    @PostMapping("/{organizationId}/{userId}/user-group-assigns")
    public ResponseEntity<List<UserGroupAssign>> createUserGroupAssign(
            @RequestBody @Encrypt List<UserGroupAssign> userGroupAssignList,
            @PathVariable(name = "organizationId") Long organizationId,
            @PathVariable(name = "userId") @Encrypt Long userId) {
        userGroupAssignList.forEach((userGroupAssign) -> {
            userGroupAssign.setTenantId(organizationId);
            userGroupAssign.setUserId(userId);
        });
        this.validObject(userGroupAssignList);
        return Results.success(userGroupAssignService.createOrUpdateUserGroupAssign(userGroupAssignList));
    }

    @ApiOperation(value = "创建用户分配")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiImplicitParams({@ApiImplicitParam(name = "organizationId", value = "租户Id", paramType = "path"),
            @ApiImplicitParam(name = "userGroupId", value = "用户组Id", paramType = "path")})
    @PostMapping("/{organizationId}/user-groups/{userGroupId}/users")
    public ResponseEntity<List<UserGroupAssign>> createUserGroupAssignUsers(
            @RequestBody @Encrypt List<UserGroupAssign> userGroupAssignList,
            @PathVariable(name = "organizationId") Long organizationId,
            @PathVariable("userGroupId") @Encrypt Long userGroupId) {
        userGroupAssignList.forEach((userGroupAssign) -> {
            userGroupAssign.setTenantId(organizationId);
            userGroupAssign.setUserGroupId(userGroupId);
        });
        return Results.success(userGroupAssignService.createOrUpdateUserGroupAssign(userGroupAssignList));
    }

    @ApiOperation(value = "删除用户组分配")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/{organizationId}/{userId}/user-group-assigns")
    public ResponseEntity removeUserGroupAssign(@RequestBody @Encrypt List<UserGroupAssign> userGroupAssignList,
                                                @PathVariable("userId") @Encrypt Long userId) {
        SecurityTokenHelper.validToken(userGroupAssignList);
        userGroupAssignService.removeUserGroupAssign(userGroupAssignList);
        return Results.success();
    }

    @ApiOperation(value = "删除用户分配")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/{organizationId}/user-groups/{userGroupId}/users")
    public ResponseEntity removeUserGroupAssignUsers(
            @RequestBody @Encrypt List<UserGroupAssign> userGroupAssignList,
            @PathVariable("userGroupId") @Encrypt Long userGroupId) {
        SecurityTokenHelper.validToken(userGroupAssignList);
        userGroupAssignService.removeUserGroupAssign(userGroupAssignList);
        return Results.success();
    }

    @ApiOperation(value = "查询排除已经分配的用户组列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @CustomPageRequest
    @GetMapping("/{organizationId}/{userId}/user-group-assigns/exclude-groups")
    public ResponseEntity<Page<UserGroup>> listExcludeUserGroups(
            @PathVariable(name = "userId") @Encrypt Long userId,
            @PathVariable(name = "organizationId") Long organizationId,
            @Encrypt UserGroupAssign userGroupAssign,
            PageRequest pageRequest) {
        userGroupAssign.setUserId(userId);
        userGroupAssign.setTenantId(organizationId);
        return Results.success(userGroupAssignRepository.selectExcludeUserGroups(userGroupAssign, pageRequest));
    }

    @ApiOperation(value = "查询排除已经分配的用户列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiImplicitParams({@ApiImplicitParam(name = "organizationId", value = "租户Id", paramType = "path"),
            @ApiImplicitParam(name = "userGroupId", value = "用户组Id", paramType = "path")})
    @CustomPageRequest
    @GetMapping("/{organizationId}/user-groups/{userGroupId}/users/exclude-users")
    public ResponseEntity<Page<UserGroupAssign>> listExcludeUsers(
            @PathVariable(name = "userGroupId") @Encrypt Long userGroupId,
            @PathVariable("organizationId") Long organizationId,
            @Encrypt UserGroupAssign userGroupAssign,
            PageRequest pageRequest) {
        userGroupAssign.setUserId(userGroupId);
        userGroupAssign.setTenantId(organizationId);
        return Results.success(userGroupAssignRepository.selectExcludeUsers(userGroupAssign, pageRequest));
    }


}
