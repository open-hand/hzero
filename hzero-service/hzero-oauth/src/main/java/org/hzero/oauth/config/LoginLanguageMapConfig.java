package org.hzero.oauth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 浏览器语言与系统语言之间的映射维护
 *
 * @author xianzhi.chen@hand-china.com 2021/07/13 10:50
 */
@Component
@ConfigurationProperties(prefix = LoginLanguageMapConfig.PREFIX)
public class LoginLanguageMapConfig {

    public static final String PREFIX = "hzero.oauth.login";

    private Map<String, String> langMap = new HashMap<>();

    public LoginLanguageMapConfig() {
        // 日语
        langMap.put("ja", "ja_JP");
        // 韩语
        langMap.put("ko", "ko_KR");
        // 泰语
        langMap.put("th", "th_TH");
    }

    public Map<String, String> getLangMap() {
        return langMap;
    }

    public void setLangMap(Map<String, String> langMap) {
        this.langMap.putAll(langMap);
    }
}
