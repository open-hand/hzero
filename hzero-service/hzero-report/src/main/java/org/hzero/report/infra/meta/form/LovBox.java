package org.hzero.report.infra.meta.form;

/**
 * 值列表框
 *
 * @author xianzhi.chen@hand-china.com 2018年11月30日下午1:21:44
 */
public class LovBox extends FormElement {

    private final String value;
    private final String valueSource;

    public LovBox(String name, String meaning, String value, String content) {
        super(name, meaning);
        this.type = "Lov";
        this.value = value;
        this.valueSource = content;
    }

    public String getValue() {
        return this.value;
    }

    public String getValueSource() {
        return valueSource;
    }
}
