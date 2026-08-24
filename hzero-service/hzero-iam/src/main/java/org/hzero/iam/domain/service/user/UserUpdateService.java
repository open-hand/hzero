package org.hzero.iam.domain.service.user;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.hzero.core.user.UserType;
import org.hzero.iam.app.service.MemberRoleService;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.entity.UserInfo;
import org.hzero.iam.infra.feign.OauthAdminFeignClient;
import org.springframework.beans.factory.annotation.Autowired;

import io.choerodon.core.exception.CommonException;

/**
 * 更新用户服务
 *
 * @author bojiangzhou 2019/04/22
 */
public class UserUpdateService extends UserBuildService {

    @Autowired
    protected MemberRoleService memberRoleService;
    @Autowired
    private UserCheckService userCheckService;
    @Autowired
    private OauthAdminFeignClient oauthAdminService;

    @Override
    protected final User createUser(User user) {
        return super.createUser(user);
    }

    /**
     * 更新用户信息
     *
     * @param user 待更新用户
     */
    @Override
    public User updateUser(User user) {
        User returnVal = super.updateUser(user);
        if (BooleanUtils.isFalse(user.getEnabled()) || BooleanUtils.isTrue(user.getLocked())) {
            oauthAdminService.invalidByUsername(user.getLoginName());
        }
        return returnVal;
    }

    @Override
    protected final User registerUser(User user) {
        return super.registerUser(user);
    }

    @Override
    protected void checkValidity(User user) {
        User existUser = userRepository.selectByPrimaryKey(user.getId());
        if (existUser == null) {
            throw new CommonException("hiam.warn.user.notFound");
        }

        user.setEmail(StringUtils.defaultIfBlank(user.getEmail(), null));
        user.setPhone(StringUtils.defaultIfBlank(user.getPhone(), null));

        // 检查用户账号相关
        checkUserAccount(user, existUser);
    }

    /**
     * 检查用户账号是否合法
     *
     * @param user      待更新用户
     * @param existUser 数据库用户
     */
    protected void checkUserAccount(User user, User existUser) {
        // 手机号为空不检查
        if (StringUtils.isNotBlank(user.getPhone()) && !StringUtils.equals(existUser.getPhone(), user.getPhone())) {
            userCheckService.checkPhoneRegistered(user.getPhone(), UserType.ofDefault(user.getUserType()));
            //user.setPhoneCheckFlag(BaseConstants.Flag.NO);
        }
        // 邮箱为空不检查
        if (StringUtils.isNotBlank(user.getEmail()) && !StringUtils.equals(existUser.getEmail(), user.getEmail())) {
            userCheckService.checkEmailRegistered(user.getEmail(), UserType.ofDefault(user.getUserType()));
            //user.setEmailCheckFlag(BaseConstants.Flag.NO);
        }
    }

    @Override
    protected void handleBeforePersist(User user) {
        // do nothing
    }

    /**
     * 创建用户，插入数据库
     *
     * @param user User
     */
    @Override
    protected void persistUser(User user) {
        // 更新数据
        userRepository.updateOptional(user,
                User.FIELD_EMAIL,
                User.FIELD_REAL_NAME,
                User.FIELD_PHONE,
                User.FIELD_INTERNATIONAL_TEL_CODE,
                User.FIELD_ENABLED
        );
    }

    /**
     * 创建 UserInfo
     *
     * @param user User
     */
    @Override
    protected void handleUserInfo(User user) {
        // 同步UserInfo
        UserInfo existUserInfo = userRepository.selectUserInfoByPrimaryKey(user.getId());
        if (existUserInfo == null) {
            throw new CommonException("hiam.warn.user.notFound");
        }

        existUserInfo.setEndDateActive(user.getEndDateActive());
        existUserInfo.setStartDateActive(user.getStartDateActive());
        existUserInfo.setBirthday(user.getBirthday());
        existUserInfo.setNickname(user.getNickname());
        existUserInfo.setAddressDetail(user.getAddressDetail());
        existUserInfo.setGender(user.getGender());
        existUserInfo.setCountryId(user.getCountryId());
        existUserInfo.setRegionId(user.getRegionId());
        existUserInfo.setTenantId(user.getOrganizationId());
        userRepository.updateUserInfoByPrimaryKey(existUserInfo);
    }

}
