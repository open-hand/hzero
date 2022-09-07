package org.hzero.core.async;

import java.util.Date;

/**
 * 异步任务DTO
 * @author XCXCXCXCX
 * @date 2019/8/7
 */
public class TaskDTO {

    private Long taskId;
    private String taskCode;
    private String taskName;
    private String hostName;
    private String serviceName;
    private Long tenantId;
    private Long userId;
    private AsyncTaskState state;
    private String errorInfo;
    private String downloadUrl;
    private Date endDateTime;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public AsyncTaskState getState() {
        return state;
    }

    public void setState(AsyncTaskState state) {
        this.state = state;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    @Override
    public String toString() {
        return "TaskDTO{" +
                "taskId=" + taskId +
                ", taskCode='" + taskCode + '\'' +
                ", taskName='" + taskName + '\'' +
                ", hostName='" + hostName + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", tenantId=" + tenantId +
                ", userId=" + userId +
                ", state=" + state +
                ", errorInfo='" + errorInfo + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", endDateTime=" + endDateTime +
                '}';
    }
}
