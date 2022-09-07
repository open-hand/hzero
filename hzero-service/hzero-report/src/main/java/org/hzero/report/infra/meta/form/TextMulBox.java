package org.hzero.report.infra.meta.form;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/08/06 17:07
 */
public class TextMulBox extends FormElement {

    private final String value;

    public TextMulBox(String name, String meaning, String value) {
        super(name, meaning);
        this.type = "MulInput";
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
