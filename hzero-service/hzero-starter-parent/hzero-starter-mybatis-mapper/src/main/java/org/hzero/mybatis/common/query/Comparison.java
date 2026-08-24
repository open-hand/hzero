/**
 * Copyright 2016 www.extdo.com
 */
package org.hzero.mybatis.common.query;

/**
 * @author njq.niu@hand-china.com
 */
public enum Comparison {

    EQUAL(" = "),
    NOT_EQUAL(" <> "),
    GREATER_THAN(" > "),
    GREATER_THAN_OR_EQUALTO(" >="),
    LESS_THAN(" < "),
    LESS_THAN_OR_EQUALTO(" <= "),
    LIKE(" LIKE CONCAT(''%'', CONCAT({0}, ''%'')) "),
    BETWEEN(" BETWEEN "),
    IN(" IN "),
    IS_NULL(" IS NULL"),
    IS_NOT_NULL(" IS NOT NULL");

    private String sql;

    private Comparison(String sql) {
        this.sql = sql;
    }

    public String sql() {
        return this.sql;
    }
}
