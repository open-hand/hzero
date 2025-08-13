package org.hzero.iam.api.dto;

import java.util.Date;

import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModelProperty;

/**
 * 调度任务DTO
 *
 * @author yuqing.zhang@hand-china.com 2020/05/11 14:54
 */
public class JobInfoDTO extends AuditDomain {
    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long jobId;
    @ApiModelProperty("执行器ID,hsdr_executor.executor_id")
    private Long executorId;
    @ApiModelProperty("任务编码")
    private String jobCode;
    @ApiModelProperty("任务执行corn")
    private String jobCron;
    @ApiModelProperty("任务描述")
    private String description;
    @ApiModelProperty("执行器任务参数")
    private String jobParam;
    @ApiModelProperty("报警邮件")
    private String alarmEmail;
    @ApiModelProperty("执行器策略，HSDR.EXECUTOR_STRATEGY")
    private String executorStrategy;
    @ApiModelProperty("失败处理策略，HSDR.FAIL_STRATEGY")
    private String failStrategy;
    @ApiModelProperty("策略参数")
    private String strategyParam;
    @ApiModelProperty("任务类型，HSDR.GLUE_TYPE")
    private String glueType;
    @ApiModelProperty("jobHandler")
    private String jobHandler;
    @ApiModelProperty("周期性, 1周期  0非周期")
    private Integer cycleFlag;
    @ApiModelProperty("有效时间从")
    private Date startDate;
    @ApiModelProperty("有效时间至")
    private Date endDate;
    @ApiModelProperty("租户ID,hpfm_tenant.tenant_id")
    private Long tenantId;
    @ApiModelProperty("父任务ID")
    private Long parentId;
    @ApiModelProperty("是否串行执行任务")
    private Integer serial;
    @ApiModelProperty("扩展属性")
    private String expandParam;
    @ApiModelProperty("初始化标识")
    private Integer initFlag;

    private String tenantName;
    private String executorStrategyMeaning;
    private String failStrategyMeaning;
    private String glueTypeMeaning;
    private String jobStatus;
    private String jobStatusMeaning;
    private String executorName;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Long getExecutorId() {
        return executorId;
    }

    public void setExecutorId(Long executorId) {
        this.executorId = executorId;
    }

    public String getJobCode() {
        return jobCode;
    }

    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }

    public String getJobCron() {
        return jobCron;
    }

    public void setJobCron(String jobCron) {
        this.jobCron = jobCron;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJobParam() {
        return jobParam;
    }

    public void setJobParam(String jobParam) {
        this.jobParam = jobParam;
    }

    public String getAlarmEmail() {
        return alarmEmail;
    }

    public void setAlarmEmail(String alarmEmail) {
        this.alarmEmail = alarmEmail;
    }

    public String getExecutorStrategy() {
        return executorStrategy;
    }

    public void setExecutorStrategy(String executorStrategy) {
        this.executorStrategy = executorStrategy;
    }

    public String getFailStrategy() {
        return failStrategy;
    }

    public void setFailStrategy(String failStrategy) {
        this.failStrategy = failStrategy;
    }

    public String getStrategyParam() {
        return strategyParam;
    }

    public void setStrategyParam(String strategyParam) {
        this.strategyParam = strategyParam;
    }

    public String getGlueType() {
        return glueType;
    }

    public void setGlueType(String glueType) {
        this.glueType = glueType;
    }

    public String getJobHandler() {
        return jobHandler;
    }

    public void setJobHandler(String jobHandler) {
        this.jobHandler = jobHandler;
    }

    public Integer getCycleFlag() {
        return cycleFlag;
    }

    public void setCycleFlag(Integer cycleFlag) {
        this.cycleFlag = cycleFlag;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getSerial() {
        return serial;
    }

    public void setSerial(Integer serial) {
        this.serial = serial;
    }

    public String getExpandParam() {
        return expandParam;
    }

    public void setExpandParam(String expandParam) {
        this.expandParam = expandParam;
    }

    public Integer getInitFlag() {
        return initFlag;
    }

    public void setInitFlag(Integer initFlag) {
        this.initFlag = initFlag;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getExecutorStrategyMeaning() {
        return executorStrategyMeaning;
    }

    public void setExecutorStrategyMeaning(String executorStrategyMeaning) {
        this.executorStrategyMeaning = executorStrategyMeaning;
    }

    public String getFailStrategyMeaning() {
        return failStrategyMeaning;
    }

    public void setFailStrategyMeaning(String failStrategyMeaning) {
        this.failStrategyMeaning = failStrategyMeaning;
    }

    public String getGlueTypeMeaning() {
        return glueTypeMeaning;
    }

    public void setGlueTypeMeaning(String glueTypeMeaning) {
        this.glueTypeMeaning = glueTypeMeaning;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getJobStatusMeaning() {
        return jobStatusMeaning;
    }

    public void setJobStatusMeaning(String jobStatusMeaning) {
        this.jobStatusMeaning = jobStatusMeaning;
    }

    public String getExecutorName() {
        return executorName;
    }

    public void setExecutorName(String executorName) {
        this.executorName = executorName;
    }

    @Override
    public String toString() {
        return "JobInfoDTO{" +
                "jobId=" + jobId +
                ", executorId=" + executorId +
                ", jobCode='" + jobCode + '\'' +
                ", jobCron='" + jobCron + '\'' +
                ", description='" + description + '\'' +
                ", jobParam='" + jobParam + '\'' +
                ", alarmEmail='" + alarmEmail + '\'' +
                ", executorStrategy='" + executorStrategy + '\'' +
                ", failStrategy='" + failStrategy + '\'' +
                ", strategyParam='" + strategyParam + '\'' +
                ", glueType='" + glueType + '\'' +
                ", jobHandler='" + jobHandler + '\'' +
                ", cycleFlag=" + cycleFlag +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", tenantId=" + tenantId +
                ", parentId=" + parentId +
                ", serial=" + serial +
                ", expandParam='" + expandParam + '\'' +
                ", initFlag=" + initFlag +
                ", tenantName='" + tenantName + '\'' +
                ", executorStrategyMeaning='" + executorStrategyMeaning + '\'' +
                ", failStrategyMeaning='" + failStrategyMeaning + '\'' +
                ", glueTypeMeaning='" + glueTypeMeaning + '\'' +
                ", jobStatus='" + jobStatus + '\'' +
                ", jobStatusMeaning='" + jobStatusMeaning + '\'' +
                ", executorName='" + executorName + '\'' +
                '}';
    }
}
