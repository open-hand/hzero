package org.hzero.dd.dto;

public class WorkProgressAndResultMessageDTO {
    private Long agent_id;
    private Long task_id;

    public void setAgent_id(Long agent_id) {
        this.agent_id = agent_id;
    }

    public void setTask_id(Long task_id) {
        this.task_id = task_id;
    }

    public Long getAgent_id() {
        return agent_id;
    }

    public Long getTask_id() {
        return task_id;
    }
}
