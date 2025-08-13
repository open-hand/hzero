package org.hzero.scheduler.api.dto;

import java.util.List;

import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 并发请求查询参数
 *
 * @author shuangfei.zhu@hand-china.com 2019/03/06 15:12
 */
public class RequestQueryDTO {

    private Long tenantId;
    @Encrypt
    private Long jobId;
    private Long taskId;
    private String concCode;
    private String concName;
    private Integer cycleFlag;
    private String jobStatus;
    private List<String> triggerStatus;
    private Boolean includeNull;
    private String clientResult;

    public Long getTenantId() {
        return tenantId;
    }

    public RequestQueryDTO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Long getJobId() {
        return jobId;
    }

    public RequestQueryDTO setJobId(Long jobId) {
        this.jobId = jobId;
        return this;
    }

    public Long getTaskId() {
        return taskId;
    }

    public RequestQueryDTO setTaskId(Long taskId) {
        this.taskId = taskId;
        return this;
    }

    public String getConcCode() {
        return concCode;
    }

    public RequestQueryDTO setConcCode(String concCode) {
        this.concCode = concCode;
        return this;
    }

    public String getConcName() {
        return concName;
    }

    public RequestQueryDTO setConcName(String concName) {
        this.concName = concName;
        return this;
    }

    public Integer getCycleFlag() {
        return cycleFlag;
    }

    public RequestQueryDTO setCycleFlag(Integer cycleFlag) {
        this.cycleFlag = cycleFlag;
        return this;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public RequestQueryDTO setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
        return this;
    }

    public List<String> getTriggerStatus() {
        return triggerStatus;
    }

    public RequestQueryDTO setTriggerStatus(List<String> triggerStatus) {
        this.triggerStatus = triggerStatus;
        return this;
    }

    public Boolean getIncludeNull() {
        return includeNull;
    }

    public RequestQueryDTO setIncludeNull(Boolean includeNull) {
        this.includeNull = includeNull;
        return this;
    }

    public String getClientResult() {
        return clientResult;
    }

    public RequestQueryDTO setClientResult(String clientResult) {
        this.clientResult = clientResult;
        return this;
    }

    @Override
    public String toString() {
        return "RequestQueryDTO{" +
                "tenantId=" + tenantId +
                ", jobId=" + jobId +
                ", taskId=" + taskId +
                ", concCode='" + concCode + '\'' +
                ", concName='" + concName + '\'' +
                ", cycleFlag=" + cycleFlag +
                ", jobStatus='" + jobStatus + '\'' +
                ", triggerStatus=" + triggerStatus +
                ", includeNull=" + includeNull +
                ", clientResult='" + clientResult + '\'' +
                '}';
    }
}
