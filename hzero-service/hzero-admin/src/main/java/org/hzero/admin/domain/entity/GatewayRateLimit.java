package org.hzero.admin.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.admin.domain.repository.GatewayRateLimitRepository;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * 网关限流设置
 *
 * @author jianbo.li@china-hand.com 2018-09-10 21:40:45
 */
@ApiModel("网关限流设置")
@VersionAudit
@ModifyAudit
@Table(name = "hadm_gw_rate_limit")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GatewayRateLimit extends AuditDomain {

    public static final String ENCRYPT_KEY = "hadm_gw_rate_limit";

    public static final String FIELD_RATE_LIMIT_ID = "rateLimitId";
    public static final String FIELD_RATE_LIMIT_KEY = "rateLimitKey";
    public static final String FIELD_RATE_LIMIT_TYPE = "rateLimitType";
    public static final String FIELD_SERVICE_NAME = "serviceName";
    public static final String FIELD_SERVICE_CONF_LABEL = "serviceConfLabel";
    public static final String FIELD_SERVICE_CONF_PROFILE = "serviceConfProfile";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_REMARK = "remark";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 限流唯一性索引集合
     */
    enum GatewayRateLimitUniqueIndex {
        /**
         * 限流key
         */
        RATE_LIMIT_KEY("RateLimitKey"),
        /**
         * 限流类型
         */
        RATE_LIMIT_TYPE("RateLimitType"),
        /**
         * 服务名称
         */
        SERVICE_NAME("ServiceName"),
        /**
         * 服务标签
         */
        SERVICE_CONF_LABEL("ServiceConfLabel"),
        /**
         * 服务配置文件
         */
        SERVICE_CONF_PROFILE("ServiceConfProfile");
        private String key;

        GatewayRateLimitUniqueIndex(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    /**
     * 判断更新字段是否涉及唯一性索引
     *
     * @return
     */
    public boolean judgeUpdateFieldIsUniqueIndex(GatewayRateLimitRepository GatewayRateLimitRepository) {
        GatewayRateLimit oriData = GatewayRateLimitRepository.selectByPrimaryKey(getRateLimitId());
        try {
            for (GatewayRateLimitUniqueIndex uniqueIndex : GatewayRateLimitUniqueIndex.values()) {
                Object oriValue = oriData.getClass().getMethod("get" + uniqueIndex.getKey()).invoke(oriData);
                Object newValue = this.getClass().getMethod("get" + uniqueIndex.getKey()).invoke(this);
                if (strIsNotEqual(oriValue, newValue)) {
                    return true;
                }
            }
        } catch (Exception e) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR);
        }
        return false;
    }

    /**
     * 判断两个对象是否不相等
     *
     * @param one   一个对象
     * @param other 另一个比较对象
     * @return 若不想等，true
     */
    private boolean strIsNotEqual(Object one, Object other) {
        return one != null ? !one.equals(other) : other != null;
    }


    /**
     * 校验唯一性索引
     */
    public void validUniqueIndex(GatewayRateLimitRepository GatewayRateLimitRepository) {
        GatewayRateLimit GatewayRateLimit = new GatewayRateLimit();
        GatewayRateLimit.setRateLimitKey(getRateLimitKey());
        GatewayRateLimit.setRateLimitType(getRateLimitType());
        GatewayRateLimit.setServiceName(getServiceName());
        GatewayRateLimit.setServiceConfLabel(getServiceConfLabel());
        GatewayRateLimit.setServiceConfProfile(getServiceConfProfile());
        if (GatewayRateLimitRepository.selectOne(GatewayRateLimit) != null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_EXISTS);
        }
    }


    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Encrypt
    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long rateLimitId;
    @ApiModelProperty(value = "代码")
    @NotBlank
    @Size(max = 80)
    private String rateLimitKey;
    @ApiModelProperty(value = "限流方式，HADM.RATE_LIMIT_TYPE")
    @LovValue(lovCode = "HADM.RATE_LIMIT_TYPE", meaningField = "rateLimitTypeMeaning")
    @NotBlank
    private String rateLimitType;
    /**
     * 暂未使用
     */
    @ApiModelProperty(value = "gateway微服务名称")
    private String serviceName;
    @ApiModelProperty(value = "gateway微服务配置标签")
    private String serviceConfLabel;
    @ApiModelProperty(value = "gateway微服务配置Profile")
    @Size(max = 240)
    private String serviceConfProfile;
    @ApiModelProperty(value = "是否启用。1启用，0未启用")
    @NotNull
    private Integer enabledFlag;
    @ApiModelProperty(value = "备注说明")
    private String remark;
    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;
    @ApiModelProperty("刷新状态")
    @LovValue(lovCode = "HADM.REFRESH_STATUS", meaningField = "refreshStatusMeaning")
    private Long refreshStatus;
    @ApiModelProperty("刷新信息")
    private String refreshMessage;
    @ApiModelProperty("最新一次刷新时间")
    private Date refreshTime;
    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String rateLimitTypeMeaning;

    @Transient
    private String refreshStatusMeaning;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getRateLimitId() {
        return rateLimitId;
    }

    public void setRateLimitId(Long rateLimitId) {
        this.rateLimitId = rateLimitId;
    }

    /**
     * @return 代码
     */
    public String getRateLimitKey() {
        return rateLimitKey;
    }

    public void setRateLimitKey(String rateLimitKey) {
        this.rateLimitKey = rateLimitKey;
    }

    /**
     * @return 限流方式，HPFM.RATE_LIMIT_TYPE
     */
    public String getRateLimitType() {
        return rateLimitType;
    }

    public void setRateLimitType(String rateLimitType) {
        this.rateLimitType = rateLimitType;
    }

    /**
     * @return gateway微服务名称
     */
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return gateway微服务配置标签
     */
    public String getServiceConfLabel() {
        return serviceConfLabel;
    }

    public void setServiceConfLabel(String serviceConfLabel) {
        this.serviceConfLabel = serviceConfLabel;
    }

    /**
     * @return gateway微服务配置Profile
     */
    public String getServiceConfProfile() {
        return serviceConfProfile;
    }

    public void setServiceConfProfile(String serviceConfProfile) {
        this.serviceConfProfile = serviceConfProfile;
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
