package org.hzero.admin.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * api监控分析
 * monitorRuleId:monitorUrl:monitorKey唯一，目前使用ip作为monitorKey
 * - 支持所有时间窗口统计数据分析，包括最大、最小、平均、平均失败、总数、总失败
 * - [开始时间,结束时间]区间内的数据分析
 *
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/12/9 2:43 下午
 */
@ApiModel("api监控分析")
@VersionAudit
@ModifyAudit
@Table(name = "hadm_api_monitor")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiMonitor extends AuditDomain {

    public static final String ENCRYPT_KEY = "hadm_api_monitor";

    public static final String FIELD_API_MONITOR_ID = "apiMonitorId";

    @Encrypt
    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long apiMonitorId;
    @Encrypt
    @ApiModelProperty(value = "api监控配置ID")
    @NotNull
    private Long monitorRuleId;
    @ApiModelProperty(value = "监控url")
    @NotEmpty
    private String monitorUrl;
    @ApiModelProperty(value = "监控key")
    @NotEmpty
    private String monitorKey;
    @ApiModelProperty(value = "统计最大请求数")
    private Integer maxStatistics;
    @ApiModelProperty(value = "统计最小请求数")
    private Integer minStatistics;
    @ApiModelProperty(value = "统计平均失败请求数")
    private Integer avgFailedStatistics;
    @ApiModelProperty(value = "统计平均请求数")
    private Integer avgStatistics;
    @ApiModelProperty(value = "统计总失败请求数")
    private Integer sumFailedStatistics;
    @ApiModelProperty(value = "统计总请求数")
    private Integer sumStatistics;
    @ApiModelProperty(value = "开始时间")
    private Date startDate;
    @ApiModelProperty(value = "结束时间")
    private Date endDate;

    @Transient
    private int avgCount;
    @Transient
    private boolean inBlacklist;

    public void setAvgCount(int avgCount) {
        this.avgCount = avgCount;
    }

    public Long getApiMonitorId() {
        return apiMonitorId;
    }

    public ApiMonitor setApiMonitorId(Long apiMonitorId) {
        this.apiMonitorId = apiMonitorId;
        return this;
    }

    public Long getMonitorRuleId() {
        return monitorRuleId;
    }

    public ApiMonitor setMonitorRuleId(Long monitorRuleId) {
        this.monitorRuleId = monitorRuleId;
        return this;
    }

    public String getMonitorUrl() {
        return monitorUrl;
    }

    public ApiMonitor setMonitorUrl(String monitorUrl) {
        this.monitorUrl = monitorUrl;
        return this;
    }

    public String getMonitorKey() {
        return monitorKey;
    }

    public ApiMonitor setMonitorKey(String monitorKey) {
        this.monitorKey = monitorKey;
        return this;
    }

    public Integer getMaxStatistics() {
        return maxStatistics;
    }

    public ApiMonitor setMaxStatistics(Integer maxStatistics) {
        this.maxStatistics = maxStatistics;
        return this;
    }

    public Integer getMinStatistics() {
        return minStatistics;
    }

    public ApiMonitor setMinStatistics(Integer minStatistics) {
        this.minStatistics = minStatistics;
        return this;
    }

    public Integer getAvgFailedStatistics() {
        return avgFailedStatistics;
    }

    public ApiMonitor setAvgFailedStatistics(Integer avgFailedStatistics) {
        this.avgFailedStatistics = avgFailedStatistics;
        return this;
    }

    public Integer getAvgStatistics() {
        return avgStatistics;
    }

    public ApiMonitor setAvgStatistics(Integer avgStatistics) {
        this.avgStatistics = avgStatistics;
        return this;
    }

    public Integer getSumFailedStatistics() {
        return sumFailedStatistics;
    }

    public ApiMonitor setSumFailedStatistics(Integer sumFailedStatistics) {
        this.sumFailedStatistics = sumFailedStatistics;
        return this;
    }

    public Integer getSumStatistics() {
        return sumStatistics;
    }

    public ApiMonitor setSumStatistics(Integer sumStatistics) {
        this.sumStatistics = sumStatistics;
        return this;
    }

    public Date getStartDate() {
        return startDate;
    }

    public ApiMonitor setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public Date getEndDate() {
        return endDate;
    }

    public ApiMonitor setEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    public void addCountAndRefreshAvg(int avgCount){
        this.avgCount += avgCount;
        avgFailedStatistics = sumFailedStatistics / this.avgCount ;
        avgStatistics = sumStatistics / this.avgCount;
    }

    public boolean isInBlacklist() {
        return inBlacklist;
    }

    public void setInBlacklist(boolean inBlacklist) {
        this.inBlacklist = inBlacklist;
    }
}
