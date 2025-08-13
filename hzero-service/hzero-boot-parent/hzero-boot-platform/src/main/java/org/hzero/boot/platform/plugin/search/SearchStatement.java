package org.hzero.boot.platform.plugin.search;

import io.choerodon.mybatis.helper.EntityHelper;
import org.hzero.boot.platform.plugin.search.entity.SearchConditions;
import org.hzero.boot.platform.plugin.search.entity.SearchOrders;
import org.hzero.boot.platform.plugin.search.entity.SearchRequest;
import org.hzero.core.util.FieldNameUtils;
import org.hzero.mybatis.domian.Condition;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class SearchStatement {
    private List<Criterion> criterionList;
    private List<OrderBy> orderByList;

    private SearchStatement() {
        this.criterionList = new ArrayList<>();
        this.orderByList = new ArrayList<>();
    }

    public static SearchStatementBuilder builder(SearchRequest searchRequest) {
        return new SearchStatementBuilder(searchRequest);
    }

    public static class SearchStatementBuilder {
        private SearchRequest searchRequest;
        private Map<String, String> fieldTableAbbrMap;
        private Map<String, String> fieldColumnMap;
        private Map<String, Class<?>> fieldTypeMap;

        public SearchStatementBuilder(SearchRequest searchRequest) {
            this.searchRequest = searchRequest;
            this.fieldTableAbbrMap = new HashMap<>();
            this.fieldColumnMap = new HashMap<>();
            this.fieldTypeMap = new HashMap<>();
        }

        public SearchStatementBuilder handleEntity(Class<?> entityClass) {
            return handleEntity(null, entityClass);
        }

        public SearchStatementBuilder handleEntity(String tableAbbr, Class<?> entityClass) {
            Assert.isTrue(EntityHelper.contain(entityClass), "Class " + entityClass + " is't an entity.");
            EntityHelper.getColumns(entityClass).forEach(entityColumn -> {
                fieldTableAbbrMap.put(entityColumn.getField().getName(), tableAbbr);
                fieldColumnMap.put(entityColumn.getField().getName(), entityColumn.getColumn());
                fieldTypeMap.put(entityColumn.getField().getName(), entityColumn.getField().getJavaType());
            });
            return this;
        }

        public SearchStatementBuilder handleTableAbbr(String fieldName, String tableAbbr) {
            fieldTableAbbrMap.put(fieldName, tableAbbr);
            return this;
        }


        public SearchStatementBuilder handleColumn(String fieldName, String column) {
            fieldColumnMap.put(fieldName, column);
            return this;
        }

        public SearchStatementBuilder handleType(String fieldName, Class<?> type) {
            fieldTypeMap.put(fieldName, type);
            return this;
        }

        public SearchStatement builder() {
            SearchStatement searchStatement = new SearchStatement();
            if (searchRequest == null) {
                return searchStatement;
            }
            if (searchRequest.getConditions() != null && !CollectionUtils.isEmpty(searchRequest.getConditions().getConditions())) {
                for (SearchConditions.Condition condition : searchRequest.getConditions().getConditions()) {
                    searchStatement.getCriterionList()
                            .add(condition.getComparator().getCriterion()
                                    .apply(getColumn(condition.getFieldName(), true), size -> {
                                        if (size < 0) {
                                            String[] values = condition.getValue().split(",");
                                            if (values.length < -size) {
                                                throw new IllegalArgumentException(String.format("Values size must not less than %d, [%s]", -size, condition.getValue()));
                                            }
                                            return Arrays.stream(values).map(value -> getValue(condition.getFieldName(), value)).collect(Collectors.toList());
                                        } else if (size == 0) {
                                            return null;
                                        } else if (size == 1) {
                                            return getValue(condition.getFieldName(), condition.getValue());
                                        } else {
                                            String[] values = condition.getValue().split(",");
                                            if (values.length != size) {
                                                throw new IllegalArgumentException(String.format("Values size must not equal %d, [%s]", -size, condition.getValue()));
                                            }
                                            return Arrays.stream(values).map(value -> getValue(condition.getFieldName(), value)).collect(Collectors.toList());
                                        }
                                    }));
                }
            }
            if (searchRequest.getOrders() != null && !CollectionUtils.isEmpty(searchRequest.getOrders().getOrders())) {
                for (SearchOrders.Order order : searchRequest.getOrders().getOrders()) {
                    searchStatement.getOrderByList()
                            .add(new OrderBy(getColumn(order.getFieldName(), false), order.getDirection().name()));
                }
            }
            return searchStatement;
        }

        private Object getValue(String fieldName, String value) {
            if (fieldTypeMap.containsKey(fieldName)) {
                return SearchValueProcessor.process(value, fieldTypeMap.get(fieldName));
            }
            return value;
        }

        private String getColumn(String fieldName, boolean appendAbbr) {
            String column = fieldColumnMap.containsKey(fieldName)
                    ? fieldColumnMap.get(fieldName)
                    : FieldNameUtils.camel2Underline(fieldName, false);
            if (!appendAbbr) {
                return column;
            }
            String abbr = fieldTableAbbrMap.get(fieldName);
            if (abbr != null) {
                column = abbr + "." + column;
            }
            return column;
        }
    }

    public static class Criterion extends Condition.Criterion {

        public Criterion(String condition) {
            super(condition);
        }

        public Criterion(String condition, Object value, String typeHandler) {
            super(condition, value, typeHandler);
        }

        public Criterion(String condition, Object value) {
            super(condition, value);
        }

        public Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super(condition, value, secondValue, typeHandler);
        }

        public Criterion(String condition, Object value, Object secondValue) {
            super(condition, value, secondValue);
        }

        public Criterion(String condition, boolean isOr) {
            super(condition, isOr);
        }

        public Criterion(String condition, Object value, String typeHandler, boolean isOr) {
            super(condition, value, typeHandler, isOr);
        }

        public Criterion(String condition, Object value, boolean isOr) {
            super(condition, value, isOr);
        }

        public Criterion(String condition, Object value, Object secondValue, String typeHandler, boolean isOr) {
            super(condition, value, secondValue, typeHandler, isOr);
        }

        public Criterion(String condition, Object value, Object secondValue, boolean isOr) {
            super(condition, value, secondValue, isOr);
        }
    }

    public static class OrderBy {
        private String columnName;
        private String direction;

        public OrderBy(String columnName, String direction) {
            this.columnName = columnName;
            this.direction = direction;
        }

        public String getColumnName() {
            return columnName;
        }

        public OrderBy setColumnName(String columnName) {
            this.columnName = columnName;
            return this;
        }

        public String getDirection() {
            return direction;
        }

        public OrderBy setDirection(String direction) {
            this.direction = direction;
            return this;
        }
    }

    public List<Criterion> getCriterionList() {
        return criterionList;
    }

    public List<OrderBy> getOrderByList() {
        return orderByList;
    }
}
