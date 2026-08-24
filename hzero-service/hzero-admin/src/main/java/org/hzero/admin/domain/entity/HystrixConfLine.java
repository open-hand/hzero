package org.hzero.admin.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Hystrix保护设置行明细
 *
 * @author xingxing.wu@hand-china.com 2018-09-11 14:05:14
 */
@ApiModel("Hystrix保护设置行明细")
@VersionAudit
@ModifyAudit
@Table(name = "hadm_hystrix_conf_line")
public class HystrixConfLine extends AuditDomain {

	public static final String ENCRYPT_KEY = "hadm_hystrix_conf_line";

	public static final String FIELD_CONF_LINE_ID = "confLineId";
    public static final String FIELD_CONF_ID = "confId";
    public static final String FIELD_PROPERTY_NAME = "propertyName";
    public static final String FIELD_PROPERTY_VALUE = "propertyValue";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_ORDER_SEQ = "orderSeq";
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
    private Long confLineId;
	@Encrypt
    @ApiModelProperty(value = "配置ID",required = true)
    @NotNull
    private Long confId;
    @ApiModelProperty(value = "参数名，HPFM_HYSTRIX_CONF_PROPS，与CONF_TYPE为父子值集",required = true)
    @NotBlank
	@Size(max = 240)
    private String propertyName;
    @ApiModelProperty(value = "参数值",required = true)
    @NotBlank
	@Size(max = 120)
    private String propertyValue;
    @ApiModelProperty(value = "是否启用。1启用，0未启用",required = true)
    @NotNull
    private Integer enabledFlag;
   @ApiModelProperty(value = "排序号")    
    private Long orderSeq;
   @ApiModelProperty(value = "备注说明")    
    private String remark;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
	public Long getConfLineId() {
		return confLineId;
	}

	public void setConfLineId(Long confLineId) {
		this.confLineId = confLineId;
	}
    /**
     * @return 配置ID
     */
	public Long getConfId() {
		return confId;
	}

	public void setConfId(Long confId) {
		this.confId = confId;
	}
    /**
     * @return 参数名，HPFM_HYSTRIX_CONF_PROPS，与CONF_TYPE为父子值集
     */
	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
    /**
     * @return 参数值
     */
	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
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

}
