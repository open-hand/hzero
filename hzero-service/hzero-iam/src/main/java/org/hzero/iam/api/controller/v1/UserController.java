package org.hzero.iam.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

import org.hzero.boot.message.entity.Receiver;

import io.swagger.annotations.*;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.hzero.iam.api.dto.*;
import org.hzero.iam.app.service.UserService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.domain.vo.UserVO;
import org.hzero.iam.infra.common.utils.AssertUtils;
import org.hzero.iam.infra.constant.SecCheckType;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

/**
 * 租户层用户接口
 *
 * @author bojiangzhou 2018/07/05
 */
@Api(tags = SwaggerApiConfig.USER)
@RestController("userInfoController.v1")
@RequestMapping("/hzero/v1")
@SuppressWarnings("rawtypes")
public class UserController extends BaseController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    //
    // 用户管理接口
    // ------------------------------------------------------------------------------


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "用户管理 - 分页查询跨租户用户简要信息")
    @GetMapping("/{organizationId}/users/paging/all")
    public ResponseEntity<Page<UserVO>> pagingAll(@PathVariable Long organizationId,
                                                  @Encrypt UserVO user,
                                                  @SortDefault(value = User.FIELD_ID) PageRequest pageRequest) {
        return Results.success(userRepository.selectAllocateUsers(user, pageRequest));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "用户管理 - 分页查询用户简要信息")
    @GetMapping("/{organizationId}/users/paging")
    public ResponseEntity<Page<UserVO>> paging(@PathVariable Long organizationId,
                                               @Encrypt UserVO user,
                                               @SortDefault(value = User.FIELD_ID) PageRequest pageRequest) {
        user.setOrganizationId(organizationId);
        return Results.success(userRepository.selectSimpleUsers(user, pageRequest));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "用户管理 - 根据用户ID查询用户详细信息")
    @GetMapping("/{organizationId}/users/{userId}/info")
    public ResponseEntity<UserVO> selectUserDetail(@PathVariable Long organizationId,
                                                   @PathVariable @Encrypt Long userId) {
        UserVO params = new UserVO();
        // 限制所属租户
        params.setOrganizationId(organizationId);
        params.setId(userId);
        return Results.success(userRepository.selectUserDetails(params));
    }

    @ApiOperation(value = "用户查询 - 查询多租户下的用户信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/users/multi-tenant-list")
    @CustomPageRequest
    public ResponseEntity<Page<UserVO>> listMultiTenantUsers(@PathVariable("organizationId") Long organizationId,
                                                             @ModelAttribute @Encrypt UserVO params,
                                                             PageRequest pageRequest) {
        return Results.success(userRepository.selectMultiTenantUsers(params, pageRequest));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "用户管理 - 创建用户")
    @PostMapping("/{organizationId}/users")
    public ResponseEntity<User> createUser(@PathVariable Long organizationId,
                                           @RequestBody @Encrypt User user) {
        user.setOrganizationId(organizationId);
        validObject(user);
        return Results.success(userService.createUser(user));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "用户管理 - 修改用户")
    @PutMapping("/{organizationId}/users")
    public ResponseEntity<User> updateUser(@PathVariable Long organizationId,
                                           @RequestBody @Encrypt User user) {
        user.setOrganizationId(organizationId);
        SecurityTokenHelper.validToken(user, false);
        return Results.success(userService.updateUser(user));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "用户管理 - 锁定用户")
    @PostMapping("/{organizationId}/users/{userId}/locked")
    public ResponseEntity lockUser(@PathVariable Long organizationId,
                                   @PathVariable @Encrypt Long userId) {
        userService.lockUser(userId, organizationId);
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "用户管理 - 解锁用户")
    @PostMapping("/{organizationId}/users/{userId}/unlocked")
    public ResponseEntity unlockUser(@PathVariable Long organizationId,
                                     @PathVariable @Encrypt Long userId) {
        userService.unlockUser(userId, organizationId);
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "用户管理 - 禁用用户")
    @PostMapping("/{organizationId}/users/{userId}/frozen")
    public ResponseEntity frozenUser(@PathVariable Long organizationId,
                                     @PathVariable @Encrypt Long userId) {
        userService.frozenUser(userId, organizationId);
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "用户管理 - 启用用户")
    @PostMapping("/{organizationId}/users/{userId}/unfrozen")
    public ResponseEntity unfrozenUser(@PathVariable Long organizationId,
                                       @PathVariable @Encrypt Long userId) {
        userService.unfrozenUser(userId, organizationId);
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "用户管理 - 修改用户密码")
    @PutMapping(value = "/{organizationId}/users/{userId}/admin-password")
    public ResponseEntity updateUserPassword(@PathVariable @Encrypt Long userId,
                                             @PathVariable Long organizationId,
                                             @RequestBody UserPasswordDTO userPassword) {
        userService.updateUserPassword(userId, organizationId, userPassword);
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "用户管理 - 重置用户密码")
    @PutMapping(value = "/{organizationId}/users/{userId}/admin-password-reset")
    public ResponseEntity resetUserPassword(@PathVariable @Encrypt Long userId,
                                            @PathVariable Long organizationId,
                                            @RequestBody UserPasswordDTO userPassword) {
        userService.resetUserPassword(userId, organizationId, userPassword);
        return Results.success();
    }

    @ApiOperation(value = "用户管理 - 导出用户信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/users/export")
    @ExcelExport(UserExportDTO.class)
    public ResponseEntity<List<UserExportDTO>> export(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
            @ApiParam("用户权限类型查询条件(csv)") @RequestParam(value = "authorityTypeQueryParams", required = false) String authorityTypeQueryParams,
            @Encrypt UserVO user,
            ExportParam exportParam,
            HttpServletResponse response,
            PageRequest pageRequest) {
        user.setOrganizationId(organizationId);
        List<UserExportDTO> results = this.userRepository.exportUserInfo(user, authorityTypeQueryParams, pageRequest, exportParam);
        return Results.success(results);
    }

    @Deprecated
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据用户名或邮箱或手机查询用户信息")
    @GetMapping("/{organizationId}/users")
    public ResponseEntity<UserVO> queryByLoginNameOrEmailOrPhone(
            @PathVariable Long organizationId,
            @RequestParam(name = "condition") String condition,
            @RequestParam(name = "userType", defaultValue = User.DEFAULT_USER_TYPE) String userType) {
        UserVO params = new UserVO();
        params.setCondition(condition);
        params.setOrganizationId(organizationId);
        params.setUserType(userType);
        UserVO user = userRepository.selectByLoginNameOrEmailOrPhone(params);
        AssertUtils.notNull(user, "user.account.not-exists", condition);
        return Results.success(user);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("根据用户姓名/邮箱查询用户列表")
    @GetMapping("/{organizationId}/users/real-name")
    public ResponseEntity<List<UserVO>> queryByRealNameOrEmail(
            @PathVariable Long organizationId,
            @RequestParam(name = "realName") String condition,
            @RequestParam(name = "userType", defaultValue = User.DEFAULT_USER_TYPE) String userType) {
        UserVO params = new UserVO();
        params.setCondition(condition);
        params.setOrganizationId(organizationId);
        params.setUserType(userType);
        return Results.success(userRepository.selectByRealNameOrEmail(params));
    }

    @ApiOperation(value = "员工分配列表")
    @ApiImplicitParams({@ApiImplicitParam(name = "userId", value = "用户Id", paramType = "path")})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/users/{userId}/user-employee-assigns")
    @CustomPageRequest
    public ResponseEntity<Page<UserEmployeeAssignDTO>> pageUserEmployeeAssign(
            @PathVariable Long organizationId,
            @PathVariable @Encrypt Long userId,
            @Encrypt UserEmployeeAssignDTO params,
            @ApiIgnore PageRequest pageRequest) {
        return Results.success(userService.pageUserEmployeeAssign(pageRequest, organizationId, userId, params));
    }

    @ApiOperation(value = "根据用户ID获取消息接收人 - 内部调用")
    @Permission(permissionWithin = true)
    @GetMapping("/{organizationId}/users/by-ids")
    public ResponseEntity<List<Receiver>> listReceiverByUserIds(@PathVariable("organizationId") Long organizationId,
                                                                @RequestParam("userIds") List<Long> userIds) {
        return Results.success(userService.listReceiverByUserIds(organizationId, userIds));
    }

    @ApiOperation(value = "根据用户ID获取第三方消息接收人 - 内部调用")
    @Permission(permissionWithin = true)
    @GetMapping("/{organizationId}/open-users/by-ids")
    public ResponseEntity<List<Receiver>> listOpenReceiverByUserIds(@PathVariable("organizationId") Long organizationId,
                                                                    @RequestParam("userIds") List<Long> userIds,
                                                                    @RequestParam("thirdPlatformType") String thirdPlatformType) {
        return Results.success(userService.listOpenReceiverByUserIds(organizationId, userIds, thirdPlatformType));
    }

    @ApiOperation(value = "二次校验的用户列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/users/sec-check")
    public ResponseEntity<Page<UserSecCheckDTO>> listEnabledSecondaryCheck(@PathVariable Long organizationId,
                                                                           UserSecCheckSearchDTO searchDTO,
                                                                           @ApiIgnore PageRequest pageRequest) {
        searchDTO.setTenantId(organizationId);
        return Results.success(this.userService.pageSecondaryCheck(searchDTO, pageRequest));
    }

    @ApiOperation(value = "开启指定用户手机二次校验")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/{organizationId}/users/sec-check/enable/phone")
    public ResponseEntity<Void> enablePhoneSecCheck(@PathVariable Long organizationId,
                                                    @RequestBody @Encrypt Set<Long> userIds) {
        this.userService.enableSecCheck(organizationId, userIds, SecCheckType.PHONE);
        return Results.success();
    }

    @ApiOperation(value = "开启指定用户邮箱二次校验")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/{organizationId}/users/sec-check/enable/email")
    public ResponseEntity<Void> enableEmailSecCheck(@PathVariable Long organizationId,
                                                    @RequestBody @Encrypt Set<Long> userIds) {
        this.userService.enableSecCheck(organizationId, userIds, SecCheckType.EMAIL);
        return Results.success();
    }

    @ApiOperation(value = "关闭指定用户手机二次校验")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/{organizationId}/users/sec-check/disable/phone")
    public ResponseEntity<Void> disablePhoneSecCheck(@PathVariable Long organizationId,
                                                     @RequestBody @Encrypt Set<Long> userIds) {
        this.userService.disableSecCheck(organizationId, userIds, SecCheckType.PHONE);
        return Results.success();
    }

    @ApiOperation(value = "关闭指定用户邮箱二次校验")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/{organizationId}/users/sec-check/disable/email")
    public ResponseEntity<Void> disableEmailSecCheck(@PathVariable Long organizationId,
                                                     @RequestBody @Encrypt Set<Long> userIds) {
        this.userService.disableSecCheck(organizationId, userIds, SecCheckType.EMAIL);
        return Results.success();
    }
}
