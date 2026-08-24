package org.hzero.boot.scheduler.api.dto;

/**
 * 任务进度信息
 *
 * @author shuangfei.zhu@hand-china.com 2019/03/18 16:57
 */
public class JobProgress {

    /**
     * 任务进度
     */
    private Integer progress;
    /**
     * 描述信息
     */
    private String message;

    public Integer getProgress() {
        return progress;
    }

    public JobProgress setProgress(Integer progress) {
        this.progress = progress;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public JobProgress setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public String toString() {
        return "JobProgress{" +
                "progress=" + progress +
                ", message='" + message + '\'' +
                '}';
    }
}
