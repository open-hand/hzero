package org.hzero.boot.message.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Objects;

/**
 * 接收者类型
 *
 * @author runbai.chen@hand-china.com 2018-07-30 17:41:11
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("all")
public class ReceiverType {

	/**
	 * 接收者类型ID
	 */
    private Long receiverTypeId;

    /**
     * 类型代码
     */
    private String typeCode;

    /**
     * 类型名称
     */
    private String typeName;
    /**
     * 目标路由
     */
	private String routeName;

    /**
     * 服务URL
     */
    private String apiUrl;
    /**
     * 启用标识
     */
    private Integer enabledFlag;
    /**
     * 租户ID,hpfm_tenant.tenant_id
     */
    private Long tenantId;

	public Long getReceiverTypeId() {
		return receiverTypeId;
	}

    public void setReceiverTypeId(Long receiverTypeId) {
        this.receiverTypeId = receiverTypeId;
    }
    /**
     * @return 类型代码
     */
	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
    /**
     * @return 类型名称
     */
	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
    /**
     * @return 目标路由
     */
	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}
    /**
     * @return 服务URL
     */
	public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}
    /**
     * @return 启用标识
     */
	public Integer getEnabledFlag() {
		return enabledFlag;
	}

	public void setEnabledFlag(Integer enabledFlag) {
		this.enabledFlag = enabledFlag;
	}
    /**
     * @return 租户ID,hpfm_tenant.tenant_id
     */
	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ReceiverType)) {
			return false;
		}
		ReceiverType that = (ReceiverType) o;
		return Objects.equal(receiverTypeId, that.receiverTypeId) &&
				Objects.equal(typeCode, that.typeCode) &&
				Objects.equal(typeName, that.typeName) &&
				Objects.equal(routeName, that.routeName) &&
				Objects.equal(apiUrl, that.apiUrl) &&
				Objects.equal(enabledFlag, that.enabledFlag) &&
				Objects.equal(tenantId, that.tenantId);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(receiverTypeId, typeCode, typeName, routeName, apiUrl, enabledFlag, tenantId);
	}

	@Override
    public String toString() {
        return "ReceiverType{" +
                "receiverTypeId=" + receiverTypeId +
                ", typeCode='" + typeCode + '\'' +
                ", typeName='" + typeName + '\'' +
                ", routeName='" + routeName + '\'' +
                ", apiUrl='" + apiUrl + '\'' +
                ", enabledFlag=" + enabledFlag +
                ", tenantId=" + tenantId +
                '}';
    }
}
