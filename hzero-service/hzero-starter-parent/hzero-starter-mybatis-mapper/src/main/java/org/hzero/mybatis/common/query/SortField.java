/**
 * Copyright 2016 www.extdo.com
 */
package org.hzero.mybatis.common.query;


import java.util.Objects;

/**
 * @author njq.niu@hand-china.com
 */
public class SortField extends SQLField {

    private SortType sortType;

    public SortField(String field, SortType SortType) {
        super(field);
        this.sortType = SortType;
    }

    public SortType getSortType() {
        return sortType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SortField)) return false;
        if (!super.equals(o)) return false;
        SortField sortField = (SortField) o;
        return sortType == sortField.sortType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sortType);
    }
}
