package org.hzero.scheduler.api.dto;

import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.Date;

import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 调度日志查询条件
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/29 10:14
 */
public class JobLogQueryDTO {

    @Encrypt
    private Long jobId;
    private Long taskId;
    private String jobCode;
    private String description;
    private Long tenantId;
    private String jobResult;
    private String clientResult;
    private String executorName;
    private Date timeStart;
    private Date timeEnd;

    public Long getJobId() {
        return jobId;
    }

    public JobLogQueryDTO setJobId(Long jobId) {
        this.jobId = jobId;
        return this;
    }

    public Long getTaskId() {
        return taskId;
    }

    public JobLogQueryDTO setTaskId(Long taskId) {
        this.taskId = taskId;
        return this;
    }

    public String getJobCode() {
        return jobCode;
    }

    public JobLogQueryDTO setJobCode(String jobCode) {
        this.jobCode = jobCode;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public JobLogQueryDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public JobLogQueryDTO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getJobResult() {
        return jobResult;
    }

    public JobLogQueryDTO setJobResult(String jobResult) {
        this.jobResult = jobResult;
        return this;
    }

    public String getClientResult() {
        return clientResult;
    }

    public JobLogQueryDTO setClientResult(String clientResult) {
        this.clientResult = clientResult;
        return this;
    }

    public String getExecutorName() {
        return executorName;
    }

    public JobLogQueryDTO setExecutorName(String executorName) {
        this.executorName = executorName;
        return this;
    }

    public Date getTimeStart() {
        return timeStart;
    }

    public JobLogQueryDTO setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
        return this;
    }

    public Date getTimeEnd() {
        return timeEnd;
    }

    public JobLogQueryDTO setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
        return this;
    }

    @Override
    public String toString() {
        return "JobLogQueryDTO{" +
                "jobId=" + jobId +
                ", taskId=" + taskId +
                ", jobCode='" + jobCode + '\'' +
                ", description='" + description + '\'' +
                ", tenantId=" + tenantId +
                ", jobResult='" + jobResult + '\'' +
                ", clientResult='" + clientResult + '\'' +
                ", executorName='" + executorName + '\'' +
                ", timeStart=" + timeStart +
                ", timeEnd=" + timeEnd +
                '}';
    }
}
