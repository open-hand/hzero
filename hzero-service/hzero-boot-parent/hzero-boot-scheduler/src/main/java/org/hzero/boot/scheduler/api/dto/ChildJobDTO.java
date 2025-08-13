package org.hzero.boot.scheduler.api.dto;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hzero.boot.scheduler.infra.enums.ExecutorStrategy;
import org.hzero.boot.scheduler.infra.enums.FailStrategy;
import org.hzero.boot.scheduler.infra.enums.GlueType;

/**
 * 子任务传输对象
 *
 * @author shuangfei.zhu@hand-china.com 2019/02/14 10:42
 */
public class ChildJobDTO {

    @NotBlank
    private String jobCode;
    @NotBlank
    private String executorCode;
    private String jobCron;
    private String description;
    private String alarmEmail;
    @NotBlank
    private String executorStrategy;
    private String jobParam;
    @NotBlank
    private String failStrategy;
    @NotBlank
    private String glueType;
    @NotBlank
    private String jobHandler;
    private Integer cycleFlag;
    private Date startDate;
    private Date endDate;
    @NotNull
    private Long tenantId;
    @NotNull
    private Long parentId;

    public String getJobCode() {
        return jobCode;
    }

    public ChildJobDTO setJobCode(String jobCode) {
        this.jobCode = jobCode;
        return this;
    }

    public String getExecutorCode() {
        return executorCode;
    }

    public ChildJobDTO setExecutorCode(String executorCode) {
        this.executorCode = executorCode;
        return this;
    }

    public String getJobCron() {
        return jobCron;
    }

    public ChildJobDTO setJobCron(String jobCron) {
        this.jobCron = jobCron;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ChildJobDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getAlarmEmail() {
        return alarmEmail;
    }

    public ChildJobDTO setAlarmEmail(String alarmEmail) {
        this.alarmEmail = alarmEmail;
        return this;
    }

    public String getExecutorStrategy() {
        return executorStrategy;
    }

    public ChildJobDTO setExecutorStrategy(String executorStrategy) {
        this.executorStrategy = executorStrategy;
        return this;
    }

    public ChildJobDTO setExecutorStrategy(ExecutorStrategy executorStrategy) {
        this.executorStrategy = executorStrategy.getValue();
        return this;
    }

    public String getJobParam() {
        return jobParam;
    }

    public ChildJobDTO setJobParam(String jobParam) {
        this.jobParam = jobParam;
        return this;
    }

    public String getFailStrategy() {
        return failStrategy;
    }

    public ChildJobDTO setFailStrategy(String failStrategy) {
        this.failStrategy = failStrategy;
        return this;
    }

    public ChildJobDTO setFailStrategy(FailStrategy failStrategy) {
        this.failStrategy = failStrategy.getValue();
        return this;
    }

    public String getGlueType() {
        return glueType;
    }

    public ChildJobDTO setGlueType(String glueType) {
        this.glueType = glueType;
        return this;
    }

    public ChildJobDTO setGlueType(GlueType glueType) {
        this.glueType = glueType.getValue();
        return this;
    }

    public String getJobHandler() {
        return jobHandler;
    }

    public ChildJobDTO setJobHandler(String jobHandler) {
        this.jobHandler = jobHandler;
        return this;
    }

    public Integer getCycleFlag() {
        return cycleFlag;
    }

    public ChildJobDTO setCycleFlag(Integer cycleFlag) {
        this.cycleFlag = cycleFlag;
        return this;
    }

    public Date getStartDate() {
        return startDate;
    }

    public ChildJobDTO setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public Date getEndDate() {
        return endDate;
    }

    public ChildJobDTO setEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public ChildJobDTO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Long getParentId() {
        return parentId;
    }

    public ChildJobDTO setParentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }

    @Override
    public String toString() {
        return "ChildJobDTO{" +
                "jobCode='" + jobCode + '\'' +
                ", executorCode='" + executorCode + '\'' +
                ", jobCron='" + jobCron + '\'' +
                ", description='" + description + '\'' +
                ", alarmEmail='" + alarmEmail + '\'' +
                ", executorStrategy='" + executorStrategy + '\'' +
                ", jobParam='" + jobParam + '\'' +
                ", failStrategy='" + failStrategy + '\'' +
                ", glueType='" + glueType + '\'' +
                ", jobHandler='" + jobHandler + '\'' +
                ", cycleFlag=" + cycleFlag +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", tenantId=" + tenantId +
                ", parentId=" + parentId +
                '}';
    }
}
