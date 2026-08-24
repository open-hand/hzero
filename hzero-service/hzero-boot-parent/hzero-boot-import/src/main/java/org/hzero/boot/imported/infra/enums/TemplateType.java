package org.hzero.boot.imported.infra.enums;

/**
 * 模板类型枚举
 *
 * @author shuangfei.zhu@hand-china.com 2018/12/07 10:03
 */
@SuppressWarnings("unused")
public enum TemplateType {

    /**
     * 模板类型
     */
    SERVER("S"),

    CLIENT("C");

    private final String value;

    TemplateType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "TemplateType{" +
                "value='" + value + '\'' +
                "} " + super.toString();
    }
}
