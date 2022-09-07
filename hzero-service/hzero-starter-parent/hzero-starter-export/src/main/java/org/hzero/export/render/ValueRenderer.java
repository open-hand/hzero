package org.hzero.export.render;

/**
 * 数据渲染器
 */
public interface ValueRenderer {
    
    Object render(Object value, Object data);

}
