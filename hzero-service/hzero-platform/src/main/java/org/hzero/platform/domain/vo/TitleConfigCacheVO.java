package org.hzero.platform.domain.vo;

/**
 * 系统配置标题缓存VO
 *
 * @author xiaoyu.zhao@hand-china.com 2020/09/08 20:35
 */
public class TitleConfigCacheVO {
    private String lang;
    private String configValue;

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    @Override
    public String toString() {
        return "TitleConfigCacheVO{" + "lang='" + lang + '\'' + ", configValue='" + configValue + '\'' + '}';
    }
}
