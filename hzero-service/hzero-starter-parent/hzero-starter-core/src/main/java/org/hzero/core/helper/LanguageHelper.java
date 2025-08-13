package org.hzero.core.helper;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

/**
 * <p>
 * 获取用户语言工具类
 * </p>
 *
 * @author qingsheng.chen 2018/9/12 星期三 15:40
 */
public class LanguageHelper {
    private static volatile String defaultLanguage = "zh_CN";


    private LanguageHelper() {
    }

    public static void setDefaultLanguage(String lang) {
        LanguageHelper.defaultLanguage = lang;
    }

    public static String getDefaultLanguage() {
        return defaultLanguage;
    }

    /**
     * 根据当前登陆用户获取语言信息
     *
     * @return String
     */
    public static String language() {
        CustomUserDetails details = DetailsHelper.getUserDetails();
        String language = null;
        if (details != null) {
            language = details.getLanguage();
        }
        if (StringUtils.isBlank(language)) {
            return defaultLanguage;
        }
        return language;
    }

    public static Locale locale() {
        return LocaleUtils.toLocale(LanguageHelper.language());
    }
}
