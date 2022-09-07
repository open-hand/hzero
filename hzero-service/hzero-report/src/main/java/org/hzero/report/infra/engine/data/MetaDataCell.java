package org.hzero.report.infra.engine.data;

/**
 * 数据库元数据单元格
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午6:59:25
 */
public class MetaDataCell {
    private final MetaDataColumn column;
    private final String name;
    private final Object value;

    public MetaDataCell(MetaDataColumn column, String name, Object value) {
        this.column = column;
        this.name = name;
        this.value = value;
    }

    public MetaDataColumn getColumn() {
        return this.column;
    }

    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value;
    }
}
