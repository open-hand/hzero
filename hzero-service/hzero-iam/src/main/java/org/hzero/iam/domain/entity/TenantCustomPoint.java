package org.hzero.iam.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 租户客户化端点关系
 *
 * @author bojiangzhou 2019-12-12 11:40:55
 */
@ApiModel("租户客户化端点关系")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hiam_tenant_custom_point")
public class TenantCustomPoint extends AuditDomain {
	public static final String ENCRYPT_KEY = "hiam_tenant_custom_point";
	public static final String FIELD_TENANT_CUSTOM_POINT_ID = "tenantCustomPointId";
	public static final String FIELD_TENANT_ID = "tenantId";
	public static final String FIELD_CUSTOM_POINT_CODE = "customPointCode";

	public TenantCustomPoint() {
	}

	public TenantCustomPoint(Long tenantId, String customPointCode) {
		this.tenantId = tenantId;
		this.customPointCode = customPointCode;
	}


	//
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long tenantCustomPointId;
    @ApiModelProperty(value = "租户ID")
    private Long tenantId;
    @ApiModelProperty(value = "客户化端点编码，hiam_custom_point.code")
    private String customPointCode;

    @Transient
	@JsonIgnore
	private String tenantNum;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
	public Long getTenantCustomPointId() {
		return tenantCustomPointId;
	}

	public void setTenantCustomPointId(Long tenantCustomPointId) {
		this.tenantCustomPointId = tenantCustomPointId;
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
     * @return 客户化端点编码，hiam_custom_point.code
     */
	public String getCustomPointCode() {
		return customPointCode;
	}

	public void setCustomPointCode(String customPointCode) {
		this.customPointCode = customPointCode;
	}

	public String getTenantNum() {
		return tenantNum;
	}

	public void setTenantNum(String tenantNum) {
		this.tenantNum = tenantNum;
	}
}
