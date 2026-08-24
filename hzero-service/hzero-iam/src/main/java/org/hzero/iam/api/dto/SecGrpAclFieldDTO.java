package org.hzero.iam.api.dto;

import org.hzero.starter.keyencrypt.core.Encrypt;

import io.swagger.annotations.ApiModelProperty;

/**
 * 安全组字段权限DTO
 *
 * @author xingxingwu.hand-china.com 2019/10/28 11:18
 */
public class SecGrpAclFieldDTO {
    @ApiModelProperty(value = "安全组ID")
    @Encrypt
    private Long secGrpId;

    @ApiModelProperty(value = "租户")
    private Long tenantId;

    @ApiModelProperty(value = "API权限ID，iam_permission.id")
    @Encrypt
    private Long permissionId;

    @ApiModelProperty(value = "权限类型，值集HIAM.FIELD.PERMISSION_TYPE[HIDDEN]")
    private String permissionType;

    @ApiModelProperty(value = "字段名称")
    private String fieldName;

    @ApiModelProperty(value = "字段类型")
    private String fieldType;

    @ApiModelProperty(value = "字段含义")
    String fieldDescription;

    public Long getSecGrpId() {
        return secGrpId;
    }

    public void setSecGrpId(Long secGrpId) {
        this.secGrpId = secGrpId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }

    public String getFieldDescription() {
        return fieldDescription;
    }

    public void setFieldDescription(String fieldDescription) {
        this.fieldDescription = fieldDescription;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }
}
