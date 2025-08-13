package io.choerodon.core.convertor;

import static io.choerodon.core.convertor.ConvertHelper.getDestinClassData;
import static io.choerodon.core.convertor.ConvertHelper.invokeConvert;

import java.util.ArrayList;
import java.util.List;

import io.choerodon.core.domain.Page;

/**
 * page的转换
 * 因为需要依赖于mybatis，所以独立写了一个类
 * @author flyleft
 * 2018/3/19
 */
public class ConvertPageHelper {

    private ConvertPageHelper() {
    }

    /**
     * page的转换
     *
     * @param pageSource 要转换的page对象
     * @param destin     要转换的目标类型的Class
     * @param <T>        要转换的目标类型
     * @return 转换的后page对象
     */
    public static <T> Page<T> convertPage(final Page pageSource, final Class<T> destin) {
        Page<T> pageBack = new Page<>();
        pageBack.setNumber(pageSource.getNumber());
        pageBack.setNumberOfElements(pageSource.getNumberOfElements());
        pageBack.setSize(pageSource.getSize());
        pageBack.setTotalElements(pageSource.getTotalElements());
        pageBack.setTotalPages(pageSource.getTotalPages());
        if (pageSource.getContent().isEmpty()) {
            return pageBack;
        }

        Class<?> source = pageSource.getContent().get(0).getClass();
        if (source.getTypeName().contains(ConvertHelper.SPRING_PROXY_CLASS)) {
            source = source.getSuperclass();
        }
        final ConvertHelper.DestinClassData destinClassData = getDestinClassData(source, destin);
        List<T> list = new ArrayList<>(pageSource.getContent().size());
        for (Object object : pageSource.getContent()) {
            T t = invokeConvert(destinClassData, object);
            list.add(t);
        }
        pageBack.setContent(list);
        return pageBack;
    }

}
