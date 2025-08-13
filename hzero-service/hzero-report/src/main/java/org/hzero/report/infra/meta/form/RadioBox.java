package org.hzero.report.infra.meta.form;

import java.util.List;

/**
 * 单选框
 *
 * @author shuangfei.zhu@hand-china.com 2020/08/06 14:19
 */
public class RadioBox extends FormElement {

    private final List<SelectOption> value;

    public RadioBox(String name, String meaning, List<SelectOption> value) {
        super(name, meaning);
        this.type = "Radiobox";
        this.value = value;
    }

    public List<SelectOption> getValue() {
        return value;
    }
}
