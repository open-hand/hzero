package org.hzero.report.infra.meta.form;

import java.util.List;

/**
 * 下拉框
 *
 * @author xianzhi.chen@hand-china.com 2018年11月30日下午1:21:27
 */
public class ComboBox extends FormElement {

    private final List<SelectOption> value;
    private boolean multipled;

    public ComboBox(String name, String meaning, List<SelectOption> value) {
        super(name, meaning);
        this.type = "Select";
        this.value = value;
    }

    public boolean isMultipled() {
        return this.multipled;
    }

    public void setMultipled(boolean multipled) {
        this.multipled = multipled;
    }

    public List<SelectOption> getValue() {
        return this.value;
    }
}
