package org.hzero.mybatis.domian;

/**
 * <p>
 * 多语言字段
 * </p>
 *
 * @author qingsheng.chen 2018/9/16 星期日 14:57
 */
public class MultiLanguageField {
    private String lang;
    private String value;

    public String getLang() {
        return lang;
    }

    public MultiLanguageField setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public String getValue() {
        return value;
    }

    public MultiLanguageField setValue(String value) {
        this.value = value;
        return this;
    }
}
