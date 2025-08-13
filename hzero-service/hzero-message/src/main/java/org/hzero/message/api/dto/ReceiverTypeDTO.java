package org.hzero.message.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.message.domain.entity.ReceiverType;
import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author 润柏哥
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReceiverTypeDTO implements SecurityToken {

    @Encrypt
    private Long receiverTypeId;
    private String typeCode;
    private String typeName;
    @LovValue(lovCode = "HMSG.RECEIVER.TYPE_MODE")
    private String typeModeCode;
    private String typeModeMeaning;
    private String typeMeaning;
    private String routeName;
    private String apiUrl;
    private Integer enabledFlag;
    private Long tenantId;
    private Long objectVersionNumber;
    private String tenantName;
    private String _token;

    public Long getReceiverTypeId() {
        return receiverTypeId;
    }

    public void setReceiverTypeId(Long receiverTypeId) {
        this.receiverTypeId = receiverTypeId;
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

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    @Override
    public String get_token() {
        return this._token;
    }

    @Override
    public void set_token(String _token) {
        this._token = _token;
    }

    @Override
    public Class<? extends SecurityToken> associateEntityClass() {
        return ReceiverType.class;
    }

    @Override
    public String toString() {
        return "ReceiverTypeDTO{" +
                "receiverTypeId=" + receiverTypeId +
                ", typeCode='" + typeCode + '\'' +
                ", typeName='" + typeName + '\'' +
                ", typeModeCode='" + typeModeCode + '\'' +
                ", routeName='" + routeName + '\'' +
                ", apiUrl='" + apiUrl + '\'' +
                ", enabledFlag=" + enabledFlag +
                ", tenantId=" + tenantId +
                ", objectVersionNumber=" + objectVersionNumber +
                ", tenantName='" + tenantName + '\'' +
                '}';
    }

    public String getTypeMeaning() {
        return typeMeaning;
    }

    public void setTypeMeaning(String typeMeaning) {
        this.typeMeaning = typeMeaning;
    }

    public String getTypeModeMeaning() {
        return typeModeMeaning;
    }

    public void setTypeModeMeaning(String typeModeMeaning) {
        this.typeModeMeaning = typeModeMeaning;
    }

    public String getTypeModeCode() {
        return typeModeCode;
    }

    public void setTypeModeCode(String typeModeCode) {
        this.typeModeCode = typeModeCode;
    }
}
