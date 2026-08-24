package org.hzero.iam.domain.service.secgrp.dto;

import org.hzero.iam.domain.entity.SecGrpAclField;

/**
 * 安全组字段权限回收行DTO
 *
 * @author allen.liu
 * @date 2019/12/16
 */
public class SecGrpAclFieldRevokeLineDTO {
    private Long secGrpAclFieldId;
    private Long secGrpId;
    private Long tenantId;
    private Long fieldId;
    private String assignTypeCode;

    public SecGrpAclFieldRevokeLineDTO(SecGrpAclField secGrpAclField){
        this.secGrpAclFieldId = secGrpAclField.getSecGrpAclFieldId();
        this.secGrpId = secGrpAclField.getSecGrpId();
        this.tenantId = secGrpAclField.getTenantId();
        this.fieldId = secGrpAclField.getFieldId();
        this.assignTypeCode = secGrpAclField.getAssignTypeCode();
    }


    public Long getSecGrpAclFieldId() {
        return secGrpAclFieldId;
    }

    public SecGrpAclFieldRevokeLineDTO setSecGrpAclFieldId(Long secGrpAclFieldId) {
        this.secGrpAclFieldId = secGrpAclFieldId;
        return this;
    }

    public Long getSecGrpId() {
        return secGrpId;
    }

    public SecGrpAclFieldRevokeLineDTO setSecGrpId(Long secGrpId) {
        this.secGrpId = secGrpId;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public SecGrpAclFieldRevokeLineDTO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public SecGrpAclFieldRevokeLineDTO setFieldId(Long fieldId) {
        this.fieldId = fieldId;
        return this;
    }

    public String getAssignTypeCode() {
        return assignTypeCode;
    }

    public void setAssignTypeCode(String assignTypeCode) {
        this.assignTypeCode = assignTypeCode;
    }
}
