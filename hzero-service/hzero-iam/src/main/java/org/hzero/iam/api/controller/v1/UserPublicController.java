package org.hzero.iam.api.controller.v1;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.encrypt.EncryptClient;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.captcha.CaptchaResult;
import org.hzero.core.user.UserType;
import org.hzero.core.util.CheckStrength;
import org.hzero.core.util.Results;
import org.hzero.iam.api.dto.PasswordDTO;
import org.hzero.iam.app.service.UserService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.domain.service.user.UserCaptchaService;
import org.hzero.iam.domain.service.user.UserCheckService;
import org.hzero.iam.domain.service.user.util.ConfigGetter;
import org.hzero.iam.domain.service.user.util.ProfileCode;
import org.hzero.iam.infra.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 租户层用户接口
 *
 * @author bojiangzhou 2019/10/28
 * @author bojiangzhou 2018/07/05
 */
@Api(tags = SwaggerApiConfig.USER_PUBLIC)
@RestController("userPublicController.v1")
@RequestMapping("/hzero/v1")
@SuppressWarnings("rawtypes")
public class UserPublicController extends BaseController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserCaptchaService userCaptchaService;
    @Autowired
    private UserCheckService userCheckService;
    @Autowired
    private ConfigGetter configGetter;
    @Autowired
    private EncryptClient encryptClient;

    //
    // 发送验证码
    // ------------------------------------------------------------------------------

    @ApiOperation(value = "验证码 - 注册时发送手机验证码")
    @Permission(permissionPublic = true)
    @GetMapping("/users/register-phone/send-captcha")
    public ResponseEntity<Map> sendRegisterPhoneCaptcha(
            @ApiParam("国际冠码，默认+86") @RequestParam(defaultValue = BaseConstants.DEFAULT_CROWN_CODE) String internationalTelCode,
            @ApiParam("发送验证码的手机号") @RequestParam String phone,
            @ApiParam(value = "用户类型：P/C") @RequestParam(required = false, defaultValue = UserType.DEFAULT_USER_TYPE) String userType,
            @ApiParam(value = "验证码业务范围") @RequestParam(required = false) String businessScope) {
        CaptchaResult captchaResult = userCaptchaService.sendPhoneCaptcha(internationalTelCode, phone, UserType.ofDefault(userType),
                businessScope, false, false, configGetter.getValue(ProfileCode.MSG_CODE_REGISTER_USER));
        return Results.success(captchaResultMap(captchaResult));
    }

    @ApiOperation(value = "验证码 - 注册时发送邮箱验证码")
    @Permission(permissionPublic = true)
    @GetMapping("/users/register-email/send-captcha")
    public ResponseEntity<Map> sendRegisterEmailCaptcha(
            @ApiParam("发送验证码的邮箱") @RequestParam String email,
            @ApiParam(value = "用户类型：P/C") @RequestParam(required = false, defaultValue = UserType.DEFAULT_USER_TYPE) String userType,
            @ApiParam(value = "验证码业务范围") @RequestParam(required = false) String businessScope) {
        CaptchaResult captchaResult = userCaptchaService.sendEmailCaptcha(email, UserType.ofDefault(userType), businessScope,
                false, false, configGetter.getValue(ProfileCode.MSG_CODE_REGISTER_USER));
        return Results.success(captchaResultMap(captchaResult));
    }

    @ApiOperation(value = "验证码 - 找回密码时发送手机验证码")
    @Permission(permissionPublic = true)
    @GetMapping("/users/find-password-phone/send-captcha")
    public ResponseEntity<Map> sendModifyPasswordPhoneCaptcha(
            @ApiParam("国际冠码，默认+86") @RequestParam(defaultValue = BaseConstants.DEFAULT_CROWN_CODE) String internationalTelCode,
            @ApiParam("发送验证码的手机号") @RequestParam String phone,
            @ApiParam(value = "用户类型：P/C") @RequestParam(required = false, defaultValue = UserType.DEFAULT_USER_TYPE) String userType,
            @ApiParam(value = "验证码业务范围") @RequestParam(required = false) String businessScope) {
        CaptchaResult captchaResult = userCaptchaService.sendPhoneCaptcha(internationalTelCode, phone, UserType.ofDefault(userType),
                businessScope, false, true, configGetter.getValue(ProfileCode.MSG_CODE_FIND_PASSWORD));
        return Results.success(captchaResultMap(captchaResult));
    }

    @ApiOperation(value = "验证码 - 找回密码时发送邮箱验证码")
    @Permission(permissionPublic = true)
    @GetMapping("/users/find-password-email/send-captcha")
    public ResponseEntity<Map> sendModifyPasswordEmailCaptcha(
            @ApiParam("发送验证码的邮箱") @RequestParam String email,
            @ApiParam(value = "用户类型：P/C") @RequestParam(required = false, defaultValue = UserType.DEFAULT_USER_TYPE) String userType,
            @ApiParam(value = "验证码业务范围") @RequestParam(required = false) String businessScope) {
        CaptchaResult captchaResult = userCaptchaService.sendEmailCaptcha(email, UserType.ofDefault(userType), businessScope,
                false, true, configGetter.getValue(ProfileCode.MSG_CODE_FIND_PASSWORD));
        return Results.success(captchaResultMap(captchaResult));
    }


    @ApiOperation(value = "验证码 - 前置验证码校验(public)")
    @Permission(permissionPublic = true)
    @GetMapping("/users/captcha/pre-validate")
    public ResponseEntity lastCaptchaValidate(
            @ApiParam(value = "用户类型：P/C") @RequestParam(required = false, defaultValue = UserType.DEFAULT_USER_TYPE) String userType,
            @ApiParam(value = "验证码业务范围") @RequestParam(required = false) String businessScope,
            @RequestParam String captchaKey,
            @RequestParam String captcha) {
        CaptchaResult captchaResult = userCaptchaService.validateCaptchaAndCacheResult(captchaKey, captcha,
                UserType.ofDefault(userType), businessScope);
        Map<String, String> result = new HashMap<>(2);
        result.put(CaptchaResult.FIELD_LAST_CHECK_KEY, captchaResult.getLastCheckKey());
        return Results.success(result);
    }

    //
    // 创建用户
    // ------------------------------------------------------------------------------

    @Permission(permissionWithin = true)
    @ApiOperation(value = "创建用户 - 内部调用接口，需传入默认分配的角色；如果传入了 type ，则校验验证码，不传则不校验")
    @PostMapping("/users/internal")
    public ResponseEntity<User> createUserInternal(
            @RequestBody User user,
            @ApiParam(value = "验证码业务范围") @RequestParam(required = false) String businessScope,
            @ApiParam(value = "创建类型：手机-phone/邮箱-email") @RequestParam(required = false) String type,
            @ApiParam(value = "是否校验验证码") @RequestParam(required = false, defaultValue = "true") boolean validateCaptcha,
            @ApiParam(value = "验证码Key：发送验证码时返回的 captchaKey") @RequestParam(required = false) String captchaKey,
            @ApiParam(value = "验证码：用户输入的验证码") @RequestParam(required = false) String captcha) {
        // 手机注册
        if (StringUtils.equalsIgnoreCase(type, User.FIELD_PHONE)) {
            if (validateCaptcha) {
                userCaptchaService.validateCaptcha(captchaKey, captcha, user.getPhone(),
                        UserType.ofDefault(user.getUserType()), businessScope);
            }
            user.setPhoneCheckFlag(BaseConstants.Flag.YES);
        }
        // 邮箱注册
        else if (StringUtils.equalsIgnoreCase(type, User.FIELD_EMAIL)) {
            if (validateCaptcha) {
                userCaptchaService.validateCaptcha(captchaKey, captcha, user.getEmail(),
                        UserType.ofDefault(user.getUserType()), businessScope);
            }
            user.setEmailCheckFlag(BaseConstants.Flag.YES);
        }
        user.setUserType(UserType.ofDefault(user.getUserType()).value());
        validObject(user);
        return Results.success(userService.createUserInternal(user));
    }

    @ApiOperation(value = "用户注册(创建) - 使用手机注册，分配默认的游客角色")
    @Permission(permissionPublic = true)
    @PostMapping("/users/register")
    public ResponseEntity register(
            @RequestBody User user,
            @ApiParam(value = "验证码业务范围") @RequestParam(required = false) String businessScope,
            @ApiParam(value = "验证码Key：发送验证码时返回的 captchaKey") @RequestParam(required = false) String captchaKey,
            @ApiParam(value = "验证码：用户输入的验证码") @RequestParam(required = false) String captcha) {
        user.setUserType(UserType.ofDefault(user.getUserType()).value());
        userCaptchaService.validateCaptcha(captchaKey, captcha, user.getPhone(),
                UserType.ofDefault(user.getUserType()), businessScope);
        user.setPhoneCheckFlag(BaseConstants.Flag.YES);
        user = userService.register(user);
        return Results.success(user);
    }

    @ApiOperation(value = "用户注册(创建) - 使用邮箱注册，分配默认的游客角色")
    @Permission(permissionPublic = true)
    @PostMapping("/users/register-with-email")
    public ResponseEntity registerWithEmail(
            @RequestBody User user,
            @ApiParam(value = "验证码业务范围") @RequestParam(required = false) String businessScope,
            @ApiParam(value = "验证码Key：发送验证码时返回的 captchaKey") @RequestParam(required = false) String captchaKey,
            @ApiParam(value = "验证码：用户输入的验证码") @RequestParam(required = false) String captcha) {
        user.setUserType(UserType.ofDefault(user.getUserType()).value());
        userCaptchaService.validateCaptcha(captchaKey, captcha, user.getEmail(),
                UserType.ofDefault(user.getUserType()), businessScope);
        user.setEmailCheckFlag(BaseConstants.Flag.YES);
        user = userService.register(user);
        return Results.success(user);
    }

    //
    // 找回密码
    // ------------------------------------------------------------------------------

    @ApiOperation(value = "找回密码 - 通过手机找回密码")
    @Permission(permissionPublic = true)
    @PutMapping("/users/find-password/phone")
    public ResponseEntity findPasswordByPhone(
            @ApiParam("密码参数") @RequestBody PasswordDTO passwordDTO,
            @ApiParam(value = "用户类型：P/C") @RequestParam(required = false, defaultValue = UserType.DEFAULT_USER_TYPE) String userType,
            @ApiParam(value = "验证码业务范围") @RequestParam(required = false) String businessScope,
            @ApiParam(value = "验证码Key：发送验证码时返回的 captchaKey") @RequestParam(required = false) String captchaKey,
            @ApiParam(value = "验证码：用户输入的验证码") @RequestParam(required = false) String captcha) {
        userCaptchaService.validateCaptcha(captchaKey, captcha, passwordDTO.getPhone(),
                UserType.ofDefault(userType), businessScope);
        userService.updateUserPasswordByPhone(passwordDTO, UserType.ofDefault(userType));
        return Results.success();
    }

    @ApiOperation(value = "找回密码 - 通过邮箱找回密码")
    @Permission(permissionPublic = true)
    @PutMapping("/users/find-password/email")
    public ResponseEntity findPasswordByEmail(
            @ApiParam("密码参数") @RequestBody PasswordDTO passwordDTO,
            @ApiParam(value = "用户类型：P/C") @RequestParam(required = false, defaultValue = UserType.DEFAULT_USER_TYPE) String userType,
            @ApiParam(value = "验证码业务范围") @RequestParam(required = false) String businessScope,
            @ApiParam(value = "验证码Key：发送验证码时返回的 captchaKey") @RequestParam(required = false) String captchaKey,
            @ApiParam(value = "验证码：用户输入的验证码") @RequestParam(required = false) String captcha) {
        userCaptchaService.validateCaptcha(captchaKey, captcha, passwordDTO.getEmail(),
                UserType.ofDefault(userType), businessScope);
        userService.updateUserPasswordByEmail(passwordDTO, UserType.ofDefault(userType));
        return Results.success();
    }

    @ApiOperation(value = "找回密码 - 通过手机找回密码(前置校验之后)")
    @Permission(permissionPublic = true)
    @PutMapping("/users/find-password-checked/phone")
    public ResponseEntity findPasswordAfterCheckByPhone(
            @ApiParam("密码参数") @RequestBody PasswordDTO passwordDTO,
            @ApiParam(value = "用户类型：P/C") @RequestParam(required = false, defaultValue = UserType.DEFAULT_USER_TYPE) String userType,
            @ApiParam(value = "前置验证码Key lastCheckKey") @RequestParam String lastCheckKey) {
        userCaptchaService.validateLastCheckResult(lastCheckKey, UserType.ofDefault(userType), null, Constants.APP_CODE);
        userService.updateUserPasswordByPhone(passwordDTO, UserType.ofDefault(userType));
        return Results.success();
    }

    @ApiOperation(value = "找回密码 - 通过邮箱找回密码(前置校验之后)")
    @Permission(permissionPublic = true)
    @PutMapping("/users/find-password-checked/email")
    public ResponseEntity findPasswordAfterCheckByEmail(
            @ApiParam("密码参数") @RequestBody PasswordDTO passwordDTO,
            @ApiParam(value = "用户类型：P/C") @RequestParam(required = false, defaultValue = UserType.DEFAULT_USER_TYPE) String userType,
            @ApiParam(value = "前置验证码Key lastCheckKey") @RequestParam String lastCheckKey) {
        userCaptchaService.validateLastCheckResult(lastCheckKey, UserType.ofDefault(userType), null, Constants.APP_CODE);
        userService.updateUserPasswordByEmail(passwordDTO, UserType.ofDefault(userType));
        return Results.success();
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "登录用户 - 用户根据手机或邮箱修改密码")
    @PutMapping(value = "/users/reset-password/by-account")
    public ResponseEntity updatePasswordByAccount(
            @RequestParam("account") String account,
            @RequestParam("password") String password,
            @RequestParam String captchaKey,
            @RequestParam String captcha,
            @ApiParam("用户类型") @RequestParam(required = false, defaultValue = User.DEFAULT_USER_TYPE) String userType,
            @ApiParam(value = "验证码业务范围") @RequestParam(required = false) String businessScope) {
        userService.updatePasswordByAccount(account, UserType.ofDefault(userType), businessScope, password, captchaKey, captcha);
        return Results.success();
    }

    //
    // 用户校验
    // ------------------------------------------------------------------------------

    @ApiOperation(value = "用户校验 - 校验用户登录账号是否已注册")
    @Permission(permissionPublic = true)
    @GetMapping("/users/validation/username")
    public ResponseEntity checkLoginNameRegistered(@ApiParam("登录名") @RequestParam(name = "username") String loginName) {
        userCheckService.checkLoginNameRegistered(loginName);
        return Results.success();
    }

    @ApiOperation(value = "用户校验 - 校验用户手机号是否已注册")
    @Permission(permissionPublic = true)
    @GetMapping("/users/validation/phone")
    public ResponseEntity checkPhoneRegistered(
            @ApiParam("手机号") @RequestParam String phone,
            @ApiParam("用户类型") @RequestParam(required = false, defaultValue = User.DEFAULT_USER_TYPE) String userType) {
        userCheckService.checkPhoneRegistered(phone, UserType.ofDefault(userType));
        return Results.success();
    }

    @ApiOperation(value = "用户校验 - 校验用户邮箱是否已注册")
    @Permission(permissionPublic = true)
    @GetMapping("/users/validation/email")
    public ResponseEntity checkEmailRegistered(
            @ApiParam("邮箱") @RequestParam String email,
            @ApiParam("用户类型") @RequestParam(required = false, defaultValue = User.DEFAULT_USER_TYPE) String userType) {
        userCheckService.checkEmailRegistered(email, UserType.ofDefault(userType));
        return Results.success();
    }

    @ApiOperation(value = "检测密码强度")
    @Permission(permissionPublic = true)
    @PostMapping("/users/check-pwd-strength")
    public ResponseEntity checkPasswordStrength(@ApiParam("密码") @RequestParam("password") String password) {
        password = encryptClient.decrypt(password);
        Map<String, String> strength = new HashMap<>(1);
        strength.put("level", CheckStrength.getPasswordLevel(password).name());
        return Results.success(strength);
    }

    /**
     * 校验密码是否符合密码策略
     *
     * @param password 密码
     * @param tenantId 租户ID，默认全局租户
     * @throws CommonException 密码不合法
     */
    @ApiOperation(value = "校验密码是否符合密码策略")
    @Permission(permissionPublic = true)
    @PostMapping("/users/validation/password-policy")
    public ResponseEntity checkPasswordPolicy(@ApiParam("密码") @RequestParam("password") String password,
                                              @ApiParam("租户ID") @RequestParam(required = false) Long tenantId) {
        tenantId = Optional.ofNullable(tenantId).orElse(Constants.SITE_TENANT_ID);
        userCheckService.checkPasswordPolicy(password, tenantId);
        return Results.success();
    }

    //
    // public
    // ------------------------------------------------------------------------------

    @ApiOperation(value = "查询所有国际冠码")
    @Permission(permissionPublic = true)
    @GetMapping("/users/idd-list")
    public ResponseEntity<List<Map<String, String>>> listIDD() {
        return Results.success(userService.listIDD());
    }

    @ApiOperation(value = "拉取最近更新过的记录")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("users/recent")
    public ResponseEntity<List<User>> listRecentUser(
            @ApiParam("租户ID") @RequestParam(required = false) Long tenantId,
            @ApiParam("过去多久内(单位：ms，默认5min)") @RequestParam(required = false, defaultValue = "300000") long before) {
        return Results.success(userRepository.listRecentUser(tenantId, new Date(System.currentTimeMillis() - before)));
    }

    public static Map<String, Object> captchaResultMap(CaptchaResult captchaResult) {
        Map<String, Object> result = new HashMap<>(2);
        result.put(CaptchaResult.FIELD_CAPTCHA_KEY, captchaResult.getCaptchaKey());
        result.put(CaptchaResult.FIELD_INTERVAL, captchaResult.getInterval());
        result.put(BaseConstants.FIELD_MSG, captchaResult.getMessage());
        return result;
    }

}
