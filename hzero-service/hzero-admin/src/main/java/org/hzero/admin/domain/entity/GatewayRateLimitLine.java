package org.hzero.admin.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 * 网关限流设置行明细
 *
 * @author jianbo.li@china-hand.com 2018-09-10 21:40:45
 */
@ApiModel("网关限流设置行明细")
@VersionAudit
@ModifyAudit
@Table(name = "hadm_gw_rate_limit_line")
public class GatewayRateLimitLine extends AuditDomain {

    public static final String ENCRYPT_KEY = "hadm_gw_rate_limit_line";

    public static final String FIELD_RATE_LIMIT_LINE_ID = "rateLimitLineId";
    public static final String FIELD_RATE_LIMIT_ID = "rateLimitId";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_REMARK = "remark";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Encrypt
    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long rateLimitLineId;
    @Encrypt
    @ApiModelProperty(value = "gateway限流设置ID", required = true)
    @NotNull
    private Long rateLimitId;
    @Encrypt
    @ApiModelProperty(value = "服务路由id", required = true)
    @NotNull
    private Long serviceRouteId;
    @ApiModelProperty(value = "每秒限制量", required = true)
    @NotNull
    @Range(min = 1L)
    private Integer replenishRate;
    @ApiModelProperty(value = "突发流量限制大小")
    @Range(min = 1L)
    private Integer burstCapacity;
    /**
     * user/role/tenant/origin/url
     */
    @ApiModelProperty(value = "限流维度")
    private String rateLimitDimension;

    @Transient
    private String path;

    @ApiModelProperty(value = "是否启用。1启用，0未启用", required = true)
    @NotNull
    private Integer enabledFlag;
    @ApiModelProperty(value = "备注说明")
    private String remark;
    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getRateLimitLineId() {
        return rateLimitLineId;
    }

    public void setRateLimitLineId(Long rateLimitLineId) {
        this.rateLimitLineId = rateLimitLineId;
    }

    /**
     * @return gateway限流设置ID
     */
    public Long getRateLimitId() {
        return rateLimitId;
    }

    public void setRateLimitId(Long rateLimitId) {
        this.rateLimitId = rateLimitId;
    }

    public Long getServiceRouteId() {
        return serviceRouteId;
    }

    public GatewayRateLimitLine setServiceRouteId(Long serviceRouteId) {
        this.serviceRouteId = serviceRouteId;
        return this;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return 是否启用。1启用，0未启用
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    /**
     * @return 备注说明
     */
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    @Override
    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

}
