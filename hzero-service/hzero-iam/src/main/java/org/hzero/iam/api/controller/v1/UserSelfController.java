package org.hzero.iam.api.controller.v1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.captcha.CaptchaResult;
import org.hzero.core.user.UserType;
import org.hzero.core.util.Results;
import org.hzero.iam.api.dto.TenantDTO;
import org.hzero.iam.api.dto.UserConfigDTO;
import org.hzero.iam.api.dto.UserPasswordDTO;
import org.hzero.iam.app.service.UserSelfService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.TenantRepository;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.domain.service.user.UserCaptchaService;
import org.hzero.iam.domain.service.user.util.ConfigGetter;
import org.hzero.iam.domain.service.user.util.ProfileCode;
import org.hzero.iam.domain.vo.UserVO;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.*;

/**
 * 登录用户自己操作的接口
 *
 * @author bojiangzhou 2019/04/18
 */
@Api(tags = SwaggerApiConfig.USER_SELF)
@RestController("userSelfController.v1")
@RequestMapping("/hzero/v1")
@SuppressWarnings("rawtypes")
public class UserSelfController extends BaseController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private UserCaptchaService userCaptchaService;
    @Autowired
    private UserSelfService userSelfService;
    @Autowired
    private ConfigGetter configGetter;

    //
    // 登录用户信息查询
    // ------------------------------------------------------------------------------

    @Permission(permissionLogin = true)
    @ApiOperation(value = "登录用户 - 查询自身基础信息")
    @GetMapping(value = "/users/self")
    public ResponseEntity<UserVO> selectSelf() {
        return Results.success(userRepository.selectSelf());
    }

    @Permission(permissionLogin = true)
    @ApiOperation(value = "登录用户 - 个人中心查询详细信息")
    @GetMapping(value = "/users/self/detail")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<UserVO> selectSelfDetail() {
        return Results.success(userRepository.selectSelfDetails());
    }

    @ApiOperation("登录用户 - 查询可访问的租户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantNum", value = "租户编号", paramType = "query"),
            @ApiImplicitParam(name = "tenantName", value = "租户名称", paramType = "query")
    })
    @Permission(permissionLogin = true)
    @GetMapping("/users/self-tenants")
    public ResponseEntity<List<TenantDTO>> selfTenant(@Encrypt TenantDTO params) {
        return Results.success(tenantRepository.selectSelfTenants(params));
    }

    @ApiOperation("登录用户 - 查询可访问的租户列表（分页查询）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantNum", value = "租户编号", paramType = "query"),
            @ApiImplicitParam(name = "tenantName", value = "租户名称", paramType = "query")
    })
    @Permission(permissionLogin = true)
    @GetMapping("/users/self-tenants/page")
    public ResponseEntity<Page<TenantDTO>> selfTenantPage(@Encrypt TenantDTO params, PageRequest pageRequest) {
        return Results.success(tenantRepository.selectSelfTenants(params, pageRequest));
    }

    //
    // 登录用户更新信息
    // ------------------------------------------------------------------------------

    @ApiOperation(value = "登录用户 - 更改默认角色")
    @Permission(permissionLogin = true)
    @PutMapping("/users/default-role")
    public ResponseEntity updateUserDefaultRole(
            @RequestParam(value = "roleId", required = false) @Encrypt Long roleId,
            @RequestParam("tenantId") Long tenantId) {
        userSelfService.updateUserDefaultRole(roleId, tenantId);
        return Results.success();
    }

    @ApiOperation(value = "登录用户 - 更改默认公司")
    @Permission(permissionLogin = true)
    @PutMapping("/users/default-company")
    public ResponseEntity updateUserDefaultCompany(@Encrypt @RequestParam(value = "companyId", required = false) Long companyId,
                                                   @RequestParam("tenantId") Long tenantId) {
        userSelfService.updateUserDefaultCompany(companyId, tenantId);
        return Results.success();
    }


    @ApiOperation("登录用户 - 更改默认租户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户Id", paramType = "query")
    })
    @Permission(permissionLogin = true)
    @PutMapping("/users/default-tenant")
    public ResponseEntity updateUserDefaultTenant(Long tenantId) {
        userSelfService.updateUserDefaultTenant(tenantId);
        return Results.success();
    }

    @ApiOperation(value = "登录用户 - 更改名称")
    @Permission(permissionLogin = true)
    @PutMapping("/users/real-name")
    public ResponseEntity updateUserInfo(@RequestParam("realName") String realName) {
        userSelfService.updateUserRealName(realName);
        return Results.success();
    }

    @ApiOperation("登录用户 - 更改时区")
    @Permission(permissionLogin = true)
    @PutMapping("/users/time-zone")
    public ResponseEntity updateUserTimeZone(@RequestParam("timeZone") String timeZone) {
        userSelfService.updateUserTimeZone(timeZone);
        return Results.success();
    }

    @ApiOperation("登录用户 - 更改语言")
    @Permission(permissionLogin = true)
    @PutMapping("/users/default-language")
    public ResponseEntity updateUserDefaultLanguage(@RequestParam(name = "language", required = false) String language) {
        userSelfService.updateUserDefaultLanguage(language);
        return Results.success();
    }

    @ApiOperation("登录用户 - 更改默认日期时间格式")
    @Permission(permissionLogin = true)
    @PutMapping("/users/datetime-format")
    public ResponseEntity updateUserDatetimeFormat(@RequestParam(value = "dateFormat", required = false) String dateFormat,
                                                   @RequestParam(value = "timeFormat", required = false) String timeFormat) {
        userSelfService.updateUserDatetimeFormat(dateFormat, timeFormat);
        return Results.success();
    }

    @ApiOperation("登录用户 - 更改用户配置项")
    @Permission(permissionLogin = true)
    @PutMapping("/users/config-items")
    public ResponseEntity updateUserConfigItems(UserConfigDTO userConfigDTO) {
        userSelfService.updateUserConfigItems(userConfigDTO);
        return Results.success();
    }

    @ApiOperation("登录用户 - 更改头像")
    @Permission(permissionLogin = true)
    @PutMapping("/users/avatar")
    public ResponseEntity updateUserAvatar(@RequestParam String avatar) {
        userSelfService.updateUserAvatar(avatar);
        return Results.success();
    }

    @Permission(permissionLogin = true)
    @ApiOperation(value = "登录用户 - 修改自己的密码")
    @PutMapping(value = "/users/password")
    public ResponseEntity selfUpdatePassword(@RequestBody UserPasswordDTO userPasswordDTO) {
        userSelfService.selfUpdatePassword(userPasswordDTO);
        return Results.success();
    }

    //
    // 手机、邮箱、密码 相关
    // ------------------------------------------------------------------------------

    @ApiOperation(value = "登录用户 - 给手机发送验证码")
    @Permission(permissionLogin = true)
    @GetMapping("/users/phone/send-captcha")
    public ResponseEntity sendUserPhoneCaptcha(
            @ApiParam("国际冠码，默认+86") @RequestParam(defaultValue = BaseConstants.DEFAULT_CROWN_CODE) String internationalTelCode,
            @RequestParam String phone,
            @ApiParam("用户类型") @RequestParam(required = false, defaultValue = User.DEFAULT_USER_TYPE) String userType,
            @ApiParam(value = "验证码业务范围") @RequestParam(required = false) String businessScope) {
        CaptchaResult captchaResult = userCaptchaService.sendPhoneCaptcha(internationalTelCode, phone, UserType.ofDefault(userType),
                businessScope, true, false, configGetter.getValue(ProfileCode.MSG_CODE_VALIDATE_PHONE));
        Map<String, Object> result = new HashMap<>(2);
        result.put(BaseConstants.FIELD_MSG, captchaResult.getMessage());
        result.put(CaptchaResult.FIELD_INTERVAL, captchaResult.getInterval());
        result.put(CaptchaResult.FIELD_CAPTCHA_KEY, captchaResult.getCaptchaKey());
        return Results.success(result);
    }

    @ApiOperation(value = "登录用户 - 给邮箱发送验证码")
    @Permission(permissionLogin = true)
    @GetMapping("/users/email/send-captcha")
    public ResponseEntity sendUserEmailCaptcha(
            @RequestParam String email,
            @ApiParam("用户类型") @RequestParam(required = false, defaultValue = User.DEFAULT_USER_TYPE) String userType,
            @ApiParam(value = "验证码业务范围") @RequestParam(required = false) String businessScope) {
        CaptchaResult captchaResult = userCaptchaService.sendEmailCaptcha(email, UserType.ofDefault(userType), businessScope,
                true, false, configGetter.getValue(ProfileCode.MSG_CODE_VALIDATE_EMAIL));
        Map<String, Object> result = new HashMap<>(2);
        result.put(CaptchaResult.FIELD_CAPTCHA_KEY, captchaResult.getCaptchaKey());
        result.put(CaptchaResult.FIELD_INTERVAL, captchaResult.getInterval());
        result.put(BaseConstants.FIELD_MSG, captchaResult.getMessage());
        return Results.success(result);
    }

    @ApiOperation(value = "登录用户 - 前置密码校验")
    @Permission(permissionLogin = true)
    @GetMapping("/users/password/pre-validate")
    public ResponseEntity prePasswordValidate(
            @RequestParam String password,
            @ApiParam("用户类型") @RequestParam(required = false, defaultValue = User.DEFAULT_USER_TYPE) String userType,
            @ApiParam(value = "验证码业务范围") @RequestParam(required = false) String businessScope) {
        CaptchaResult captchaResult = userCaptchaService.validatePasswordAndCacheResult(password, UserType.ofDefault(userType), businessScope);
        Map<String, String> result = new HashMap<>(2);
        result.put(CaptchaResult.FIELD_LAST_CHECK_KEY, captchaResult.getLastCheckKey());
        return Results.success(result);
    }

    @ApiOperation(value = "登录用户 - 给新手机号发送验证码")
    @Permission(permissionLogin = true)
    @GetMapping("/users/phone-new/send-captcha")
    public ResponseEntity sendNewPhoneCaptcha(
            @RequestParam String lastCheckKey,
            @ApiParam("国际冠码，默认+86") @RequestParam(defaultValue = BaseConstants.DEFAULT_CROWN_CODE) String internationalTelCode,
            @RequestParam String phone,
            @ApiParam("用户类型") @RequestParam(required = false, defaultValue = User.DEFAULT_USER_TYPE) String userType,
            @ApiParam(value = "验证码业务范围") @RequestParam(required = false) String businessScope) {
        CaptchaResult captchaResult = userCaptchaService.sendPhoneCaptchaAfterLastCheck(internationalTelCode, lastCheckKey, phone,
                UserType.ofDefault(userType), businessScope, configGetter.getValue(ProfileCode.MSG_CODE_MODIFY_PHONE));
        return Results.success(UserPublicController.captchaResultMap(captchaResult));
    }

    @ApiOperation(value = "登录用户 - 新手机号验证码验证并修改手机号")
    @Permission(permissionLogin = true)
    @GetMapping("/users/phone-new/validate-captcha")
    public ResponseEntity validateNewPhoneCaptcha(
            @RequestParam String lastCheckKey,
            @RequestParam String captchaKey,
            @RequestParam String captcha,
            @RequestParam String phone,
            @ApiParam("用户类型") @RequestParam(required = false, defaultValue = User.DEFAULT_USER_TYPE) String userType,
            @ApiParam(value = "验证码业务范围") @RequestParam(required = false) String businessScope) {
        userSelfService.validateNewPhoneCaptchaAndUpdate(lastCheckKey, captchaKey, captcha, phone,
                UserType.ofDefault(userType), businessScope);
        return Results.success();
    }

    @ApiOperation(value = "登录用户 - 给新邮箱发送验证码")
    @Permission(permissionLogin = true)
    @GetMapping("/users/email-new/send-captcha")
    public ResponseEntity sendNewEmailCaptcha(
            @RequestParam String lastCheckKey,
            @RequestParam String email,
            @ApiParam("用户类型") @RequestParam(required = false, defaultValue = User.DEFAULT_USER_TYPE) String userType,
            @ApiParam(value = "验证码业务范围") @RequestParam(required = false) String businessScope) {
        CaptchaResult captchaResult = userCaptchaService.sendEmailCaptchaAfterLastCheck(lastCheckKey, email,
                UserType.ofDefault(userType), businessScope, configGetter.getValue(ProfileCode.MSG_CODE_MODIFY_EMAIL));
        return Results.success(UserPublicController.captchaResultMap(captchaResult));
    }

    @ApiOperation(value = "登录用户 - 新邮箱验证码验证并修改邮箱")
    @Permission(permissionLogin = true)
    @GetMapping("/users/email-new/validate-captcha")
    public ResponseEntity validateNewEmailCaptcha(
            @RequestParam(required = false) String lastCheckKey,
            @RequestParam String captchaKey,
            @RequestParam String captcha,
            @RequestParam String email,
            @ApiParam("用户类型") @RequestParam(required = false, defaultValue = User.DEFAULT_USER_TYPE) String userType,
            @ApiParam(value = "验证码业务范围") @RequestParam(required = false) String businessScope) {
        userSelfService.validateNewEmailCaptchaAndUpdate(lastCheckKey, captchaKey, captcha, email,
                UserType.ofDefault(userType), businessScope);
        return Results.success();
    }

    @ApiOperation(value = "登录用户 - 认证手机(先发手机送验证码)")
    @Permission(permissionLogin = true)
    @PutMapping("/users/phone/authenticate")
    public ResponseEntity authenticateUserPhone(
            @RequestParam String captchaKey,
            @RequestParam String captcha,
            @ApiParam("用户类型") @RequestParam(required = false, defaultValue = User.DEFAULT_USER_TYPE) String userType,
            @ApiParam(value = "验证码业务范围") @RequestParam(required = false) String businessScope) {
        userSelfService.validateCaptchaAndAuthenticatePhone(captchaKey, captcha, UserType.ofDefault(userType), businessScope);
        return Results.success();
    }

    @ApiOperation(value = "登录用户 - 认证邮箱(先发邮箱送验证码)")
    @Permission(permissionLogin = true)
    @PutMapping("/users/email/authenticate")
    public ResponseEntity authenticateUserEmail(
            @RequestParam String captchaKey,
            @RequestParam String captcha,
            @ApiParam("用户类型") @RequestParam(required = false, defaultValue = User.DEFAULT_USER_TYPE) String userType,
            @ApiParam(value = "验证码业务范围") @RequestParam(required = false) String businessScope) {
        userSelfService.validateCaptchaAndAuthenticateEmail(captchaKey, captcha, UserType.ofDefault(userType), businessScope);
        return Results.success();
    }

    @Permission(permissionLogin = true)
    @ApiOperation(value = "登录用户 - 检查自己的密码是否正确")
    @PutMapping(value = "/users/check-password")
    public ResponseEntity checkPasswordRight(@RequestBody UserPasswordDTO userPasswordDTO) {
        userSelfService.checkSelfPasswordRight(userPasswordDTO.getOriginalPassword());
        return Results.success();
    }

    @ApiOperation(value = "查询当前用户注册时填写的企业名称")
    @Permission(permissionLogin = true)
    @GetMapping("/users/self/company-name")
    public ResponseEntity<UserVO> getCompanyName() {
        return Results.success(userRepository.selectCompanyName(DetailsHelper.getUserDetails().getUserId()));
    }

}
