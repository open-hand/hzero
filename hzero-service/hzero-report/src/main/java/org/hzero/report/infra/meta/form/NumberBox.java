package org.hzero.report.infra.meta.form;

/**
 * 数字框
 *
 * @author xianzhi.chen@hand-china.com 2018年11月30日下午1:21:44
 */
public class NumberBox extends FormElement {

    private final String value;

    public NumberBox(String name, String meaning, String value) {
        super(name, meaning);
        this.type = "InputNumber";
        this.value = value;
        this.dataType = "float";
    }

    public String getValue() {
        return this.value;
    }
}
