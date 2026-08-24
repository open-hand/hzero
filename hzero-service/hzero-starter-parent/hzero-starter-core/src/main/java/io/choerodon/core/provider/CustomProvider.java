package io.choerodon.core.provider;

import java.util.Set;

public interface CustomProvider {
    /**
     * 获取当前语言，使用建议使用ThreadLocal实现
     * @return 当前语言
     */
    String currentLanguage();
    /**
     * 获取当前访问主体ID，用户ID，ClientID (OAuth2 Client Authentication)
     * @return 当前语言
     */
    Long currentPrincipal();

    Set<String> getSupportedLanguages();

}
