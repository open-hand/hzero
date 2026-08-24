package org.hzero.export.annotation;

import org.apache.commons.lang3.StringUtils;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2021/02/07 9:53
 */
public interface ExpandProperty {

    /**
     * 获取格式掩码 覆盖注解的pattern属性
     *
     * @return 格式掩码
     */
    default String getPattern() {
        return StringUtils.EMPTY;
    }
}
