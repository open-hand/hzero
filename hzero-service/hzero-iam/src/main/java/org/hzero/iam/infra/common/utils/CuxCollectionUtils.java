package org.hzero.iam.infra.common.utils;

import io.choerodon.core.exception.CommonException;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 描述
 * </p>
 *
 * @author mingwei.liu@hand-china.com 2018/7/16
 */
public class CuxCollectionUtils {
    /**
     * 深拷贝List列表, 要求类必须存在默认构造参数<br/>
     *
     * @param tList
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> deepCopy(List<T> tList, Class<T> clazz) {
        if (CollectionUtils.isEmpty(tList)) {
            return new ArrayList<T>();
        }

        List<T> resultList = new ArrayList<T>();
        tList.forEach(t -> {
            T resultItem = null;
            try {
                resultItem = clazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
                throw new CommonException(e);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new CommonException(e);
            }
            BeanUtils.copyProperties(t, resultItem);
            resultList.add(resultItem);
        });

        return resultList;
    }
}
