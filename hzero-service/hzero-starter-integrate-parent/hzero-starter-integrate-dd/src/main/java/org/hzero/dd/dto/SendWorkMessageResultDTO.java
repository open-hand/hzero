package org.hzero.dd.dto;

public class SendWorkMessageResultDTO extends DefaultResultDTO {
    /**
     *     "errcode":0,
     *     "errmsg":"ok",
     *     "task_id":123
     */

    private long task_id;

    public long getTask_id() {
        return task_id;
    }

    public void setTask_id(long task_id) {
        this.task_id = task_id;
    }
}
