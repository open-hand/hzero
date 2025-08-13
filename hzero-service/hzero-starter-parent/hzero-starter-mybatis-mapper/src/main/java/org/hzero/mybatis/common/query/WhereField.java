package org.hzero.mybatis.common.query;

/**
 * @author njq.niu@hand-china.com
 */
public class WhereField {

    private String field;

    private Comparison comparison;

    private String expression;

    public WhereField(String field) {
        setField(field);
    }

    public WhereField(String field, Comparison comparison) {
        setField(field);
        setComparison(comparison);
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Comparison getComparison() {
        return comparison;
    }

    public void setComparison(Comparison comparison) {
        this.comparison = comparison;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
