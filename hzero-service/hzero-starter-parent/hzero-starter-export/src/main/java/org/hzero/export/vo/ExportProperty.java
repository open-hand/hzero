package org.hzero.export.vo;

import org.hzero.export.constant.CodeRender;

/**
 * 导出过程中需要使用的参数
 *
 * @author shuangfei.zhu@hand-china.com 2021/08/12 10:24
 */
public class ExportProperty {

    /**
     * 编码渲染方式
     */
    private CodeRender codeRenderMode;

    public CodeRender getCodeRenderMode() {
        return codeRenderMode;
    }

    public ExportProperty setCodeRenderMode(CodeRender codeRenderMode) {
        this.codeRenderMode = codeRenderMode;
        return this;
    }
}
