package org.hzero.platform.domain.entity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.choerodon.core.exception.CommonException;
import javax.validation.constraints.NotEmpty;

import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import io.choerodon.mybatis.domain.AuditDomain;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.common.query.Where;
import org.hzero.platform.domain.repository.LovViewLineRepository;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.Objects;

/**
 * 值集视图行
 *
 * @author gaokuo.dai@hand-china.com 2018-06-21 14:01:12
 */
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_lov_view_line")
@ApiModel("值集视图行")
@JsonInclude(JsonInclude.Include.NON_NULL)
@MultiLanguage
public class LovViewLine extends AuditDomain {

    public static final String FIELD_VIEW_LINE_ID = "viewLineId";
    public static final String FIELD_VIEW_HEADER_ID = "viewHeaderId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_LOV_ID = "lovId";
    public static final String FIELD_DISPLAY = "display";
    public static final String FIELD_ORDER_SEQ = "orderSeq";
    public static final String FIELD_FIELD_NAME = "fieldName";
    public static final String FIELD_QUERY_FIELD_FLAG = "queryFieldFlag";
    public static final String FIELD_TABLE_FIELD_FLAG = "tableFieldFlag";
    public static final String FIELD_TABLE_FIELD_WIDTH = "tableFieldWidth";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_DATA_TYPE = "dataType";
    public static final String FIELD_SOURCE_CODE = "sourceCode";


    //
    // 业务方法
    // ------------------------------------------------------------------------------

	/**
	 * 租户级校验值集视图头的租户Id是否与数据库中值集视图头租户Id相同
	 */
	public void checkOrgLovViewLineTenant(LovViewLineRepository lovViewLineRepository) {
		LovViewLine lovViewLine = lovViewLineRepository.selectByPrimaryKey(this.viewLineId);
		Assert.notNull(lovViewLine, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
		// 更新值集值时需要校验传入的值集Id是否与当前租户Id匹配，不匹配则抛出异常
		if (!Objects.equals(lovViewLine.getTenantId(), this.tenantId)) {
			throw new CommonException(HpfmMsgCodeConstants.ERROR_LOV_TENANT_NOT_MATCH);
		}
	}
     
    //
    // 数据库字段
    // ------------------------------------------------------------------------------

	@Id
    @GeneratedValue
	@ApiModelProperty("视图行ID")
	@Encrypt
    private Long viewLineId;
	@NotNull
	@ApiModelProperty("视图头ID")
	@Where
	@Encrypt
	private Long viewHeaderId;
	@ApiModelProperty("租户ID")
	@MultiLanguageField
	private Long tenantId;
	@NotNull
	@ApiModelProperty("值集ID")
	@Encrypt
	private Long lovId;
	@NotEmpty
	@Size(max = 30)
	@ApiModelProperty("显示名称")
	@MultiLanguageField
	private String display;
	@NotNull
	@ApiModelProperty("排序号")
	private Long orderSeq;
	@NotEmpty
	@Size(max = 30)
	@ApiModelProperty("字段名")
	private String fieldName;
	@NotNull
	@Range(min = 0, max = 1)
	@ApiModelProperty("是否查询字段")
	private Integer queryFieldFlag;
	@NotNull
	@Range(min = 0, max = 1)
	@ApiModelProperty("是否表格列")
	private Integer tableFieldFlag;
	@NotNull
	@ApiModelProperty("表格列宽度")
	private Integer tableFieldWidth;
	@NotNull
	@Range(min = 0, max = 1)
	@ApiModelProperty("启用标识")
	private Integer enabledFlag;
	@Length(max = 30)
	@ApiModelProperty("表单控件类型，值集:HPFM.VIEW.DATA_TYPE")
	@LovValue("HPFM.VIEW.DATA_TYPE")
	private String dataType;
	@Length(max = 30)
	@ApiModelProperty("来源编码")
	private String sourceCode;
	
	//
    // 非数据库字段
    // ------------------------------------------------------------------------------

	@Transient
    @ApiModelProperty("租户名称")
    private String tenantName;
	@Transient
	private String dataTypeMeaning;
	
	//
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键 
     */
	public Long getViewLineId() {
		return viewLineId;
	}
		
	public void setViewLineId(Long viewLineId) {
		this.viewLineId = viewLineId;
	}
    /**
     * @return 值集查询视图头表ID,hpfm_lov_view_header.view_header_id
     */
	public Long getViewHeaderId() {
		return viewHeaderId;
	}
		
	public void setViewHeaderId(Long viewHeaderId) {
		this.viewHeaderId = viewHeaderId;
	}
    /**
     * @return 租户ID
     */
	public Long getTenantId() {
		return tenantId;
	}
		
	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}
    /**
     * @return 值集ID,hpfm_lov.lov_id
     */
	public Long getLovId() {
		return lovId;
	}
		
	public void setLovId(Long lovId) {
		this.lovId = lovId;
	}
    /**
     * @return 显示名称
     */
	public String getDisplay() {
		return display;
	}
		
	public void setDisplay(String display) {
		this.display = display;
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
     * @return 表格列名
     */
	public String getFieldName() {
		return fieldName;
	}
		
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
    /**
     * @return 是否查询字段 
     */
	public Integer getQueryFieldFlag() {
		return queryFieldFlag;
	}
		
	public void setQueryFieldFlag(Integer queryFieldFlag) {
		this.queryFieldFlag = queryFieldFlag;
	}
    /**
     * @return 是否表格列 
     */
	public Integer getTableFieldFlag() {
		return tableFieldFlag;
	}
		
	public void setTableFieldFlag(Integer tableFieldFlag) {
		this.tableFieldFlag = tableFieldFlag;
	}
    /**
     * @return 表格列宽度
     */
	public Integer getTableFieldWidth() {
		return tableFieldWidth;
	}
		
	public void setTableFieldWidth(Integer tableFieldWidth) {
		this.tableFieldWidth = tableFieldWidth;
	}
    /**
     * @return 是否启用 
     */
	public Integer getEnabledFlag() {
		return enabledFlag;
	}
		
	public void setEnabledFlag(Integer enabledFlag) {
		this.enabledFlag = enabledFlag;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	/**
     * @return 查询字段：租户名称
     */
    public String getTenantName() {
        return tenantName;
    }
    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

	public String getDataTypeMeaning() {
		return dataTypeMeaning;
	}

	public void setDataTypeMeaning(String dataTypeMeaning) {
		this.dataTypeMeaning = dataTypeMeaning;
	}

	@Override
	public String toString() {
		return "LovViewLine{" +
				"viewLineId=" + viewLineId +
				", viewHeaderId=" + viewHeaderId +
				", tenantId=" + tenantId +
				", lovId=" + lovId +
				", display='" + display + '\'' +
				", orderSeq=" + orderSeq +
				", fieldName='" + fieldName + '\'' +
				", queryFieldFlag=" + queryFieldFlag +
				", tableFieldFlag=" + tableFieldFlag +
				", tableFieldWidth=" + tableFieldWidth +
				", enabledFlag=" + enabledFlag +
				", dataType='" + dataType + '\'' +
				", tenantName='" + tenantName + '\'' +
				", dataTypeMeaning='" + dataTypeMeaning + '\'' +
				'}';
	}

}
