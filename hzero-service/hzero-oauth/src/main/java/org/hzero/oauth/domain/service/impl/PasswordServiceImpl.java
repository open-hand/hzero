package org.hzero.oauth.domain.service.impl;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.oauth.domain.entity.BasePasswordPolicy;
import org.hzero.boot.oauth.domain.entity.BaseUserInfo;
import org.hzero.boot.oauth.domain.repository.BasePasswordPolicyRepository;
import org.hzero.boot.oauth.domain.repository.BaseUserInfoRepository;
import org.hzero.boot.oauth.domain.service.UserPasswordService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.captcha.CaptchaMessageHelper;
import org.hzero.core.captcha.CaptchaResult;
import org.hzero.core.exception.MessageException;
import org.hzero.core.user.UserType;
import org.hzero.core.util.Regexs;
import org.hzero.oauth.domain.entity.User;
import org.hzero.oauth.domain.repository.UserRepository;
import org.hzero.oauth.domain.service.PasswordService;
import org.hzero.oauth.domain.vo.MobileCaptchaVerifyVO;
import org.hzero.oauth.infra.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * @author bojiangzhou 2019/03/04
 */
public class PasswordServiceImpl implements PasswordService {

    @Autowired
    private CaptchaMessageHelper captchaMessageHelper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BaseUserInfoRepository userInfoRepository;
    @Autowired
    private UserPasswordService userPasswordService;
    @Autowired
    private BasePasswordPolicyRepository passwordPolicyRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePasswordByAccount(String account, UserType userType, String businessScope,
                                        String password, String captchaKey, String captcha) {

        CaptchaResult captchaResult = captchaMessageHelper.checkCaptcha(captchaKey, captcha, account, userType,
                businessScope, Constants.APP_CODE, false);
        if (!captchaResult.isSuccess()) {
            throw new MessageException(captchaResult.getMessage(), captchaResult.getCode());
        }

        User user = userRepository.selectUserByPhoneOrEmail(account, userType);
        if (user == null) {
            throw new CommonException("hoth.warn.phoneOrEmailNotFound");
        }

        // 更新用户密码
        userPasswordService.updateUserPassword(user.getId(), password);

        // 密码更新成功，绑定手机号或邮箱号
        BaseUserInfo baseUserInfo = this.userInfoRepository.selectByPrimaryKey(user.getId());
        if (Regexs.isMobile(account) && !BaseConstants.Flag.YES.equals(baseUserInfo.getPhoneCheckFlag())) {
            // 手机号已绑定
            baseUserInfo.setPhoneCheckFlag(BaseConstants.Flag.YES);
            // 更新用户详情
            this.userInfoRepository.updateOptional(baseUserInfo, BaseUserInfo.FIELD_PHONE_CHECK_FLAG);
        } else if (Regexs.isEmail(account) && !BaseConstants.Flag.YES.equals(baseUserInfo.getEmailCheckFlag())) {
            // 邮箱号已绑定
            baseUserInfo.setEmailCheckFlag(BaseConstants.Flag.YES);
            // 更新用户详情
            this.userInfoRepository.updateOptional(baseUserInfo, BaseUserInfo.FIELD_EMAIL_CHECK_FLAG);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePasswordByUser(Long userId, UserType userType, String password, MobileCaptchaVerifyVO verifyVO) {
        // 检查用户是否存在
        User user = userRepository.selectByPrimaryKey(userId);
        Assert.notNull(user, String.format("user with id %s not found.", userId));

        // 查询密码策略
        BasePasswordPolicy passwordPolicy = this.passwordPolicyRepository.selectPasswordPolicy(user.getOrganizationId());
        // 判断是否需要强制使用手机验证码校验
        if (BooleanUtils.isTrue(passwordPolicy.getForceCodeVerify())) {
            // 校验验证码
            CaptchaResult captchaResult = this.captchaMessageHelper.checkCaptcha(verifyVO.getCaptchaKey(), verifyVO.getCaptcha(),
                    verifyVO.getPhone(), UserType.ofDefault(verifyVO.getUserType()), verifyVO.getBusinessScope(), Constants.APP_CODE, false);
            if (!captchaResult.isSuccess()) {
                throw new MessageException(captchaResult.getMessage(), captchaResult.getCode());
            }

            // 查询用户详情信息
            BaseUserInfo userInfo = this.userInfoRepository.selectByPrimaryKey(user.getId());
            if (StringUtils.isBlank(user.getPhone())) {
                // 绑定手机号
                this.bindingPhone(user, userInfo, verifyVO.getPhone());
            } else if (StringUtils.equals(verifyVO.getPhone(), user.getPhone())) {
                if (BaseConstants.Flag.NO.equals(userInfo.getPhoneCheckFlag())) {
                    // 绑定手机号
                    this.bindingPhone(user, userInfo, verifyVO.getPhone());
                }
            } else if (BaseConstants.Flag.YES.equals(userInfo.getPhoneCheckFlag())) {
                // 发送短信的手机号与用户绑定的手机号不符
                throw new CommonException("hiam.error.self.phone.incompatible");
            } else {
                // 绑定手机号
                this.bindingPhone(user, userInfo, verifyVO.getPhone());
            }
        }
        userPasswordService.updateUserPassword(userId, password);
    }

    /**
     * 绑定手机号
     *
     * @param user     用户
     * @param userInfo 用户详情
     * @param phone    手机号
     */
    private void bindingPhone(User user, BaseUserInfo userInfo, String phone) {
        // 更新用户手机号
        user.setPhone(phone);
        this.userRepository.updateOptional(user, User.FIELD_PHONE);

        if (!BaseConstants.Flag.YES.equals(userInfo.getPhoneCheckFlag())) {
            userInfo.setPhoneCheckFlag(BaseConstants.Flag.YES);
            this.userInfoRepository.updateOptional(userInfo, BaseUserInfo.FIELD_PHONE_CHECK_FLAG);
        }
    }
}
