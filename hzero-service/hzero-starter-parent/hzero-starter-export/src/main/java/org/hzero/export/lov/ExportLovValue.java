package org.hzero.export.lov;

/**
 * 值集值
 *
 * @author shuangfei.zhu@hand-china.com 2020/12/25 10:22
 */
public class ExportLovValue {

    private String code;
    private String meaning;

    public String getCode() {
        return code;
    }

    public ExportLovValue setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMeaning() {
        return meaning;
    }

    public ExportLovValue setMeaning(String meaning) {
        this.meaning = meaning;
        return this;
    }
}
