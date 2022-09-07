package io.choerodon.mybatis.spring.resolver;

import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault.SortDefaults;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.mybatis.pagehelper.domain.Sort.Direction;
import io.choerodon.mybatis.pagehelper.domain.Sort.Order;
import io.choerodon.mybatis.util.SqlSafeUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;


/**
 * source :　org.springframework.data.web
 *
 * @author NaccOll
 * 2018/1/30
 **/
@Component
public class SortHandlerMethodArgumentResolver implements SortArgumentResolver {
    private static final String DEFAULT_PARAMETER = "sort";
    private static final String DEFAULT_PROPERTY_DELIMITER = ",";
    private static final String DEFAULT_QUALIFIER_DELIMITER = "_";
    private static final Sort DEFAULT_SORT = null;

    private static final String SORT_DEFAULTS_NAME = SortDefaults.class.getSimpleName();
    private static final String SORT_DEFAULT_NAME = SortDefault.class.getSimpleName();

    private Sort fallbackSort = DEFAULT_SORT;
    private String sortParameter = DEFAULT_PARAMETER;
    private String propertyDelimiter = DEFAULT_PROPERTY_DELIMITER;
    private String qualifierDelimiter = DEFAULT_QUALIFIER_DELIMITER;

    public Sort getFallbackSort() {
        return fallbackSort;
    }

    public void setFallbackSort(Sort fallbackSort) {
        this.fallbackSort = fallbackSort;
    }

    public String getSortParameter() {
        return sortParameter;
    }

    protected String getSortParameter(MethodParameter parameter) {

        StringBuilder builder = new StringBuilder();

        if (parameter != null && parameter.hasParameterAnnotation(Qualifier.class)) {
            builder.append(parameter.getParameterAnnotation(Qualifier.class).value()).append(qualifierDelimiter);
        }

        return builder.append(sortParameter).toString();
    }

    public void setSortParameter(String sortParameter) {
        this.sortParameter = sortParameter;
    }

    public String getPropertyDelimiter() {
        return propertyDelimiter;
    }

    public void setPropertyDelimiter(String propertyDelimiter) {
        this.propertyDelimiter = propertyDelimiter;
    }

    public String getQualifierDelimiter() {
        return qualifierDelimiter;
    }

    public void setQualifierDelimiter(String qualifierDelimiter) {
        this.qualifierDelimiter = qualifierDelimiter;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Sort.class.equals(parameter.getParameterType());
    }

    @Override
    public Sort resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        String[] directionParameter = webRequest.getParameterValues(getSortParameter(parameter));

        // No parameter
        if (directionParameter == null) {
            return getDefaultFromAnnotationOrFallback(parameter);
        }
        // Single empty parameter, e.g "sort="
        if (directionParameter.length == 1 && !StringUtils.hasText(directionParameter[0])) {
            return getDefaultFromAnnotationOrFallback(parameter);
        }
        Sort sort = parseParameterIntoSort(directionParameter);
        SqlSafeUtil.validate(sort);
        return sort;
    }

    private Sort getDefaultFromAnnotationOrFallback(MethodParameter parameter) {

        SortDefaults annotatedDefaults = parameter.getParameterAnnotation(SortDefaults.class);
        SortDefault annotatedDefault = parameter.getParameterAnnotation(SortDefault.class);

        if (annotatedDefault != null && annotatedDefaults != null) {
            throw new IllegalArgumentException(
                    String.format("Cannot use both @%s and @%s on parameter %s! Move %s into %s to define sorting order!",
                            SORT_DEFAULTS_NAME, SORT_DEFAULT_NAME, parameter.toString(),
                            SORT_DEFAULT_NAME, SORT_DEFAULTS_NAME));
        }

        if (annotatedDefault != null) {
            return appendOrCreateSortTo(annotatedDefault, null);
        }

        if (annotatedDefaults != null) {
            Sort sort = null;
            for (SortDefault currentAnnotatedDefault : annotatedDefaults.value()) {
                sort = appendOrCreateSortTo(currentAnnotatedDefault, sort);
            }
            return sort;
        }

        return fallbackSort;
    }

    private Sort appendOrCreateSortTo(SortDefault sortDefault, Sort sortOrNull) {

        String[] fields = getSpecificPropertyOrDefaultFromValue(sortDefault, "sort");

        if (fields.length == 0) {
            return null;
        }

        Sort sort = new Sort(sortDefault.direction(), fields);
        return sortOrNull == null ? sort : sortOrNull.and(sort);
    }

    Sort parseParameterIntoSort(String[] source) {
        List<Order> allOrders = new ArrayList<>();
        for (String part : source) {
            if (part != null) {
                String[] elements = part.split(propertyDelimiter);
                Direction direction =
                        elements.length == 0 ? null : Direction.fromStringOrNull(elements[elements.length - 1]);
                setAllOrders(elements, allOrders, direction);
            }
        }
        return allOrders.isEmpty() ? null : new Sort(allOrders);
    }

    private void setAllOrders(final String[] elements, final List<Order> allOrders, final Direction direction) {
        for (int i = 0; i < elements.length; i++) {
            if (i != elements.length - 1 || direction == null) {
                String property = elements[i];
                if (!StringUtils.hasText(property)) {
                    continue;
                }
                allOrders.add(new Order(direction, property));
            }
        }
    }

    /**
     * getSpecificPropertyOrDefaultFromValue
     *
     * @param annotation annotation
     * @param property   property
     * @param <T>        T
     * @return T T
     */
    public static <T> T getSpecificPropertyOrDefaultFromValue(Annotation annotation, String property) {

        Object propertyDefaultValue = AnnotationUtils.getDefaultValue(annotation, property);
        Object propertyValue = AnnotationUtils.getValue(annotation, property);

        return (T) (ObjectUtils.nullSafeEquals(propertyDefaultValue, propertyValue)
                ? AnnotationUtils.getValue(annotation) : propertyValue);
    }
}
