package org.hzero.boot.platform.plugin.search.entity;

import org.hzero.boot.platform.plugin.search.constant.Comparator;

import java.util.ArrayList;
import java.util.List;

/**
 * 高级检索条件配置
 *
 * @author qingsheng.chen@hand-china.com 2020-03-05 09:25:52
 */
public class SearchConditions {
    private List<Condition> conditions;

    public SearchConditions(int initialCapacity) {
        this.conditions = new ArrayList<>(initialCapacity);
    }

    public void appendCondition(String fieldName, String comparator, String value) {
        if (this.conditions == null) {
            this.conditions = new ArrayList<>();
        }
        this.conditions.add(new Condition().setFieldName(fieldName).setComparator(Comparator.parseAndValid(comparator, value)).setValue(value));
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public SearchConditions setConditions(List<Condition> conditions) {
        this.conditions = conditions;
        return this;
    }

    public static class Condition {
        private String fieldName;
        private Comparator comparator;
        private String value;

        public String getFieldName() {
            return fieldName;
        }

        public Condition setFieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public Comparator getComparator() {
            return comparator;
        }

        public Condition setComparator(Comparator comparator) {
            this.comparator = comparator;
            return this;
        }

        public String getValue() {
            return value;
        }

        public Condition setValue(String value) {
            this.value = value;
            return this;
        }
    }
}
