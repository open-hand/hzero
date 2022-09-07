package org.hzero.export.annotation;

/**
 * 默认空字体
 *
 * @author XCXCXCXCX
 * @date 2020/5/9 3:19 下午
 */
public class EmptyFont implements Font {

    @Override
    public org.apache.poi.ss.usermodel.Font getFont(org.apache.poi.ss.usermodel.Font origin) {
        return null;
    }
}
