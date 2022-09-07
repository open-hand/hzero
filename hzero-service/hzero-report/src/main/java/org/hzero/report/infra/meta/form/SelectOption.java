package org.hzero.report.infra.meta.form;

/**
 * 下拉框元素对象
 *
 * @author xianzhi.chen@hand-china.com 2018年11月30日下午1:22:24
 */
public class SelectOption {

    private String value;
    private String meaning;

    public SelectOption(String value, String meaning) {
        this.value = value;
        this.meaning = meaning;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMeaning() {
        return this.meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

}
