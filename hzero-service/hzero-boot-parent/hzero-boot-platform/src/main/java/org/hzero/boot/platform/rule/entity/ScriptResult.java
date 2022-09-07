package org.hzero.boot.platform.rule.entity;

/**
 * 结果对象
 *
 * @author shuangfei.zhu@hand-china.com 2018/10/08 10:24
 */
public class ScriptResult {

    private Boolean failed;
    private Object content;

    public Boolean getFailed() {
        return failed;
    }

    public void setFailed(Boolean failed) {
        this.failed = failed;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}