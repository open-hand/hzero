package org.hzero.report.infra.meta.form;

import java.util.List;

/**
 * 复选框
 *
 * @author xianzhi.chen@hand-china.com 2018年11月30日下午1:21:12
 */
public class CheckBox extends FormElement {

    private final List<SelectOption> value;

    public CheckBox(String name, String meaning, List<SelectOption> value) {
        super(name, meaning);
        this.type = "Checkbox";
        this.value = value;
    }

    public List<SelectOption> getValue() {
        return value;
    }
}