package org.hzero.scheduler.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.scheduler.infra.constant.HsdrConstant;
import org.hzero.scheduler.infra.constant.HsdrErrorCode;
import org.hzero.scheduler.infra.util.ValidUtils;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 并发请求
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-13 17:12:33
 */
@ApiModel("并发请求")
@VersionAudit
@ModifyAudit
@Table(name = "hsdr_conc_request")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConcurrentRequest extends AuditDomain {

    public static final String FIELD_REQUEST_ID = "requestId";
    public static final String FIELD_CONCURRENT_ID = "concurrentId";
    public static final String FIELD_EXECUTABLE_ID = "executableId";
    public static final String FIELD_CYCLE_FLAG = "cycleFlag";
    public static final String FIELD_INTERVAL_TYPE = "intervalType";
    public static final String FIELD_INTERVAL_NUMBER = "intervalNumber";
    public static final String FIELD_INTERVAL_HOUR = "intervalHour";
    public static final String FIELD_INTERVAL_MINUTE = "intervalMinute";
    public static final String FIELD_INTERVAL_SECOND = "intervalSecond";
    public static final String FIELD_CRON = "cron";
    public static final String FIELD_REQUEST_PARAM = "requestParam";
    public static final String FIELD_START_DATE = "startDate";
    public static final String FIELD_END_DATE = "endDate";
    public static final String FIELD_GROUP_ID = "groupId";
    public static final String FIELD_JOB_ID = "jobId";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 自定义数据校验
     */
    public void validate() {
        ValidUtils.isJsonValid(requestParam);
        if (Objects.equals(cycleFlag, BaseConstants.Flag.YES)) {
            if (StringUtils.isNotBlank(cron)) {
                return;
            }
            switch (intervalType) {
                case HsdrConstant.IntervalType.DAY:
                    Assert.notNull(intervalHour, HsdrErrorCode.PARAMETER_ERROR);
                    Assert.notNull(intervalMinute, HsdrErrorCode.PARAMETER_ERROR);
                    Assert.notNull(intervalSecond, HsdrErrorCode.PARAMETER_ERROR);
                    break;
                case HsdrConstant.IntervalType.HOUR:
                    Assert.notNull(intervalMinute, HsdrErrorCode.PARAMETER_ERROR);
                    Assert.notNull(intervalSecond, HsdrErrorCode.PARAMETER_ERROR);
                    break;
                case HsdrConstant.IntervalType.MINUTE:
                    Assert.notNull(intervalSecond, HsdrErrorCode.PARAMETER_ERROR);
                    break;
                default:
                    break;
            }
        }
    }

    //缓存方法

    /**
     * 生成redis存储key
     *
     * @param tenantId 租户Id
     * @param roleId   角色Id
     * @return key
     */
    public String getCacheKey(Long tenantId, Long roleId, Long concurrentId, String date) {
        return HZeroService.Scheduler.CODE + ":concurrent-request:" + tenantId + "-" + roleId + "-" + concurrentId + "-" + date;
    }

    /**
     * 初始化缓存
     *
     * @param redisHelper redis
     * @param tenantId    租户Id
     * @param roleId      角色Id
     */
    public void initCache(RedisHelper redisHelper, Long tenantId, Long roleId, Long concurrentId, String date, Integer quantity) {
        redisHelper.strSet(getCacheKey(tenantId, roleId, concurrentId, date), String.valueOf(quantity));
        redisHelper.setExpire(getCacheKey(tenantId, roleId, concurrentId, date));
    }

    /**
     * 刷新缓存
     *
     * @param redisHelper redis
     * @param tenantId    租户Id
     * @param roleId      角色Id
     */
    public void refreshCache(RedisHelper redisHelper, Long tenantId, Long roleId, Long concurrentId, String date, Integer quantity) {
        redisHelper.strIncrement(getCacheKey(tenantId, roleId, concurrentId, date), (long) quantity);
    }

    /**
     * 查询缓存
     *
     * @param redisHelper redis
     * @param tenantId    租户Id
     * @param roleId      角色Id
     */
    public Integer getCache(RedisHelper redisHelper, Long tenantId, Long roleId, Long concurrentId, String date) {
        String quantity = redisHelper.strGet(getCacheKey(tenantId, roleId, concurrentId, date));
        if (quantity != null) {
            return Integer.valueOf(quantity);
        }
        return null;
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long requestId;
    @ApiModelProperty(value = "并发程序ID,hsdr_concurrent.concurrent_id")
    @NotNull(groups = {ConcurrentRequest.Validate.class})
    @Encrypt
    private Long concurrentId;
    @ApiModelProperty(value = "并发可执行ID,hsdr_conc_executable.executable_id")
    @NotNull
    @Encrypt
    private Long executableId;
    @ApiModelProperty(value = "周期性")
    @NotNull(groups = {ConcurrentRequest.Validate.class})
    private Integer cycleFlag;
    @ApiModelProperty(value = "间隔类型，HSDR.REQUEST.INTERVAL_TYPE(天/时/分/秒)")
    private String intervalType;
    @ApiModelProperty(value = "间隔大小")
    private Long intervalNumber;
    @Max(24)
    @Min(0)
    @ApiModelProperty(value = "固定间隔-时")
    private Long intervalHour;
    @Max(60)
    @Min(0)
    @ApiModelProperty(value = "固定间隔-分")
    private Long intervalMinute;
    @Max(60)
    @Min(0)
    @ApiModelProperty(value = "固定间隔-秒")
    private Long intervalSecond;
    @ApiModelProperty(value = "CRON表达式")
    private String cron;
    @ApiModelProperty(value = "请求参数")
    private String requestParam;
    @ApiModelProperty(value = "开始时间")
    private Date startDate;
    @ApiModelProperty(value = "结束时间/下次运行时间")
    private Date endDate;
    @ApiModelProperty(value = "执行器ID,hsdr_executor.executor_id")
    @NotNull
    private Long executorId;
    @ApiModelProperty(value = "任务ID，hsdr_job_info.job_id")
    @Encrypt
    private Long jobId;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id")
    @NotNull(groups = {ConcurrentRequest.Validate.class})
    private Long tenantId;

    public interface Validate {
    }

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    @JsonIgnore
    private String jobName;

    /**
     * 请求编码
     */
    @Transient
    @Size(max = 20)
    private String concCode;
    /**
     * 请求名称
     */
    @Transient
    @Size(max = 20)
    private String concName;
    /**
     * 父任务id
     */
    @Transient
    private Long parentId;
    @Transient
    private String tenantName;
    @LovValue(lovCode = HsdrConstant.JobStatus.CODE)
    @Transient
    private String jobStatus;
    @Transient
    private String jobStatusMeaning;
    /**
     * 提交人
     */
    @Transient
    private String username;

    @Transient
    private Long taskId;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    public Long getRequestId() {
        return requestId;
    }

    public ConcurrentRequest setRequestId(Long requestId) {
        this.requestId = requestId;
        return this;
    }

    public Long getConcurrentId() {
        return concurrentId;
    }

    public ConcurrentRequest setConcurrentId(Long concurrentId) {
        this.concurrentId = concurrentId;
        return this;
    }

    public Long getExecutableId() {
        return executableId;
    }

    public ConcurrentRequest setExecutableId(Long executableId) {
        this.executableId = executableId;
        return this;
    }

    public Integer getCycleFlag() {
        return cycleFlag;
    }

    public ConcurrentRequest setCycleFlag(Integer cycleFlag) {
        this.cycleFlag = cycleFlag;
        return this;
    }

    public String getIntervalType() {
        return intervalType;
    }

    public ConcurrentRequest setIntervalType(String intervalType) {
        this.intervalType = intervalType;
        return this;
    }

    public Long getIntervalNumber() {
        return intervalNumber;
    }

    public ConcurrentRequest setIntervalNumber(Long intervalNumber) {
        this.intervalNumber = intervalNumber;
        return this;
    }

    public Long getIntervalHour() {
        return intervalHour;
    }

    public ConcurrentRequest setIntervalHour(Long intervalHour) {
        this.intervalHour = intervalHour;
        return this;
    }

    public Long getIntervalMinute() {
        return intervalMinute;
    }

    public ConcurrentRequest setIntervalMinute(Long intervalMinute) {
        this.intervalMinute = intervalMinute;
        return this;
    }

    public Long getIntervalSecond() {
        return intervalSecond;
    }

    public ConcurrentRequest setIntervalSecond(Long intervalSecond) {
        this.intervalSecond = intervalSecond;
        return this;
    }

    public String getCron() {
        return cron;
    }

    public ConcurrentRequest setCron(String cron) {
        this.cron = cron;
        return this;
    }

    public String getRequestParam() {
        return requestParam;
    }

    public ConcurrentRequest setRequestParam(String requestParam) {
        this.requestParam = requestParam;
        return this;
    }

    public Date getStartDate() {
        return startDate;
    }

    public ConcurrentRequest setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public Date getEndDate() {
        return endDate;
    }

    public ConcurrentRequest setEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    public Long getExecutorId() {
        return executorId;
    }

    public ConcurrentRequest setExecutorId(Long executorId) {
        this.executorId = executorId;
        return this;
    }

    public Long getJobId() {
        return jobId;
    }

    public ConcurrentRequest setJobId(Long jobId) {
        this.jobId = jobId;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public ConcurrentRequest setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getJobName() {
        return jobName;
    }

    public ConcurrentRequest setJobName(String jobName) {
        this.jobName = jobName;
        return this;
    }

    public String getConcCode() {
        return concCode;
    }

    public ConcurrentRequest setConcCode(String concCode) {
        this.concCode = concCode;
        return this;
    }

    public String getConcName() {
        return concName;
    }

    public ConcurrentRequest setConcName(String concName) {
        this.concName = concName;
        return this;
    }

    public Long getParentId() {
        return parentId;
    }

    public ConcurrentRequest setParentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public ConcurrentRequest setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public ConcurrentRequest setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
        return this;
    }

    public String getJobStatusMeaning() {
        return jobStatusMeaning;
    }

    public ConcurrentRequest setJobStatusMeaning(String jobStatusMeaning) {
        this.jobStatusMeaning = jobStatusMeaning;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public ConcurrentRequest setUsername(String username) {
        this.username = username;
        return this;
    }

    public Long getTaskId() {
        return taskId;
    }

    public ConcurrentRequest setTaskId(Long taskId) {
        this.taskId = taskId;
        return this;
    }
}
