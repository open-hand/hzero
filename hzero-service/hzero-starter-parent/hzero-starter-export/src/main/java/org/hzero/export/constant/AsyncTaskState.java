package org.hzero.export.constant;

/**
 * 异步任务状态
 *
 * @author shuangfei.zhu@hand-china.com 2021/08/31 15:43
 */
public enum AsyncTaskState {

    /**
     * 导出中
     */
    DOING("doing"),
    /**
     * 导出成功
     */
    DONE("done"),
    /**
     * 导出失败
     */
    FAILED("failed"),
    /**
     * 取消导出
     */
    CANCELLED("cancelled");

    private final String code;

    AsyncTaskState(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code;
    }
}
