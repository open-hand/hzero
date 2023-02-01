package org.hzero.export.render;

/**
 * 数据渲染器
 *
 * @author bojiangzhou 2019/01/18
 */
public interface ValueRenderer {

    /**
     * 数据渲染
     *
     * @param value 数据
     * @param data  行数据
     * @return 渲染结果
     */
    Object render(Object value, Object data);
}