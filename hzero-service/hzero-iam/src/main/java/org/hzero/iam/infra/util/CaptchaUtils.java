package org.hzero.iam.infra.util;

import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
import org.hzero.iam.infra.constant.Constants;

/**
 * 验证码工具类
 *
 * @author bojiangzhou 2018/07/03
 */
public class CaptchaUtils {

    private CaptchaUtils() {}

    /**
     * 生成手机验证码，默认6位数字
     * 
     * @return 手机验证
     */
    public static String generatePhoneCaptcha() {
        return generatePhoneCaptcha(6);
    }

    /**
     * 生成手机验证码，数字类型
     * 
     * @param count 验证码位数
     * @return 手机验证码
     */
    public static String generatePhoneCaptcha(int count) {
        return RandomStringUtils.random(count, 0, 9, false, true, Constants.NUMBERS);
    }

    /**
     * 生成邮箱验证码，默认6位数字
     *
     * @return 邮箱验证码
     */
    public static String generateEmailCaptcha() {
        return generateEmailCaptcha(6);
    }

    /**
     * 生成邮箱验证码，数字类型
     *
     * @param count 验证码位数
     * @return 邮箱验证码
     */
    public static String generateEmailCaptcha(int count) {
        return RandomStringUtils.random(count, 0, 8, false, true,
                        new char[] {'1', '2', '3', '4', '5', '6', '7', '8', '9'});
    }

    /**
     * @return 生成验证码key
     */
    public static String generateCaptchaKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
