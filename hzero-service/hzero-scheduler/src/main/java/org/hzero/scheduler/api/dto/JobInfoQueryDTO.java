package org.hzero.scheduler.api.dto;

import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.List;

import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 任务信息查询参数
 *
 * @author shuangfei.zhu@hand-china.com 2019/03/06 14:33
 */
public class JobInfoQueryDTO {

    @Encrypt
    private Long jobId;
    private Long taskId;
    private Long tenantId;
    private String jobCode;
    private String description;
    private String jobHandler;
    private String glueType;
    private String jobStatus;
    private List<String> triggerStatus;
    private Boolean includeNull;
    private String executorCode;
    private String executorName;
    private String sourceFlag;

    public Long getJobId() {
        return jobId;
    }

    public JobInfoQueryDTO setJobId(Long jobId) {
        this.jobId = jobId;
        return this;
    }

    public Long getTaskId() {
        return taskId;
    }

    public JobInfoQueryDTO setTaskId(Long taskId) {
        this.taskId = taskId;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public JobInfoQueryDTO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getJobCode() {
        return jobCode;
    }

    public JobInfoQueryDTO setJobCode(String jobCode) {
        this.jobCode = jobCode;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public JobInfoQueryDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getJobHandler() {
        return jobHandler;
    }

    public JobInfoQueryDTO setJobHandler(String jobHandler) {
        this.jobHandler = jobHandler;
        return this;
    }

    public String getGlueType() {
        return glueType;
    }

    public JobInfoQueryDTO setGlueType(String glueType) {
        this.glueType = glueType;
        return this;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public JobInfoQueryDTO setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
        return this;
    }

    public List<String> getTriggerStatus() {
        return triggerStatus;
    }

    public JobInfoQueryDTO setTriggerStatus(List<String> triggerStatus) {
        this.triggerStatus = triggerStatus;
        return this;
    }

    public Boolean getIncludeNull() {
        return includeNull;
    }

    public JobInfoQueryDTO setIncludeNull(Boolean includeNull) {
        this.includeNull = includeNull;
        return this;
    }

    public String getExecutorCode() {
        return executorCode;
    }

    public JobInfoQueryDTO setExecutorCode(String executorCode) {
        this.executorCode = executorCode;
        return this;
    }

    public String getExecutorName() {
        return executorName;
    }

    public JobInfoQueryDTO setExecutorName(String executorName) {
        this.executorName = executorName;
        return this;
    }

    public String getSourceFlag() {
        return sourceFlag;
    }

    public JobInfoQueryDTO setSourceFlag(String sourceFlag) {
        this.sourceFlag = sourceFlag;
        return this;
    }

    @Override
    public String toString() {
        return "JobInfoQueryDTO{" +
                "jobId=" + jobId +
                ", taskId=" + taskId +
                ", tenantId=" + tenantId +
                ", jobCode='" + jobCode + '\'' +
                ", description='" + description + '\'' +
                ", jobHandler='" + jobHandler + '\'' +
                ", glueType='" + glueType + '\'' +
                ", jobStatus='" + jobStatus + '\'' +
                ", triggerStatus=" + triggerStatus +
                ", includeNull=" + includeNull +
                ", executorCode='" + executorCode + '\'' +
                ", executorName='" + executorName + '\'' +
                ", sourceFlag='" + sourceFlag + '\'' +
                '}';
    }
}
