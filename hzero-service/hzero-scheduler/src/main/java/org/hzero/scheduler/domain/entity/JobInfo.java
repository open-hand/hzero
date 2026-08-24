package org.hzero.scheduler.domain.entity;

import java.util.Date;
import java.util.Objects;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.validator.constraints.Length;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.util.Regexs;
import org.hzero.mybatis.common.query.Where;
import org.hzero.scheduler.infra.constant.HsdrConstant;
import org.hzero.scheduler.infra.constant.HsdrErrorCode;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author shuangfei.zhu@hand-china.com 2019-01-10 10:54:23
 */
@ApiModel("任务信息")
@VersionAudit
@ModifyAudit
@Table(name = "hsdr_job_info")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobInfo extends AuditDomain {

    public static final String FIELD_JOB_ID = "jobId";
    public static final String FIELD_EXECUTOR_ID = "executorId";
    public static final String FIELD_JOB_CODE = "jobCode";
    public static final String FIELD_JOB_CRON = "jobCron";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_JOB_PARAM = "jobParam";
    public static final String FIELD_ALARM_EMAIL = "alarmEmail";
    public static final String FIELD_EXECUTOR_STRATEGY = "executorStrategy";
    public static final String FIELD_FAIL_STRATEGY = "failStrategy";
    public static final String FIELD_STRATEGY_PARAM = "strategyParam";
    public static final String FIELD_GLUE_TYPE = "glueType";
    public static final String FIELD_JOB_HANDLER = "jobHandler";
    public static final String FIELD_CYCLE_FLAG = "cycleFlag";
    public static final String FIELD_START_DATE = "startDate";
    public static final String FIELD_END_DATE = "endDate";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_PARENT_ID = "parentId";
    public static final String FIELD_SERIAL = "serial";
    public static final String FIELD_EXPAND_PARAM = "expandParam";
    public static final String FIELD_INIT_FLAG = "initFlag";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 校验
     */
    public void validate() {
        if (Objects.equals(glueType, HsdrConstant.JobType.SIMPLE)) {
            Assert.notNull(jobHandler, HsdrErrorCode.PARAMETER_ERROR);
        }
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Where
    @Encrypt
    private Long jobId;
    @ApiModelProperty(value = "执行器ID,hsdr_executor.executor_id")
    @Encrypt
    private Long executorId;
    @ApiModelProperty(value = "任务编码")
    @NotBlank
    @Length(max = 30)
    @Pattern(regexp = Regexs.CODE_UPPER)
    private String jobCode;
    @ApiModelProperty(value = "任务执行corn")
    @Length(max = 60)
    private String jobCron;
    @ApiModelProperty(value = "任务描述")
    @Length(max = 240)
    private String description;
    @ApiModelProperty(value = "执行器任务参数")
    private String jobParam;
    @ApiModelProperty(value = "报警邮件")
    @Length(max = 240)
    @Email
    private String alarmEmail;
    @ApiModelProperty(value = "执行器策略，HSDR.EXECUTOR_STRATEGY")
    @NotBlank
    @Length(max = 30)
    @LovValue(lovCode = HsdrConstant.ExecutorStrategy.CODE)
    private String executorStrategy;
    @ApiModelProperty(value = "失败处理策略，HSDR.FAIL_STRATEGY")
    @NotBlank
    @Length(max = 30)
    @LovValue(lovCode = HsdrConstant.FailStrategy.CODE)
    private String failStrategy;
    @ApiModelProperty(value = "策略参数")
    @Length(max = 240)
    private String strategyParam;
    @ApiModelProperty(value = "任务类型，HSDR.GLUE_TYPE")
    @NotBlank
    @Length(max = 30)
    @LovValue(lovCode = HsdrConstant.JobType.CODE)
    private String glueType;
    @ApiModelProperty(value = "jobHandler")
    @Length(max = 30)
    private String jobHandler;
    @ApiModelProperty(value = "周期性, 1周期  0非周期")
    private Integer cycleFlag;
    @ApiModelProperty(value = "有效时间从")
    private Date startDate;
    @ApiModelProperty(value = "有效时间至")
    private Date endDate;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id")
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "父任务ID")
    private Long parentId;
    @ApiModelProperty(value = "是否串行执行任务")
    private Integer serial;
    @ApiModelProperty(value = "扩展属性")
    private String expandParam;
    @ApiModelProperty(value = "初始化标识")
    private Integer initFlag;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private Long taskId;
    @Transient
    private String tenantName;
    @Transient
    private String executorStrategyMeaning;
    @Transient
    private String failStrategyMeaning;
    @Transient
    private String glueTypeMeaning;
    @LovValue(lovCode = HsdrConstant.JobStatus.CODE)
    @Transient
    private String jobStatus;
    @Transient
    private String jobStatusMeaning;
    @Transient
    private String executorName;
    @Transient
    private String executorCode;
    @LovValue(lovCode = "HSDR.JOB.SOURCE_FLAG")
    @Transient
    private String sourceFlag;
    @Transient
    private String sourceFlagMeaning;

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getJobId() {
        return jobId;
    }

    public JobInfo setJobId(Long jobId) {
        this.jobId = jobId;
        return this;
    }

    public Long getExecutorId() {
        return executorId;
    }

    public JobInfo setExecutorId(Long executorId) {
        this.executorId = executorId;
        return this;
    }

    public String getJobCode() {
        return jobCode;
    }

    public JobInfo setJobCode(String jobCode) {
        this.jobCode = jobCode;
        return this;
    }

    public String getJobCron() {
        return jobCron;
    }

    public JobInfo setJobCron(String jobCron) {
        this.jobCron = jobCron;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public JobInfo setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getJobParam() {
        return jobParam;
    }

    public JobInfo setJobParam(String jobParam) {
        this.jobParam = jobParam;
        return this;
    }

    public String getAlarmEmail() {
        return alarmEmail;
    }

    public JobInfo setAlarmEmail(String alarmEmail) {
        this.alarmEmail = alarmEmail;
        return this;
    }

    public String getExecutorStrategy() {
        return executorStrategy;
    }

    public JobInfo setExecutorStrategy(String executorStrategy) {
        this.executorStrategy = executorStrategy;
        return this;
    }

    public String getFailStrategy() {
        return failStrategy;
    }

    public JobInfo setFailStrategy(String failStrategy) {
        this.failStrategy = failStrategy;
        return this;
    }

    public String getStrategyParam() {
        return strategyParam;
    }

    public JobInfo setStrategyParam(String strategyParam) {
        this.strategyParam = strategyParam;
        return this;
    }

    public String getGlueType() {
        return glueType;
    }

    public JobInfo setGlueType(String glueType) {
        this.glueType = glueType;
        return this;
    }

    public String getJobHandler() {
        return jobHandler;
    }

    public JobInfo setJobHandler(String jobHandler) {
        this.jobHandler = jobHandler;
        return this;
    }

    public Integer getCycleFlag() {
        return cycleFlag;
    }

    public JobInfo setCycleFlag(Integer cycleFlag) {
        this.cycleFlag = cycleFlag;
        return this;
    }

    public Date getStartDate() {
        return startDate;
    }

    public JobInfo setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public Date getEndDate() {
        return endDate;
    }

    public JobInfo setEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public JobInfo setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Long getParentId() {
        return parentId;
    }

    public JobInfo setParentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }

    public Long getTaskId() {
        return taskId;
    }

    public JobInfo setTaskId(Long taskId) {
        this.taskId = taskId;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public JobInfo setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public String getExecutorStrategyMeaning() {
        return executorStrategyMeaning;
    }

    public JobInfo setExecutorStrategyMeaning(String executorStrategyMeaning) {
        this.executorStrategyMeaning = executorStrategyMeaning;
        return this;
    }

    public String getFailStrategyMeaning() {
        return failStrategyMeaning;
    }

    public JobInfo setFailStrategyMeaning(String failStrategyMeaning) {
        this.failStrategyMeaning = failStrategyMeaning;
        return this;
    }

    public String getGlueTypeMeaning() {
        return glueTypeMeaning;
    }

    public JobInfo setGlueTypeMeaning(String glueTypeMeaning) {
        this.glueTypeMeaning = glueTypeMeaning;
        return this;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public JobInfo setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
        return this;
    }

    public String getJobStatusMeaning() {
        return jobStatusMeaning;
    }

    public JobInfo setJobStatusMeaning(String jobStatusMeaning) {
        this.jobStatusMeaning = jobStatusMeaning;
        return this;
    }

    public String getExecutorName() {
        return executorName;
    }

    public void setExecutorName(String executorName) {
        this.executorName = executorName;
    }

    public Integer getSerial() {
        return serial;
    }

    public JobInfo setSerial(Integer serial) {
        this.serial = serial;
        return this;
    }

    public String getExpandParam() {
        return expandParam;
    }

    public JobInfo setExpandParam(String expandParam) {
        this.expandParam = expandParam;
        return this;
    }

    public Integer getInitFlag() {
        return initFlag;
    }

    public JobInfo setInitFlag(Integer initFlag) {
        this.initFlag = initFlag;
        return this;
    }

    public String getExecutorCode() {
        return executorCode;
    }

    public JobInfo setExecutorCode(String executorCode) {
        this.executorCode = executorCode;
        return this;
    }

    public String getSourceFlag() {
        return sourceFlag;
    }

    public JobInfo setSourceFlag(String sourceFlag) {
        this.sourceFlag = sourceFlag;
        return this;
    }

    public String getSourceFlagMeaning() {
        return sourceFlagMeaning;
    }

    public JobInfo setSourceFlagMeaning(String sourceFlagMeaning) {
        this.sourceFlagMeaning = sourceFlagMeaning;
        return this;
    }
}
