package org.hzero.iam.infra.util;

import org.apache.commons.lang3.StringUtils;

import io.choerodon.core.exception.CommonException;

/**
 * 验证工具类
 *
 * @author bojiangzhou 2018/07/04
 */
public class ValidatorUtils {

    private ValidatorUtils() {}

    public static final String PHONE_REGEX = "^1[2345678][0-9]{9}$";
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$";

    /**
     * 校验手机号格式是否正确
     * 
     * @param phone 手机号
     */
    public static void validatePhone(String phone) {
        if (StringUtils.isBlank(phone) || !phone.matches(PHONE_REGEX)) {
            throw new CommonException("validation.phone.incorrect", phone);
        }
    }

    /**
     * 校验邮箱格式是否正确
     *
     * @param email 邮箱号
     */
    public static void validateEmail(String email) {
        if (StringUtils.isBlank(email) || email.length() > 64 || !email.matches(EMAIL_REGEX)) {
            throw new CommonException("validation.email.incorrect", email);
        }
    }


}
