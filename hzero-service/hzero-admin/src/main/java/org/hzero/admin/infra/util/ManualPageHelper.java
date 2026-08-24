package org.hzero.admin.infra.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

import static java.util.stream.Collectors.toList;

import io.choerodon.core.domain.Page;
import io.choerodon.core.domain.PageInfo;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;

/**
 * list手动分页工具类
 *
 * @author modify by bo.he02@hand-china.com on 2020/05/12
 * @author flyleft
 * @author superlee
 */
public class ManualPageHelper {

    private static final Logger logger = LoggerFactory.getLogger(ManualPageHelper.class);

    private static final String PARAMS_KEY = "params";

    private ManualPageHelper() {
    }

    /**
     * 分页
     *
     * @param source      元数据
     * @param pageRequest 分页参数对象
     * @param filters     筛选条件
     * @param <T>         数据泛型
     * @return 分页结果
     */
    public static <T> Page<T> postPage(final List<T> source, PageRequest pageRequest, final Map<String, Object> filters) {
        // 最终的数据
        List<T> content = source;

        if (MapUtils.isNotEmpty(filters) && CollectionUtils.isNotEmpty(content)) {
            // 对数据进行筛选
            content = content.stream().filter(t -> throughFilter(t, filters))
                    .collect(toList());
        }

        if (CollectionUtils.isNotEmpty(content)) {
            // 排序方法
            Comparator<T> comparator = defaultCompare(pageRequest.getSort());
            if (Objects.nonNull(comparator)) {
                // 对数据进行排序
                content = content.stream()
                        .sorted(defaultCompare(pageRequest.getSort())).collect(toList());
            }
        }

        // 数据分页
        content = getPageList(pageRequest.getPage(), pageRequest.getSize(), content);
        // 构建结果对象
        return new Page<>(content, new PageInfo(pageRequest.getPage(), pageRequest.getSize()), source.size());
    }

    /**
     * 排序器
     *
     * @param sort 排序元数据
     * @param <T>  数据类型
     * @return 排序结果
     */
    private static <T> Comparator<T> defaultCompare(final Sort sort) {
        if (Objects.isNull(sort)) {
            return Comparator.comparingInt(Objects::hashCode);
        }

        // 根据排序条件，构建排序方法
        return (T o1, T o2) -> {
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

    /**
     * 获取分页数据
     *
     * @param page     分页页码
     * @param pageSize 分页大小
     * @param source   元数据
     * @param <T>      元数据泛型
     * @return 分页数据
     */
    private static <T> List<T> getPageList(int page, int pageSize, List<T> source) {
        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }
        int totalCount = source.size();
        page = Math.max(page, 0);
        int fromIndex = page * pageSize;
        if (fromIndex >= totalCount) {
            return Collections.emptyList();
        }
        int toIndex = fromIndex + pageSize;
        if (toIndex > totalCount) {
            toIndex = totalCount;
        }
        return source.subList(fromIndex, toIndex);
    }

    /**
     * @param obj
     * @param filters
     * @param <T>
     * @return
     */
    private static <T> boolean throughFilter(final T obj, final Map<String, Object> filters) {
        Class<?> objClass = obj.getClass();
        boolean allIsNullExcludeParams = true;
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            if (!PARAMS_KEY.equals(key) && !StringUtils.isBlank(String.valueOf(entry.getValue()))) {
                allIsNullExcludeParams = false;
                break;
            }
        }

        final Object params = filters.get(PARAMS_KEY);
        if (Objects.nonNull(params) && allIsNullExcludeParams) {
            return filters.entrySet().stream()
                    .filter(t -> !t.getKey().equals(PARAMS_KEY))
                    .anyMatch(i -> paramThrough(objClass, obj, i.getKey(), params));
        } else {
            return filters.entrySet().stream()
                    .filter(t -> Objects.nonNull(t.getValue()))
                    .noneMatch(i -> notThrough(objClass, obj, i));
        }
    }

    /**
     * 判断param参数是否通过筛选
     *
     * @param objClass 对象class
     * @param obj      对象
     * @param key      key
     * @param params   params
     * @param <T>      对象泛型
     * @return 是否通过筛选
     */
    private static <T> boolean paramThrough(final Class<?> objClass, final T obj, final String key, final Object params) {
        try {
            Field field = objClass.getDeclaredField(key);
            field.setAccessible(true);
            if (field.getType().equals(String.class) && params instanceof String) {
                final Object value = field.get(obj);
                if (value != null && ((String) value).contains((String) params)) {
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

    /**
     * 判断未通过筛选
     *
     * @param objClass 对象class
     * @param obj      对象
     * @param i        处理项
     * @param <T>      对象泛型
     * @return 筛选结果
     */
    private static <T> boolean notThrough(final Class<?> objClass, final T obj, final Map.Entry<String, Object> i) {
        try {
            Field field = objClass.getDeclaredField(i.getKey());
            field.setAccessible(true);
            if (field.getType().equals(String.class) && i.getValue() instanceof String) {
                final Object value = field.get(obj);
                if (value == null || !((String) value).toLowerCase().contains((String) i.getValue())) {
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