package org.hzero.swagger.infra.util;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;

public class ManualPageHelper {

    private static final Logger logger = LoggerFactory.getLogger(ManualPageHelper.class);

    private static final String PARAMS_KEY = "params";

    private ManualPageHelper() {
    }

    public static <T> Page<T> postPage(final List<T> source, final PageRequest pageRequest, final Map<String, Object> filters) {
        return postPage(source, pageRequest, filters, defaultCompare(pageRequest));
    }

    public static <T> Page<T> postPage(final List<T> source, final PageRequest pageRequest,
                                       final Map<String, Object> filters, Comparator<T> comparable) {
        Page<T> page = new Page<>();
        page.setSize(pageRequest.getSize());
        page.setNumber(pageRequest.getPage());
        final List<T> filterList = source.stream().filter(t -> throughFilter(t, filters))
                .sorted(comparable).collect(Collectors.toList());
        List<T> pageList = getPageList(pageRequest.getPage(), pageRequest.getSize(), filterList);
        int pageSize = filterList.size() / pageRequest.getSize() + (filterList.size() % pageRequest.getSize() > 0 ? 1 : 0);
        page.setTotalPages(pageSize);
        page.setTotalElements(filterList.size());
        page.setNumberOfElements(pageList.size());
        page.setContent(pageList);
        return page;
    }

    private static <T> Comparator<T> defaultCompare(final PageRequest pageRequest) {
        return (T o1, T o2) -> {
            Sort sort = pageRequest.getSort();
            Iterator<Sort.Order> iterator = sort.iterator();
            if (iterator.hasNext()) {
                Sort.Order order = iterator.next();
                String property = order.getProperty();
                Class<?> objClass = o1.getClass();
                try {
                    Field field = objClass.getDeclaredField(property);
                    field.setAccessible(true);
                    if (field.getType().equals(String.class)) {
                        if (order.getDirection().isAscending()) {
                            return ((String) field.get(o1)).compareTo((String) field.get(o2));
                        } else {
                            return ((String) field.get(o2)).compareTo((String) field.get(o1));
                        }
                    }
                    if (field.getType().equals(Integer.class)
                            || field.getType().equals(Short.class)
                            || field.getType().equals(Character.class)
                            || field.getType().equals(Byte.class)) {
                        if (order.getDirection().isAscending()) {
                            return Integer.compare((Integer) field.get(o1), (Integer) field.get(o2));
                        } else {
                            return Integer.compare((Integer) field.get(o2), (Integer) field.get(o1));
                        }
                    }
                    if (field.getType().equals(Double.class) || field.getType().equals(Float.class)) {
                        if (order.getDirection().isAscending()) {
                            return Double.compare((Double) field.get(o1), (Double) field.get(o2));
                        } else {
                            return Double.compare((Double) field.get(o2), (Double) field.get(o1));
                        }
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    logger.debug("error in compare {}", e.getMessage());
                }
            }
            return 0;
        };
    }

    private static <T> List<T> getPageList(int page, int pageSize, List<T> source) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }
        int totalCount = source.size();
        int fromIndex = page * pageSize;
        if (fromIndex >= totalCount) {
            return Collections.emptyList();
        }
        int toIndex = ((page + 1) * pageSize);
        if (toIndex > totalCount) {
            toIndex = totalCount;
        }
        return source.subList(fromIndex, toIndex);
    }

    private static <T> boolean throughFilter(final T obj, final Map<String, Object> filters) {
        Class<?> objClass = obj.getClass();
        final Object params = filters.get(PARAMS_KEY);
        if (params != null) {
            return filters.entrySet().stream()
                    .filter(t -> !t.getKey().equals(PARAMS_KEY))
                    .anyMatch(i -> paramThrough(objClass, obj, i.getKey(), params));
        } else {
            return filters.entrySet().stream()
                    .filter(t -> t.getValue() != null).noneMatch(i -> notThrough(objClass, obj, i));
        }
    }

    private static <T> boolean paramThrough(final Class<?> objClass, final T obj, final String key, final Object params) {
        try {
            Field field = objClass.getDeclaredField(key);
            field.setAccessible(true);
            if (field.getType().equals(String.class) && params instanceof String) {
                final Object value = field.get(obj);
                if (value !=null && ((String)value).contains((String)params)) {
                    return true;
                }
            } else {
                if (params.equals(field.get(obj))) {
                    return true;
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.debug("error in throughFilter {}", e.getMessage());
        }
        return false;
    }

    private static <T> boolean notThrough(final Class<?> objClass, final T obj, final Map.Entry<String, Object> i) {
        try {
            Field field = objClass.getDeclaredField(i.getKey());
            field.setAccessible(true);
            if (field.getType().equals(String.class) && i.getValue() instanceof String) {
                final Object value = field.get(obj);
                if (value ==null || !((String)value).toLowerCase().contains((String)i.getValue())) {
                    return true;
                }
            } else {
                if (i.getValue().equals(field.get(obj))) {
                    return true;
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.debug("error in throughFilter {}", e.getMessage());
        }
        return false;
    }
}
