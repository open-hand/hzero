package org.hzero.admin.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.mybatis.common.query.Where;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * Hystrix保护设置
 *
 * @author xingxing.wu@hand-china.com 2018-09-11 14:05:14
 */
@ApiModel("Hystrix保护设置")
@VersionAudit
@ModifyAudit
@Table(name = "hadm_hystrix_conf")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HystrixConf extends AuditDomain {

    public static final String ENCRYPT_KEY = "hadm_hystrix_conf";

    public static final String FIELD_CONF_ID = "confId";
    public static final String FIELD_CONF_KEY = "confKey";
    public static final String FIELD_CONF_TYPE_CODE = "confTypeCode";
    public static final String FIELD_SERVICE_NAME = "serviceName";
    public static final String FIELD_SERVICE_CONF_LABEL = "serviceConfLabel";
    public static final String FIELD_SERVICE_CONF_PROFILE = "serviceConfProfile";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_ORDER_SEQ = "orderSeq";
    public static final String FIELD_REMARK = "remark";
    public static final String REFRESH_STATUS = "refreshStatus";
    public static final String REFRESH_MESSAGE = "refreshMessage";
    public static final String REFRESH_TIME = "refreshTime";

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
    @Where
    private Long confId;
    @ApiModelProperty(value = "代码Key值，作为分组依据，例如，如果类型为命令参数/请求参数，则此值为commandKey，如果为线程池参数，则此值为threadPoolKey", required = true)
    @NotBlank
    @Size(max = 120)
    private String confKey;
    @ApiModelProperty(value = "命令参数/线程池参数/请求参数，值级：HPFM.HYSTRIX_CONF_TYPE", required = true)
    @NotBlank
    @Size(max = 120)
    private String confTypeCode;
    @ApiModelProperty(value = "微服务名称", required = true)
    @NotBlank
    @Size(max = 120)
    private String serviceName;
    @ApiModelProperty(value = "微服务配置标签")
    @Size(max = 240)
    private String serviceConfLabel;
    @ApiModelProperty(value = "微服务配置Profile")
    @Size(max = 240)
    private String serviceConfProfile;
    @ApiModelProperty(value = "是否启用。1启用，0未启用", required = true)
    @NotNull
    private Integer enabledFlag;
    @ApiModelProperty(value = "排序号")
    private Long orderSeq;
    @ApiModelProperty(value = "备注说明")
    private String remark;
    @ApiModelProperty(value = "上次更新是否成功。1成功，0失败")
    private Integer refreshStatus;
    @ApiModelProperty(value = "更新信息")
    @Size(max = 360)
    private String refreshMessage;
    @ApiModelProperty(value = "上次更新时间")
    private Date refreshTime;
    @Transient
    private List<HystrixConfLine> hystrixConfLines;


    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getConfId() {
        return confId;
    }

    public void setConfId(Long confId) {
        this.confId = confId;
    }

    /**
     * @return 代码Key值，作为分组依据，例如，如果类型为命令参数/请求参数，则此值为commandKey，如果为线程池参数，则此值为threadPoolKey
     */
    public String getConfKey() {
        return confKey;
    }

    public void setConfKey(String confKey) {
        this.confKey = confKey;
    }

    /**
     * @return 命令参数/线程池参数/请求参数，值级：HPFM.HYSTRIX_CONF_TYPE
     */
    public String getConfTypeCode() {
        return confTypeCode;
    }

    public void setConfTypeCode(String confTypeCode) {
        this.confTypeCode = confTypeCode;
    }

    /**
     * @return 微服务名称
     */
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return 微服务配置标签
     */
    public String getServiceConfLabel() {
        return serviceConfLabel;
    }

    public void setServiceConfLabel(String serviceConfLabel) {
        this.serviceConfLabel = serviceConfLabel;
    }

    /**
     * @return 微服务配置Profile
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
     * @return 排序号
     */
    public Long getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(Long orderSeq) {
        this.orderSeq = orderSeq;
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

    /**
     * @return 明细
     */
    public List<HystrixConfLine> getHystrixConfLines() {
        return hystrixConfLines;
    }

    public void setHystrixConfLines(List<HystrixConfLine> hystrixConfLines) {
        this.hystrixConfLines = hystrixConfLines;
    }

    public Integer getRefreshStatus() {
        return refreshStatus;
    }

    public void setRefreshStatus(Integer refreshStatus) {
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
}
