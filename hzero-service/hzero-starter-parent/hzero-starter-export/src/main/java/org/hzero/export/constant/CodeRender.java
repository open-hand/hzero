package org.hzero.export.constant;

/**
 * 导出文件的字段名渲染方式，根据对应的导入模板解析模式选择
 *
 * @author shuangfei.zhu@hand-china.com 2020/10/26 11:47
 */
public enum CodeRender {

    /**
     * 不渲染编码
     */
    NONE,
    /**
     * 数据头部渲染
     */
    HEAD,
    /**
     * 隐藏sheet页渲染
     */
    SHEET;
}
