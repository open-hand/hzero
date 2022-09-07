package org.hzero.boot.scheduler.api.dto;

import java.util.Date;

/**
 * 调度任务数据传输对象
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/17 14:17
 */
public class JobDataDTO {

    public static final String FIELD_JOB_ID = "jobId";
    public static final String FIELD_LOG_ID = "logId";
    public static final String FIELD_JOB_CODE = "jobCode";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_EXECUTOR_ID = "executorId";
    public static final String FIELD_JOB_TYPE = "jobType";
    public static final String FIELD_JOB_HANDLER = "jobHandler";
    public static final String FIELD_START_DATE = "startDate";
    public static final String FIELD_END_DATE = "endDate";
    public static final String FIELD_PARAM = "param";
    public static final String FIELD_RUN_TIME = "runTime";
    public static final String FIELD_USER_INFO = "userInfo";

    private Long jobId;
    private Long logId;
    private String jobCode;
    private Long tenantId;
    private String jobType;
    private String jobHandler;
    private String param;
    private Long runTime;
    private UserInfo userInfo;

    public Long getJobId() {
        return jobId;
    }

    public JobDataDTO setJobId(Long jobId) {
        this.jobId = jobId;
        return this;
    }

    public Long getLogId() {
        return logId;
    }

    public JobDataDTO setLogId(Long logId) {
        this.logId = logId;
        return this;
    }

    public String getJobCode() {
        return jobCode;
    }

    public JobDataDTO setJobCode(String jobCode) {
        this.jobCode = jobCode;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public JobDataDTO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getJobType() {
        return jobType;
    }

    public JobDataDTO setJobType(String jobType) {
        this.jobType = jobType;
        return this;
    }

    public String getJobHandler() {
        return jobHandler;
    }

    public JobDataDTO setJobHandler(String jobHandler) {
        this.jobHandler = jobHandler;
        return this;
    }

    public String getParam() {
        return param;
    }

    public JobDataDTO setParam(String param) {
        this.param = param;
        return this;
    }

    public Long getRunTime() {
        return runTime;
    }

    public JobDataDTO setRunTime(Long runTime) {
        this.runTime = runTime;
        return this;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public JobDataDTO setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    @Override
    public String toString() {
        return "JobDataDTO{" +
                "jobId=" + jobId +
                ", logId=" + logId +
                ", jobCode='" + jobCode + '\'' +
                ", tenantId=" + tenantId +
                ", jobType='" + jobType + '\'' +
                ", jobHandler='" + jobHandler + '\'' +
                ", param='" + param + '\'' +
                ", runTime=" + runTime +
                ", userInfo=" + userInfo +
                '}';
    }
}
