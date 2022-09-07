package org.hzero.iam.api.controller.v1;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.hzero.core.base.BaseController;
import org.hzero.core.user.UserType;
import org.hzero.core.util.Results;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.hzero.iam.api.dto.UserEmployeeAssignDTO;
import org.hzero.iam.api.dto.UserExportDTO;
import org.hzero.iam.api.dto.UserPasswordDTO;
import org.hzero.iam.app.service.UserService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.domain.vo.UserVO;
import org.hzero.iam.infra.common.utils.AssertUtils;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * HIAM用户接口，增加了各种用户查询API，增加用户注册、校验API，增加修改手机、邮箱API。
 *
 * @author bojiangzhou 2018/07/01
 * @see UserController
 */
@Api(tags = SwaggerApiConfig.USER_SITE)
@RestController("hiamUserSiteController.v1")
@RequestMapping("/hzero/v1/users")
@SuppressWarnings("rawtypes")
public class UserSiteController extends BaseController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    //
    // 用户管理接口
    // ------------------------------------------------------------------------------

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "用户管理 - 分页查询跨租户用户简要信息")
    @GetMapping("/paging/all")
    public ResponseEntity<Page<UserVO>> pagingAll(@Encrypt UserVO user,
                                                  @SortDefault(value = User.FIELD_ID) PageRequest pageRequest) {
        return Results.success(userRepository.selectAllocateUsers(user, pageRequest));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "用户管理 - 分页查询用户简要信息")
    @GetMapping("/paging")
    public ResponseEntity<Page<UserVO>> paging(@Encrypt UserVO user,
                                               @SortDefault(value = User.FIELD_ID) PageRequest pageRequest) {
        if (user.getDefaultTenant() != null && user.getDefaultTenant() == 1) {
            user.setOrganizationId(Constants.SITE_TENANT_ID);
        }
        return Results.success(userRepository.selectSimpleUsers(user, pageRequest));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "用户管理 - 根据用户ID查询用户详细信息")
    @GetMapping("/{userId}/info")
    public ResponseEntity selectUserDetail(@PathVariable @Encrypt Long userId) {
        UserVO params = new UserVO();
        params.setId(userId);
        return Results.success(userRepository.selectUserDetails(params));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "用户管理 - 创建用户")
    @PostMapping
    public ResponseEntity createUser(@RequestBody User user) {
        validObject(user);
        return Results.success(userService.createUser(user));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "用户管理 - 修改用户")
    @PutMapping
    public ResponseEntity updateUser(@RequestBody @Encrypt User user) {
        SecurityTokenHelper.validToken(user, false);
        return Results.success(userService.updateUser(user));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "用户管理 - 锁定用户")
    @PostMapping("/{userId}/locked")
    public ResponseEntity lockUser(@PathVariable @Encrypt Long userId) {
        userService.lockUser(userId, null);
        return Results.success();
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "用户管理 - 解锁用户")
    @PostMapping("/{userId}/unlocked")
    public ResponseEntity unlockUser(@PathVariable @Encrypt Long userId) {
        userService.unlockUser(userId, null);
        return Results.success();
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "用户管理 - 禁用用户")
    @PostMapping("/{userId}/frozen")
    public ResponseEntity frozenUser(@PathVariable @Encrypt Long userId) {
        userService.frozenUser(userId, null);
        return Results.success();
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "用户管理 - 启用用户")
    @PostMapping("/{userId}/unfrozen")
    public ResponseEntity unfrozenUser(@PathVariable @Encrypt Long userId) {
        userService.unfrozenUser(userId, null);
        return Results.success();
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "用户管理 - 修改用户密码")
    @PutMapping(value = "/{userId}/admin-password")
    public ResponseEntity updateUserPassword(@PathVariable @Encrypt Long userId,
                                             @RequestBody UserPasswordDTO userPassword) {
        userService.updateUserPassword(userId, userPassword.getOrganizationId(), userPassword);
        return Results.success();
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "用户管理 - 重置用户密码")
    @PutMapping(value = "/{userId}/admin-password-reset")
    public ResponseEntity resetUserPassword(@PathVariable @Encrypt Long userId,
                                            @RequestBody UserPasswordDTO userPassword) {
        userService.resetUserPassword(userId, userPassword.getOrganizationId(), userPassword);
        return Results.success();
    }

    @ApiOperation(value = "用户管理 - 导出用户信息")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/export")
    @ExcelExport(UserExportDTO.class)
    public ResponseEntity<List<UserExportDTO>> export(
            @ApiParam("用户权限类型查询条件(csv)") @RequestParam(value = "authorityTypeQueryParams", required = false) String authorityTypeQueryParams,
            @Encrypt UserVO user,
            ExportParam exportParam,
            HttpServletResponse response,
            PageRequest pageRequest) {
        if (user.getDefaultTenant() != null && user.getDefaultTenant() == 1) {
            user.setOrganizationId(Constants.SITE_TENANT_ID);
        }
        List<UserExportDTO> results = this.userRepository.exportUserInfo(user, authorityTypeQueryParams, pageRequest, exportParam);
        return Results.success(results);
    }


    //
    // 外部接口，暂不知道哪里使用了
    // ------------------------------------------------------------------------------

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "根据用户名或邮箱或手机查询用户信息")
    @GetMapping
    public ResponseEntity<UserVO> queryByLoginNameOrEmailOrPhone(
            @RequestParam(name = "condition") String condition,
            @ApiParam(value = "用户类型：P/C") @RequestParam(required = false, defaultValue = UserType.DEFAULT_USER_TYPE) String userType) {
        UserVO params = new UserVO();
        params.setCondition(condition);
        params.setUserType(userType);
        UserVO user = userRepository.selectByLoginNameOrEmailOrPhone(params);
        AssertUtils.notNull(user, "user.account.not-exists", condition);
        return Results.success(user);
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "根据用户名查询用户信息")
    @GetMapping("/username")
    public ResponseEntity<UserVO> queryByLoginName(
            @RequestParam(name = "username") String username,
            @ApiParam(value = "用户类型：P/C") @RequestParam(required = false, defaultValue = UserType.DEFAULT_USER_TYPE) String userType) {
        UserVO params = new UserVO();
        params.setCondition(username);
        params.setUserType(userType);
        UserVO user = userRepository.selectByLoginNameOrEmailOrPhone(params);
        AssertUtils.notNull(user, "user.account.not-exists", username);
        user.setLoginName(username);
        return Results.success(user);
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "根据邮箱查询用户信息")
    @GetMapping("/email")
    public ResponseEntity<UserVO> queryByEmail(
            @RequestParam(name = "email") String email,
            @ApiParam(value = "用户类型：P/C") @RequestParam(required = false, defaultValue = UserType.DEFAULT_USER_TYPE) String userType) {
        UserVO params = new UserVO();
        params.setCondition(email);
        params.setUserType(userType);
        UserVO user = userRepository.selectByLoginNameOrEmailOrPhone(params);
        AssertUtils.notNull(user, "user.account.not-exists", email);
        user.setEmail(email);
        return Results.success(user);
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "根据手机查询用户信息")
    @GetMapping("/phone")
    public ResponseEntity<UserVO> queryByPhone(
            @RequestParam(name = "phone") String phone,
            @ApiParam(value = "用户类型：P/C") @RequestParam(required = false, defaultValue = UserType.DEFAULT_USER_TYPE) String userType) {
        UserVO params = new UserVO();
        params.setCondition(phone);
        params.setUserType(userType);
        UserVO user = userRepository.selectByLoginNameOrEmailOrPhone(params);
        AssertUtils.notNull(user, "user.account.not-exists", phone);
        user.setPhone(phone);
        return Results.success(user);
    }

    @ApiOperation(value = "员工分配列表")
    @ApiImplicitParams({@ApiImplicitParam(name = "userId", value = "用户Id", paramType = "path")})
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{userId}/user-employee-assigns")
    @CustomPageRequest
    public ResponseEntity<Page<UserEmployeeAssignDTO>> pageUserEmployeeAssign(
            @PathVariable @Encrypt Long userId,
            @Encrypt UserEmployeeAssignDTO params,
            @ApiIgnore PageRequest pageRequest) {
        return Results.success(userService.pageUserEmployeeAssign(pageRequest, null, userId, params));
    }
}
