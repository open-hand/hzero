package org.hzero.export.annotation;

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

/**
 * 颜色自定义接口
 *
 * @author XCXCXCXCX 2020/5/27 2:47
 */
public interface Color {
    /**
     * 使用什么颜色
     *
     * @return {@link IndexedColors}
     */
    IndexedColors getColor();

    /**
     * 填充格式
     *
     * @return {@link IndexedColors}
     */
    FillPatternType getFillPattern();
}
