package org.hzero.platform.api.dto;

/**
 * 引用该汇率类型的租户信息
 *
 * @author liang.jin@hand-china.com 2018/07/02 10:57
 */
public class TenantRateTypeDTO {
    private Long rateTypeTnId;
    private String tenantNum;
    private String tenantName;
    private String typeCode;
    private String typeName;
    private Integer enabledFlag;

    public Long getRateTypeTnId() {
        return rateTypeTnId;
    }

    public void setRateTypeTnId(Long rateTypeTnId) {
        this.rateTypeTnId = rateTypeTnId;
    }

    public String getTenantNum() {
        return tenantNum;
    }

    public void setTenantNum(String tenantNum) {
        this.tenantNum = tenantNum;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }
}
