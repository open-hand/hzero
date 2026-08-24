package org.hzero.core.captcha;

import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;


/**
 * 验证码生成器
 *
 * @author bojiangzhou 2018/08/08
 */
public class CaptchaGenerator {

    private CaptchaGenerator() {}

    private static final char[] NUMBERS = new char[10];
    private static final char[] CHARS = new char[('9' - '0' + 1) + ('z' - 'a' + 1) + ('Z' - 'A' + 1)];

    static {
        int c = 0;
        for (int i = 0; i < 10; i++) {
            NUMBERS[i] = (char) (i + 48);
            CHARS[c++] = NUMBERS[i];
        }
        for (int i = 0, len = 'Z' - 'A' + 1; i < len; i++) {
            CHARS[c++] = (char) ('A' + i);
        }
        for (int i = 0, len = 'z' - 'a' + 1; i < len; i++) {
            CHARS[c++] = (char) ('a' + i);
        }
    }

    /**
     * @return 6位数字验证码
     */
    public static String generateNumberCaptcha() {
        return generateNumberCaptcha(6);
    }

    /**
     * @param count 位数
     * @return 指定位数的数字验证码
     */
    public static String generateNumberCaptcha(int count) {
        return RandomStringUtils.random(count, 0, NUMBERS.length - 1, false, true, NUMBERS);
    }

    /**
     * @param count 位数
     * @return 指定位数的数字验证码
     */
    public static String generateNumberCaptcha(int count, char[] source) {
        return RandomStringUtils.random(count, 0, source.length - 1, false, true, source);
    }

    /**
     * @param count 位数
     * @return 指定位数的字符验证码
     */
    public static String generateCharCaptcha(int count) {
        return RandomStringUtils.random(count, 0, CHARS.length - 1, true, true, CHARS);
    }

    /**
     * @return 验证码KEY
     */
    public static String generateCaptchaKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
