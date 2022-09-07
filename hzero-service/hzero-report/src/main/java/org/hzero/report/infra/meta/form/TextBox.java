package org.hzero.report.infra.meta.form;

/**
 * 文本框
 *
 * @author xianzhi.chen@hand-china.com 2018年11月30日下午1:22:45
 */
public class TextBox extends FormElement {

    private final String value;

    public TextBox(String name, String meaning, String value) {
        super(name, meaning);
        this.type = "Input";
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
