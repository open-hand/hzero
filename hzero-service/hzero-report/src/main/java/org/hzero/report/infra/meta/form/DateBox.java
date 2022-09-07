package org.hzero.report.infra.meta.form;

/**
 * 日期框
 *
 * @author xianzhi.chen@hand-china.com 2018年11月30日下午1:21:44
 */
public class DateBox extends FormElement {

    private final String value;

    public DateBox(String name, String meaning, String value) {
        super(name, meaning);
        this.type = "DatePicker";
        this.value = value;
        this.dataType = "date";
    }

    public String getValue() {
        return this.value;
    }
}
