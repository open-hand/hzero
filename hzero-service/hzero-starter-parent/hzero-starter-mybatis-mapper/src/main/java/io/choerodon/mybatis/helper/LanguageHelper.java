package io.choerodon.mybatis.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.helper.feign.LanguageRemoteService;
import org.apache.commons.lang3.LocaleUtils;
import org.hzero.core.util.ResponseUtils;
import org.hzero.mybatis.domian.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * @author Created by xausky on 4/7/17.
 */
public class LanguageHelper {
    private static final Logger logger = LoggerFactory.getLogger(LanguageHelper.class);
    private static final ThreadLocal<String> language = new ThreadLocal<>();
    private static volatile List<Language> languageList;
    private static volatile LanguageRemoteService languageRemoteService;
    private static volatile String defaultLanguage = "zh_CN";
    private static final LanguageHelper SINGLE_INSTANCE = new LanguageHelper();

    private LanguageHelper() {
    }

    public static LanguageHelper getInstance() {
        return SINGLE_INSTANCE;
    }

    public static void setDefaultLanguage(String lang) {
        LanguageHelper.defaultLanguage = lang;
        org.hzero.core.helper.LanguageHelper.setDefaultLanguage(lang);
    }

    public static String getDefaultLanguage() {
        return defaultLanguage;
    }

    /**
     * 获取所有的语言
     *
     * @return 语言列表
     */
    public static List<Language> languages() {
        if (languageList == null) {
            loadLanguages();
        }
        return Optional.ofNullable(languageList)
                .orElseThrow(() -> new CommonException("error.mybatis.language.load"));
    }

    public static void resetLanguages(List<Language> languageList) {
        LanguageHelper.languageList = languageList;
    }

    /**
     * 根据当前登陆用户获取语言信息
     *
     * @return String
     */
    public static String language() {
        CustomUserDetails details = DetailsHelper.getUserDetails();
        if (details != null) {
            language(details.getLanguage());
        } else {
            if (language.get() == null) {
                language(defaultLanguage);
                logger.warn("principal not instanceof CustomUserDetails language is zh_CN");
            }
        }
        return language.get();
    }

    public static void language(String lang) {
        language.set(lang);
    }

    public static Locale locale() {
        return LocaleUtils.toLocale(LanguageHelper.language());
    }

    private static void loadLanguages() {
        logger.info("Loading languages...");
        languageList = ResponseUtils.getResponse(languageRemoteService().listLanguage(),
                new TypeReference<List<Language>>() {
                },
                (status, response) -> logger.error("Error get language from platform {} : {}", status, response),
                (exceptionResponse) -> logger.error("Error get language from platform failed is true : {}", exceptionResponse));
        if (CollectionUtils.isEmpty(languageList)) {
            logger.error("Language is empty!");
            languageList = null;
        }
        logger.info("Loaded languages, {}", languageList);
    }

    private static LanguageRemoteService languageRemoteService() {
        if (languageRemoteService == null) {
            synchronized (LanguageHelper.class) {
                if (languageRemoteService == null) {
                    languageRemoteService = ApplicationContextHelper.getContext().getBean(LanguageRemoteService.class);
                }
            }
        }
        return languageRemoteService;
    }
}
