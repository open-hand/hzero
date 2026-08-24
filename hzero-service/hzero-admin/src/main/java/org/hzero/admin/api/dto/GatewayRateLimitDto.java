package org.hzero.admin.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.admin.domain.entity.GatewayRateLimit;
import org.hzero.admin.domain.entity.GatewayRateLimitLine;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Transient;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * @author jianbo.li@china-hand.com 2018-09-10 21:40:45
 */
public class GatewayRateLimitDto extends AuditDomain {
    @Encrypt
    private Long rateLimitId;
    @Size(max = 80)
    private String rateLimitKey;
    @LovValue(lovCode = "HADM.RATE_LIMIT_TYPE", meaningField = "rateLimitTypeMeaning")
    private String rateLimitType;
    private String serviceName;
    private String serviceConfLabel;
    @Size(max = 240)
    private String serviceConfProfile;
    private Integer enabledFlag;
    private String remark;
    private Long objectVersionNumber;
    @ApiModelProperty("刷新状态")
    @LovValue(lovCode = "HADM.REFRESH_STATUS", meaningField = "refreshStatusMeaning")
    private Long refreshStatus;
    @ApiModelProperty("刷新信息")
    private String refreshMessage;
    @ApiModelProperty("最新一次刷新时间")
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATETIME)
    @JsonFormat(pattern = BaseConstants.Pattern.DATETIME)
    private Date refreshTime;

    @Transient
    private List<GatewayRateLimitLine> gatewayRateLimitLineList;

    @Transient
    private String rateLimitTypeMeaning;

    @Transient
    private String refreshStatusMeaning;

    @JsonIgnore
    private Date creationDate;
    @JsonIgnore
    private Long createdBy;
    @JsonIgnore
    private Date lastUpdateDate;
    @JsonIgnore
    private Long lastUpdatedBy;

    @Override
    public Class<? extends SecurityToken> associateEntityClass() {
        return GatewayRateLimit.class;
    }

    public Long getRateLimitId() {
        return rateLimitId;
    }

    public void setRateLimitId(Long rateLimitId) {
        this.rateLimitId = rateLimitId;
    }

    public String getRateLimitKey() {
        return rateLimitKey;
    }

    public void setRateLimitKey(String rateLimitKey) {
        this.rateLimitKey = rateLimitKey;
    }

    public String getRateLimitType() {
        return rateLimitType;
    }

    public void setRateLimitType(String rateLimitType) {
        this.rateLimitType = rateLimitType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceConfLabel() {
        return serviceConfLabel;
    }

    public void setServiceConfLabel(String serviceConfLabel) {
        this.serviceConfLabel = serviceConfLabel;
    }

    public String getServiceConfProfile() {
        return serviceConfProfile;
    }

    public void setServiceConfProfile(String serviceConfProfile) {
        this.serviceConfProfile = serviceConfProfile;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<GatewayRateLimitLine> getGatewayRateLimitLineList() {
        return gatewayRateLimitLineList;
    }

    public void setGatewayRateLimitLineList(List<GatewayRateLimitLine> gatewayRateLimitLineList) {
        this.gatewayRateLimitLineList = gatewayRateLimitLineList;
    }

    @Override
    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    @Override
    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getRefreshStatus() {
        return refreshStatus;
    }

    public void setRefreshStatus(Long refreshStatus) {
        this.refreshStatus = refreshStatus;
    }

    public String getRefreshMessage() {
        return refreshMessage;
    }

    public void setRefreshMessage(String refreshMessage) {
        this.refreshMessage = refreshMessage;
    }

    public Date getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(Date refreshTime) {
        this.refreshTime = refreshTime;
    }

    @Override
    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public Long getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    @Override
    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    @Override
    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    @Override
    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getRateLimitTypeMeaning() {
        return rateLimitTypeMeaning;
    }

    public void setRateLimitTypeMeaning(String rateLimitTypeMeaning) {
        this.rateLimitTypeMeaning = rateLimitTypeMeaning;
    }

    public String getRefreshStatusMeaning() {
        return refreshStatusMeaning;
    }

    public void setRefreshStatusMeaning(String refreshStatusMeaning) {
        this.refreshStatusMeaning = refreshStatusMeaning;
    }
}
