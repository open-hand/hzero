package org.hzero.export.annotation;

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

/**
 * 默认空颜色
 *
 * @author XCXCXCXCX
 * @date 2020/5/9 3:20 下午
 */
public class EmptyColor implements Color {

    @Override
    public IndexedColors getColor() {
        return null;
    }

    @Override
    public FillPatternType getFillPattern() {
        return null;
    }
}
