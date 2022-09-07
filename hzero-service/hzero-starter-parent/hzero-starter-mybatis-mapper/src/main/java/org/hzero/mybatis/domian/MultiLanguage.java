package org.hzero.mybatis.domian;

/**
 * <p>
 * 多语言查询返回结构
 * </p>
 *
 * @author qingsheng.chen 2018/9/25 星期二 17:02
 */
public class MultiLanguage {
    private static final String EMPTY_STR = "";
    private String code;
    private String name;
    private String value;

    public String getCode() {
        return code;
    }

    public MultiLanguage setCode(String code) {
        this.code = code;
        return this;
    }

    public String getName() {
        return name;
    }

    public MultiLanguage setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        if (value == null) {
            return EMPTY_STR;
        }
        return value;
    }

    public MultiLanguage setValue(String value) {
        this.value = value;
        return this;
    }
}
