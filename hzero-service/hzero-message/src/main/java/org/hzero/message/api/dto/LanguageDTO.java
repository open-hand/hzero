package org.hzero.message.api.dto;

/**
 * 语言
 *
 * @author shuangfei.zhu@hand-china.com 2018/11/15 10:44
 */
public class LanguageDTO {

    private String lang;
    private String langMeaning;

    public String getLang() {
        return lang;
    }

    public LanguageDTO setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public String getLangMeaning() {
        return langMeaning;
    }

    public LanguageDTO setLangMeaning(String langMeaning) {
        this.langMeaning = langMeaning;
        return this;
    }
}
