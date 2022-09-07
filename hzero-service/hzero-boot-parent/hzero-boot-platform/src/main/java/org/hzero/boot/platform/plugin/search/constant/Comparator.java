package org.hzero.boot.platform.plugin.search.constant;

import org.hzero.boot.platform.plugin.search.SearchStatement;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public enum Comparator {
    /**
     * 等于
     */
    EQUAL(value -> sizeEqual(value, 1), (fieldName, getValue) -> new SearchStatement.Criterion(fieldName + " = ", getValue.apply(1))),
    /**
     * 不等于
     */
    NOT_EQUAL(value -> sizeEqual(value, 1), (fieldName, getValue) -> new SearchStatement.Criterion(fieldName + " <> ", getValue.apply(1))),
    /**
     * 介于
     */
    BETWEEN(value -> sizeEqual(value, 2), (fieldName, getValue) -> {
        Iterator<?> iterator = ((Collection<?>) getValue.apply(2)).iterator();
        return new SearchStatement.Criterion(fieldName + " BETWEEN AND ", iterator.next(), iterator.next());
    }),
    /**
     * 大于
     */
    GREATER_THAN(value -> sizeEqual(value, 1), (fieldName, getValue) -> new SearchStatement.Criterion(fieldName + " > ", getValue.apply(1))),
    /**
     * 大于等于
     */
    GREATER_THAN_OR_EQUAL_TO(value -> sizeEqual(value, 1), (fieldName, getValue) -> new SearchStatement.Criterion(fieldName + " >= ", getValue.apply(1))),
    /**
     * 小于
     */
    LESS_THAN(value -> sizeEqual(value, 1), (fieldName, getValue) -> new SearchStatement.Criterion(fieldName + " < ", getValue.apply(1))),
    /**
     * 小于等于
     */
    LESS_THAN_OR_EQUAL_TO(value -> sizeEqual(value, 1), (fieldName, getValue) -> new SearchStatement.Criterion(fieldName + " <= ", getValue.apply(1))),
    /**
     * 包含于
     */
    IN(value -> sizeAtLeast(value, 1), (fieldName, getValue) -> new SearchStatement.Criterion(fieldName + " IN ", getValue.apply(-1))),
    /**
     * 不包含于
     */
    NOT_IN(value -> sizeAtLeast(value, 1), (fieldName, getValue) -> new SearchStatement.Criterion(fieldName + " NOT IN ", getValue.apply(-1))),
    /**
     * 为空
     */
    IS_NULL(value -> sizeEqual(value, 0), (fieldName, getValue) -> new SearchStatement.Criterion(fieldName + " IS NULL")),
    /**
     * 不为空
     */
    IS_NOT_NULL(value -> sizeEqual(value, 0), (fieldName, getValue) -> new SearchStatement.Criterion(fieldName + " IS NOT NULL")),
    /**
     * 以此开始
     */
    START_WITH(value -> sizeEqual(value, 1), (fieldName, getValue) -> new SearchStatement.Criterion(fieldName + " LIKE ", wrap(String.valueOf(getValue.apply(1)), "%", null))),
    /**
     * 不以此开始
     */
    NOT_START_WITH(value -> sizeEqual(value, 1), (fieldName, getValue) -> new SearchStatement.Criterion(fieldName + " NOT LIKE ", wrap(String.valueOf(getValue.apply(1)), "%", null))),
    /**
     * 以此结束
     */
    END_WITH(value -> sizeEqual(value, 1), (fieldName, getValue) -> new SearchStatement.Criterion(fieldName + " LIKE ", wrap(String.valueOf(getValue.apply(1)), null, "%"))),
    /**
     * 不以此结束
     */
    NOT_END_WITH(value -> sizeEqual(value, 1), (fieldName, getValue) -> new SearchStatement.Criterion(fieldName + " NOT LIKE ", wrap(String.valueOf(getValue.apply(1)), null, "%"))),
    /**
     * 相似于
     */
    LIKE(value -> sizeEqual(value, 1), (fieldName, getValue) -> new SearchStatement.Criterion(fieldName + " LIKE ", wrap(String.valueOf(getValue.apply(1)), "%", "%"))),
    /**
     * 不相似于
     */
    NOT_LIKE(value -> sizeEqual(value, 1), (fieldName, getValue) -> new SearchStatement.Criterion(fieldName + " NOT LIKE ", wrap(String.valueOf(getValue.apply(1)), "%", "%")));

    private static final String VALUE_SPLIT = ",";
    private Predicate<String> valueValid;
    private BiFunction<String, Function<Integer, Object>, SearchStatement.Criterion> criterion;

    Comparator(Predicate<String> valueValid, BiFunction<String, Function<Integer, Object>, SearchStatement.Criterion> criterion) {
        this.valueValid = valueValid;
        this.criterion = criterion;
    }

    private static String wrap(String content, String left, String right) {
        if (left != null) {
            content = left + content;
        }
        if (right != null) {
            content = content + right;
        }
        return content;
    }

    public BiFunction<String, Function<Integer, Object>, SearchStatement.Criterion> getCriterion() {
        return criterion;
    }

    public static Comparator parseAndValid(String comparator, String value) {
        Comparator comparators = Comparator.valueOf(comparator.toUpperCase(Locale.US));
        Assert.isTrue(comparators.valueValid.test(value), "Operator parameter is invalid : " + comparators.name());
        return comparators;
    }

    private static boolean sizeEqual(String value, int size) {
        if (value == null) {
            return size == 0;
        }
        if (size == 1) {
            return StringUtils.hasText(value);
        }
        String[] values = value.split(VALUE_SPLIT);
        return values.length == size;
    }

    // 至少有 size 个参数
    private static boolean sizeAtLeast(String value, int size) {
        if (value == null) {
            return false;
        }
        String[] values = value.split(VALUE_SPLIT);
        return values.length >= size;
    }
}
