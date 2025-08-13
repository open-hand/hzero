package org.hzero.scheduler.domain.entity;

import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.mybatis.common.query.Where;
import org.hzero.scheduler.infra.constant.HsdrConstant;
import org.hzero.starter.keyencrypt.core.Encrypt;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * @author shuangfei.zhu@hand-china.com 2019-01-23 11:36:39
 */
@ApiModel("任务日志")
@VersionAudit
@ModifyAudit
@Table(name = "hsdr_job_log")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobLog extends AuditDomain {

    public static final String FIELD_LOG_ID = "logId";
    public static final String FIELD_JOB_ID = "jobId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_JOB_RESULT = "jobResult";
    public static final String FIELD_CLIENT_RESULT = "clientResult";
    public static final String FIELD_EXECUTOR_ID = "executorId";
    public static final String FIELD_ADDRESS = "address";
    public static final String FIELD_MESSAGE_HEADER = "messageHeader";
    public static final String FIELD_MESSAGE = "message";
    public static final String FIELD_START_TIME = "startTime";
    public static final String FIELD_END_TIME = "endTime";
    public static final String FIELD_LOG_URL = "logUrl";
    public static final String FIELD_LOG_MESSAGE = "logMessage";
    public static final String FIELD_OUTPUT_FILE = "outputFile";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Where
    @Encrypt
    private Long logId;
    @ApiModelProperty(value = "任务ID,hsdr_job_info.job_id")
    @NotNull
    @Where
    @Encrypt
    private Long jobId;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id")
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "任务调度结果,HSDR.LOG.JOB_RESULT")
    @Length(max = 30)
    @LovValue(lovCode = HsdrConstant.JobResult.CODE)
    private String jobResult;
    @ApiModelProperty(value = "客户端执行结果,HSDR.LOG.CLIENT_RESULT")
    @Length(max = 30)
    @LovValue(lovCode = HsdrConstant.ClientResult.CODE)
    private String clientResult;
    @ApiModelProperty(value = "执行器ID,hsdr_executor.executor_id")
    @NotNull
    @Encrypt
    private Long executorId;
    @ApiModelProperty(value = "任务执行地址")
    @Length(max = 30)
    private String address;
    @ApiModelProperty(value = "错误信息简略")
    private String messageHeader;
    @ApiModelProperty(value = "错误信息")
    private String message;
    @ApiModelProperty(value = "任务开始时间")
    private Date startTime;
    @ApiModelProperty(value = "任务结束时间")
    private Date endTime;
    @ApiModelProperty(value = "日志文件url")
    @Length(max = 240)
    private String logUrl;
    @ApiModelProperty(value = "日志信息")
    private String logMessage;
    @ApiModelProperty(value = "输出文件")
    private String outputFile;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String tenantName;
    @Transient
    private String executorName;
    @Transient
    private String jobResultMeaning;
    @Transient
    private String clientResultMeaning;
    @Transient
    private String jobCode;
    @Transient
    private String description;
    /**
     * 前端是否展示日志按钮的开关
     */
    @Transient
    private Integer logFlag;
    @Transient
    private Long taskId;

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getLogId() {
        return logId;
    }

    public JobLog setLogId(Long logId) {
        this.logId = logId;
        return this;
    }

    public Long getJobId() {
        return jobId;
    }

    public JobLog setJobId(Long jobId) {
        this.jobId = jobId;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public JobLog setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getJobResult() {
        return jobResult;
    }

    public JobLog setJobResult(String jobResult) {
        this.jobResult = jobResult;
        return this;
    }

    public String getClientResult() {
        return clientResult;
    }

    public JobLog setClientResult(String clientResult) {
        this.clientResult = clientResult;
        return this;
    }

    public Long getExecutorId() {
        return executorId;
    }

    public JobLog setExecutorId(Long executorId) {
        this.executorId = executorId;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public JobLog setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getMessageHeader() {
        return messageHeader;
    }

    public JobLog setMessageHeader(String messageHeader) {
        this.messageHeader = messageHeader;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public JobLog setMessage(String message) {
        this.message = message;
        return this;
    }

    public Date getStartTime() {
        return startTime;
    }

    public JobLog setStartTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public JobLog setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getLogUrl() {
        return logUrl;
    }

    public JobLog setLogUrl(String logUrl) {
        this.logUrl = logUrl;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public JobLog setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public String getExecutorName() {
        return executorName;
    }

    public JobLog setExecutorName(String executorName) {
        this.executorName = executorName;
        return this;
    }

    public String getJobResultMeaning() {
        return jobResultMeaning;
    }

    public JobLog setJobResultMeaning(String jobResultMeaning) {
        this.jobResultMeaning = jobResultMeaning;
        return this;
    }

    public String getClientResultMeaning() {
        return clientResultMeaning;
    }

    public JobLog setClientResultMeaning(String clientResultMeaning) {
        this.clientResultMeaning = clientResultMeaning;
        return this;
    }

    public String getJobCode() {
        return jobCode;
    }

    public JobLog setJobCode(String jobCode) {
        this.jobCode = jobCode;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public JobLog setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public JobLog setLogMessage(String logMessage) {
        this.logMessage = logMessage;
        return this;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public JobLog setOutputFile(String outputFile) {
        this.outputFile = outputFile;
        return this;
    }

    public Integer getLogFlag() {
        return logFlag;
    }

    public JobLog setLogFlag(Integer logFlag) {
        this.logFlag = logFlag;
        return this;
    }

    public Long getTaskId() {
        return taskId;
    }

    public JobLog setTaskId(Long taskId) {
        this.taskId = taskId;
        return this;
    }
}
