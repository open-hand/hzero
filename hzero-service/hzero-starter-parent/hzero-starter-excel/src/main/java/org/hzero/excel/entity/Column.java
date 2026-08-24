package org.hzero.excel.entity;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/02/04 10:48
 */
public class Column {

    /**
     * 字符串
     */
    public static final String STRING = "String";
    /**
     * 整数
     */
    public static final String LONG = "Long";
    /**
     * 浮点数
     */
    public static final String DECIMAL = "Decimal";
    /**
     * 日期
     */
    public static final String DATE = "Date";
    /**
     * 序列
     */
    public static final String SEQUENCE = "Sequence";

    private String name;
    private Integer index;
    private String format;
    private String columnType;

    public String getName() {
        return name;
    }

    public Column setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getIndex() {
        return index;
    }

    public Column setIndex(Integer index) {
        this.index = index;
        return this;
    }

    public String getFormat() {
        return format;
    }

    public Column setFormat(String format) {
        this.format = format;
        return this;
    }

    public String getColumnType() {
        return columnType;
    }

    public Column setColumnType(String columnType) {
        this.columnType = columnType;
        return this;
    }

    @Override
    public String toString() {
        return "Column{" +
                "name='" + name + '\'' +
                ", index=" + index +
                ", format='" + format + '\'' +
                ", columnType='" + columnType + '\'' +
                '}';
    }
}
