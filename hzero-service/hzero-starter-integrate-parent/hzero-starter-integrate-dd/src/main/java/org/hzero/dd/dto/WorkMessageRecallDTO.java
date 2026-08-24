package org.hzero.dd.dto;

public class WorkMessageRecallDTO {
    private Long agent_id ;
    private Long  msg_task_id;

    public Long getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(Long agent_id) {
        this.agent_id = agent_id;
    }

    public Long getMsg_task_id() {
        return msg_task_id;
    }

    public void setMsg_task_id(Long msg_task_id) {
        this.msg_task_id = msg_task_id;
    }
}
