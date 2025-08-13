package org.hzero.iam.domain.service.user;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;

import org.hzero.boot.oauth.policy.PasswordPolicyManager;
import org.hzero.boot.platform.encrypt.EncryptClient;
import org.hzero.core.user.UserType;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.HiamStaticTextRepository;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.domain.vo.CompanyVO;
import org.hzero.iam.infra.common.utils.UserUtils;

/**
 * 用户信息校验服务
 *
 * @author bojiangzhou 2018/07/01
 */
public class UserCheckService {

    @Autowired
    private PasswordPolicyManager passwordPolicyManager;
    @Autowired
    private HiamStaticTextRepository staticTextRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EncryptClient encryptClient;

    /**
     * 校验企业名称是否已注册
     * 
     * @param companyName 企业名称
     */
    public void checkCompanyNameRegistered(String companyName) {
        CompanyVO company = userRepository.countCompanyByName(companyName);
        if (company != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(company.getCreationDate());
            throw new CommonException("company.name.repeat",
                    String.valueOf(calendar.get(Calendar.YEAR)),
                    String.format("%02d", calendar.get(Calendar.MONTH) + 1),
                    String.format("%02d", calendar.get(Calendar.DATE)));
        }
    }

    /**
     * 校验登录名是否已注册
     *
     * @param loginName 登录名
     */
    public void checkLoginNameRegistered(String loginName) {
        Assert.notNull(loginName, "loginName should not be null.");
        if (userRepository.existsByLoginName(loginName)) {
            throw new CommonException("user.validation.phone.exists");
        }
    }

    /**
     * 校验手机号是否已注册
     * 
     * @param phone 手机号
     */
    public void checkPhoneRegistered(String phone, UserType userType) {
        Assert.notNull(phone, "phone should not be null.");
        if (userRepository.existsByPhone(phone, userType.value())){
            throw new CommonException("user.validation.phone.exists");
        }
    }

    /**
     * 校验邮箱是否已注册
     * 
     * @param email 邮箱
     */
    public void checkEmailRegistered(String email, UserType userType) {
        Assert.notNull(email, "email should not be null.");
        if (userRepository.existsByEmail(email, userType.value())) {
            throw new CommonException("user.validation.email.exists");
        }
    }

    /**
     * 校验密码是否符合密码策略
     * 
     * @param password 密码
     */
    public void checkPasswordPolicy(String password, Long tenantId) {
        passwordPolicyManager.passwordValidate(password, tenantId);
    }

    /**
     * 校验协议是否正确
     * 
     * @param protocolId 协议ID
     */
    public void checkRegistrationProtocol(Long protocolId) {
        Assert.notNull(protocolId, "user.validation.registration-protocol.not-null");
        int count = staticTextRepository.countValidRegisterProtocol(protocolId);
        if (count != 1) {
            throw new CommonException("user.validation.registration-protocol.invalid");
        }
    }

    /**
     * 校验用户密码是否正确
     * 
     * @param password 密码
     * @return true - 正确
     */
    public boolean checkPasswordRight(String password) {
        // 检查手机号与原手机号是否匹配
        CustomUserDetails self = UserUtils.getUserDetails();
        User user = userRepository.selectUserPassword(self.getUserId());
        return user.comparePassword(encryptClient.decrypt(password));
    }



}
