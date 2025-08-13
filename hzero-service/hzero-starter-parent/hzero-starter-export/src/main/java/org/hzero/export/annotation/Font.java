package org.hzero.export.annotation;

/**
 * 字体自定义接口
 *
 * @author XCXCXCXCX 2020/5/9 11:26
 */
public interface Font {

    /**
     * 使用什么字体
     *
     * @param origin 原字体
     * @return {@link org.apache.poi.ss.usermodel.Font}
     */
    org.apache.poi.ss.usermodel.Font getFont(org.apache.poi.ss.usermodel.Font origin);

}
