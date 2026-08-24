package org.hzero.boot.oauth.util;

import org.springframework.security.crypto.password.PasswordEncoder;

import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * @author bojiangzhou 2019/08/07
 */
public class PasswordUtils {

    private static PasswordEncoder passwordEncoder;

    static {
        ApplicationContextHelper.asyncStaticSetter(PasswordEncoder.class, PasswordUtils.class, "passwordEncoder");
    }

    /**
     * 判断密码是否匹配
     *
     * @param rawPassword     明文密码
     * @param encodedPassword 加密后的密码
     * @return true - 匹配
     */
    public static boolean checkPasswordMatch(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 加密面膜
     *
     * @param rawPassword 明文密码
     * @return 加密后的密码
     */
    public static String encodedPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

}
