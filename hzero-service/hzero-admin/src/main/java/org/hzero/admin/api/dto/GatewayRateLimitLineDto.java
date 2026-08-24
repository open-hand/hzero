package org.hzero.admin.api.dto;

import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 *
 *
 * @author jianbo.li@china-hand.com 2018-09-10 21:40:45
 */
public class GatewayRateLimitLineDto {

    @Encrypt
    private Long rateLimitLineId;

    @Encrypt
    private Long rateLimitId;

    @Encrypt
    private Long serviceRouteId;
    private Integer replenishRate;
    private Integer burstCapacity;
    private String rateLimitDimension;

    private Integer enabledFlag;
    private String remark;
    private Long objectVersionNumber;

    public Long getRateLimitLineId() {
        return rateLimitLineId;
    }

    public void setRateLimitLineId(Long rateLimitLineId) {
        this.rateLimitLineId = rateLimitLineId;
    }

    public Long getRateLimitId() {
        return rateLimitId;
    }

    public void setRateLimitId(Long rateLimitId) {
        this.rateLimitId = rateLimitId;
    }

    public Long getServiceRouteId() {
        return serviceRouteId;
    }

    public void setServiceRouteId(Long serviceRouteId) {
        this.serviceRouteId = serviceRouteId;
    }

    public Integer getReplenishRate() {
        return replenishRate;
    }

    public void setReplenishRate(Integer replenishRate) {
        this.replenishRate = replenishRate;
    }

    public Integer getBurstCapacity() {
        return burstCapacity;
    }

    public void setBurstCapacity(Integer burstCapacity) {
        this.burstCapacity = burstCapacity;
    }

    public String getRateLimitDimension() {
        return rateLimitDimension;
    }

    public void setRateLimitDimension(String rateLimitDimension) {
        this.rateLimitDimension = rateLimitDimension;
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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
