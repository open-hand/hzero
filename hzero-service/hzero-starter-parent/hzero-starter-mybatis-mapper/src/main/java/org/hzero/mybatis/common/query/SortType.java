/**
 * Copyright 2016 www.extdo.com
 */
package org.hzero.mybatis.common.query;

/**
 * @author njq.niu@hand-china.com
 */
public enum SortType {

    ASC("ASC"), DESC("DESC");

    private String sql;

    private SortType(String sql) {
        this.sql = sql;
    }

    public String sql() {
        return " " + this.sql + " ";
    }

}
