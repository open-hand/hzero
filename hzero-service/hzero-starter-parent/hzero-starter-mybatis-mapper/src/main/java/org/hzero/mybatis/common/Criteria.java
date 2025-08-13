package org.hzero.mybatis.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.choerodon.mybatis.domain.AuditDomain;
import org.hzero.mybatis.common.query.*;

/**
 * @author njq.niu@hand-china.com
 */
public class Criteria {

    private List<Selection> selectFields;

    private List<SortField> sortFields;

    private List<WhereField> whereFields;

    private List<String> excludeSelectFields;

    private List<String> updateFields;

    public Criteria() {
    }

    public Criteria(Object obj) {

    }

    public Criteria sort(String field, SortType sortType) {
        if (sortFields == null) sortFields = new ArrayList<>();
        if (!containField(sortFields, field))
            sortFields.add(new SortField(field, sortType));
        return this;
    }

    public Criteria select(String... fields) {
        excludeSelectFields = null;
        if (selectFields == null) selectFields = new ArrayList<>(50);
        // FIXME: 这是个啥
        if (fields.length > 0 && !selectFields.contains(AuditDomain.FIELD_OBJECT_VERSION_NUMBER)) {
            selectFields.add(new Selection(AuditDomain.FIELD_OBJECT_VERSION_NUMBER));
        }
        for (String field : fields) {
            if (!containField(selectFields, field))
                selectFields.add(new Selection(field));
        }
        return this;
    }

    public Criteria unSelect(String... fields) {
        selectFields = null;
        if (excludeSelectFields == null) excludeSelectFields = new ArrayList<>(50);
        for (String field : fields) {
            if (!excludeSelectFields.contains(field))
                excludeSelectFields.add(field);
        }
        return this;
    }

    public Criteria where(Object... fields) {
        for (Object field : fields) {
            if (field instanceof WhereField) {
                where((WhereField) field);
            } else if (field instanceof String) {
                where((String) field);
            }
        }
        return this;
    }

    public Criteria where(WhereField... fields) {
        if (whereFields == null) whereFields = new ArrayList<>(15);
        for (WhereField field : fields) {
            if (!whereFields.contains(field))
                whereFields.add(field);
        }
        return this;
    }

    public Criteria where(String... fields) {
        if (whereFields == null) whereFields = new ArrayList<>(15);
        for (String field : fields) {
            if (!whereFields.contains(field))
                whereFields.add(new WhereField(field));
        }
        return this;
    }


    private boolean containField(List<? extends SQLField> list, String field) {
        boolean found = false;
        for (SQLField sqlField : list) {
            if (sqlField.getField().equals(field)) {
                found = true;
                break;
            }
        }
        return found;
    }

    public void update(String... fields) {
        if (updateFields == null) {
            updateFields = new ArrayList<>(50);
        }
        if (fields.length > 0 && !updateFields.contains(AuditDomain.FIELD_LAST_UPDATE_DATE)) {
            updateFields.addAll(Arrays.asList(AuditDomain.FIELD_LAST_UPDATE_DATE, AuditDomain.FIELD_LAST_UPDATED_BY));
        }
        for (String field : fields) {
            if (!updateFields.contains(field)) {
                updateFields.add(field);
            }
        }
    }

    public List<String> getUpdateFields() {
        return updateFields;
    }

    public List<Selection> getSelectFields() {
        return selectFields;
    }

    public void setSelectFields(List<Selection> selectFields) {
        this.selectFields = selectFields;
    }

    public List<SortField> getSortFields() {
        return sortFields;
    }

    public void setSortFields(List<SortField> sortFields) {
        this.sortFields = sortFields;
    }

    public List<WhereField> getWhereFields() {
        return whereFields;
    }

    public void setWhereFields(List<WhereField> whereFields) {
        this.whereFields = whereFields;
    }

    public List<String> getExcludeSelectFields() {
        return excludeSelectFields;
    }

    public void setExcludeSelectFields(List<String> excludeSelectFields) {
        this.excludeSelectFields = excludeSelectFields;
    }
}
