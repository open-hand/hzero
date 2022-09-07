/**
 * Copyright 2016 www.extdo.com
 */
package org.hzero.mybatis.common.query;

import java.util.Objects;

/**
 * @author njq.niu@hand-china.com
 */
public class Selection extends SQLField {

    private String expression;

    public Selection(String field) {
        this(field, null);
    }

    public Selection(String field, String expression) {
        super(field);
        setExpression(expression);
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

//    @Override
//    public String sql(BaseDTO dto) {
//        EntityColumn entityColumn = findColumn(dto.getClass());
//        StringBuilder sb = new StringBuilder();
//        if (entityColumn != null) {
//            EntityTable table = EntityHelper.getEntityTable(dto.getClass());
//            JoinColumn jc = entityColumn.getJoinColumn();
//            if (jc != null) {
//                EntityColumn joinField = table.getJoinMapping().get(jc.joinName());
//                if (joinField != null && joinField.getJoinTable() != null) {
//                    EntityTable joinTable = EntityHelper.getEntityTable(joinField.getJoinTable().target());
//                    EntityColumn refColumn = joinTable.findColumnByProperty(jc.field());
//                    sb.append(table.getAlias(joinField.getJoinKey())).append(".").append(refColumn.getColumn()).append(" AS ").append(entityColumn.getColumn());
//                }
//            } else {
//                sb.append(table.getAlias()).append(".").append(entityColumn.getColumn());
//            }
//        }
//        return sb.toString();
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Selection)) return false;
        if (!super.equals(o)) return false;
        Selection selection = (Selection) o;
        return Objects.equals(expression, selection.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression);
    }
}
