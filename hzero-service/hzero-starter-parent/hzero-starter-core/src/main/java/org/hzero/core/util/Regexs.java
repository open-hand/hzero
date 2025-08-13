package org.hzero.core.util;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.apache.commons.lang3.StringUtils;

import io.choerodon.core.convertor.ApplicationContextHelper;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.properties.CoreProperties;

/**
 * 正则表达式工具类
 */
public class Regexs {

    private static final AtomicBoolean phonePatternInit = new AtomicBoolean(false);
    private static Pattern phonePattern;

    private Regexs() {
    }

    /**
     * 整数
     */
    public static final String INTEGER = "^-?[1-9]\\d*$";

    /**
     * 正整数
     */
    public static final String Z_INDEX = "^[1-9]\\d*$";

    /**
     * 负整数
     */
    public static final String NEGATIVE_INTEGER = "^-[1-9]\\d*$";

    /**
     * 数字
     */
    public static final String NUMBER = "^([+-]?)\\d*\\.?\\d+$";

    /**
     * 正数
     */
    public static final String POSITIVE_NUMBER = "^[1-9]\\d*|0$";

    /**
     * 负数
     */
    public static final String NEGATIVE_NUMBER = "^-[1-9]\\d*|0$";

    /**
     * 浮点数
     */
    public static final String FLOAT = "^([+-]?)\\d*\\.\\d+$";

    /**
     * 正浮点数
     */
    public static final String POSITIVE_FLOAT = "^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*$";

    /**
     * 负浮点数
     */
    public static final String NEGATIVE_FLOAT = "^-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)$";

    /**
     * 邮件
     */
    public static final String EMAIL = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$";

    /**
     * 颜色
     */
    public static final String COLOR = "^[a-fA-F0-9]{6}$";

    /**
     * url
     */
    public static final String URL = "^http[s]?:\\/\\/([\\w-]+\\.)+[\\w-]+([\\w-./?%&=]*)?$";

    /**
     * 仅中文
     */
    public static final String CHINESE = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$";

    /**
     * 仅ACSII字符
     */
    public static final String ASCII = "^[\\x00-\\xFF]+$";

    /**
     * 邮编
     */
    public static final String ZIP_CODE = "^\\d{6}$";

    /**
     * 手机
     */
    public static final String MOBILE = "^134[0-8]\\d{7}$|^13[^4]\\d{8}$|^14[5-9]\\d{8}$|^15[^4]\\d{8}$|^16[6]\\d{8}$|^17[0-8]\\d{8}$|^18[\\d]{9}$|^19[89]\\d{8}$";

    /**
     * ip地址
     */
    public static final String IP4 = "^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)$";

    /**
     * 非空
     */
    public static final String NOT_EMPTY = "^\\S+$";

    /**
     * 图片
     */
    public static final String PICTURE = "(.*)\\.(jpg|bmp|gif|ico|pcx|jpeg|tif|png|raw|tga)$";

    /**
     * 压缩文件
     */
    public static final String RAR = "(.*)\\.(rar|zip|7zip|tgz)$";

    /**
     * 日期
     */
    public static final String DATE =
            "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$";

    /**
     * QQ号码
     */
    public static final String QQ_NUMBER = "^[1-9]*[1-9][0-9]*$";

    /**
     * 电话号码的函数(包括验证国内区号,国际区号,分机号)
     */
    public static final String TEL = "^(([0\\+]\\d{2,3}-)?(0\\d{2,3})-)?(\\d{7,8})(-(\\d{3,}))?$";

    /**
     * 字母
     */
    public static final String LETTER = "^[A-Za-z]+$";

    /**
     * 大写字母
     */
    public static final String LETTER_U = "^[A-Z]+$";

    /**
     * 小写字母
     */
    public static final String LETTER_I = "^[a-z]+$";

    /**
     * 禁止大写字母
     */
    public static final String LETTER_NOT_U = "^[^A-Z]+$";

    /**
     * 禁止小写字母
     */
    public static final String LETTER_NOT_L = "^[^a-z]+$";

    /**
     * 身份证
     */
    public static final String IDCARD = "^(\\d{15}$|^\\d{18}$|^\\d{17}(\\d|X|x))$";

    /**
     * 大小写编码
     */
    public static final String CODE = "^[a-zA-Z0-9][a-zA-Z0-9-_./]*$";

    /**
     * 大写编码
     */
    public static final String CODE_UPPER = "^[A-Z0-9][A-Z0-9-_./]*$";

    /**
     * 大写编码
     */
    public static final String CODE_LOWER = "^[a-z0-9][a-z0-9-_./]*$";

    /**
     * 数字
     */
    public static final String DIGITAL = "^[0-9]*$";

    /**
     * 密码强度：至少包含两种不同类型字符
     */
    public static final String PASSWORD = "^(?![0-9]+$)(?![a-zA-Z]+$)(?![a-z]+$)(?![!@#$%^&*=]+$)[0-9A-Za-z!@#$%^&*=]{6,30}$";

    /**
     * 匹配正则
     *
     * @param str   源字符串
     * @param regex 正则表达式
     */
    public static boolean is(String str, String regex) {
        return Pattern.matches(regex, str);
    }

    public static boolean isNumber(String str) {
        return is(str, Regexs.NUMBER);
    }

    public static boolean isEmail(String str) {
        return is(str, Regexs.EMAIL);
    }

    public static boolean isIP(String str) {
        return is(str, Regexs.IP4);
    }

    public static boolean isMobile(String str) {
        return isMobile(BaseConstants.DEFAULT_CROWN_CODE, str);
    }

    public static boolean isMobile(String crownCode, String phone) {
        Pattern phonePattern = getPhonePattern();
        if (phonePattern != null && StringUtils.endsWith(crownCode, "86")) {
            return phonePattern.matcher(phone).matches();
        }

        crownCode = StringUtils.defaultIfBlank(crownCode, BaseConstants.DEFAULT_CROWN_CODE);
        int code = 0;
        long telNumber = 0;
        try {
            code = Integer.parseInt(crownCode);
            telNumber = Long.parseLong(phone);
        } catch (NumberFormatException e) {
            return false;
        }
        Phonenumber.PhoneNumber phoneNumber = new Phonenumber.PhoneNumber();
        phoneNumber.setCountryCode(code);
        phoneNumber.setNationalNumber(telNumber);
        return PHONE_NUMBER_UTIL.isValidNumber(phoneNumber);
    }

    public static boolean isUrl(String str) {
        return is(str, Regexs.URL);
    }

    /**
     * @param regex 正则表达式
     * @param str   字符串
     * @return 匹配结果
     */
    public static Set<String> matchString(String regex, String str) {
        Set<String> result = new HashSet<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }

    /**
     * @param pattern pattern
     * @param str     字符串
     * @return 匹配结果
     */
    public static Set<String> matchString(Pattern pattern, String str) {
        Set<String> result = new HashSet<>();
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }


    private static final PhoneNumberUtil PHONE_NUMBER_UTIL = PhoneNumberUtil.getInstance();

    private static Pattern getPhonePattern() {
        if (!phonePatternInit.get()) {
            synchronized (Regexs.class) {
                CoreProperties properties = ApplicationContextHelper.getContext().getBean(CoreProperties.class);
                String reg = properties.getRegex().getPhone();
                if (StringUtils.isNotBlank(reg)) {
                    phonePattern = Pattern.compile(reg);
                }
                phonePatternInit.compareAndSet(false, true);
            }
        }
        return phonePattern;
    }
}
