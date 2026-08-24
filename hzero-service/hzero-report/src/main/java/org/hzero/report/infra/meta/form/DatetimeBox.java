package org.hzero.report.infra.meta.form;

/**
 * 日期时间框
 *
 * @author shuangfei.zhu@hand-china.com 2019/06/12 19:40
 */
public class DatetimeBox extends FormElement {

    private final String value;

    public DatetimeBox(String name, String meaning, String value) {
        super(name, meaning);
        this.type = "DatetimePicker";
        this.value = value;
        this.dataType = "date";
    }

    public String getValue() {
        return this.value;
    }
}
