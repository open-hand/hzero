package org.hzero.iam.api.dto;

import org.hzero.starter.keyencrypt.core.Encrypt;
import org.modelmapper.internal.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;

/**
 *
 * @author bojiangzhou 2020/02/19
 */
public class SecGrpDclQueryDTO {


    @ApiModelProperty("编码")
    private String dataCode;
    @ApiModelProperty("名称")
    private String dataName;

    @ApiModelProperty("安全组ID")
    @Encrypt
    private Long secGrpId;
    @ApiModelProperty("当前角色ID")
    @Encrypt
    private Long roleId;
    @ApiModelProperty(value = "当前租户ID", hidden = true)
    @JsonIgnore
    private Long tenantId;
    @ApiModelProperty("类型")
    private String authorityTypeCode;
    @ApiModelProperty("安全组源")
    private String secGrpSource;

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private Integer selfDim;

    public void validateSecGrpParam() {
        Assert.notNull(this.roleId, "Param role not be null");
        Assert.notNull(this.authorityTypeCode, "Param authorityTypeCode not be null");
        Assert.notNull(this.secGrpSource, "Param secGrpSource not be null");
    }

    public void validateRoleParam() {
        Assert.notNull(this.roleId, "Param role not be null");
        Assert.notNull(this.authorityTypeCode, "Param authorityTypeCode not be null");
    }

    public void validateUserParam() {
        Assert.notNull(this.authorityTypeCode, "Param authorityTypeCode not be null");
    }

    public String getDataCode() {
        return dataCode;
    }

    public void setDataCode(String dataCode) {
        this.dataCode = dataCode;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public Long getSecGrpId() {
        return secGrpId;
    }

    public void setSecGrpId(Long secGrpId) {
        this.secGrpId = secGrpId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getAuthorityTypeCode() {
        return authorityTypeCode;
    }

    public void setAuthorityTypeCode(String authorityTypeCode) {
        this.authorityTypeCode = authorityTypeCode;
    }

    public String getSecGrpSource() {
        return secGrpSource;
    }

    public void setSecGrpSource(String secGrpSource) {
        this.secGrpSource = secGrpSource;
    }

    public Integer getSelfDim() {
        return selfDim;
    }

    public void setSelfDim(Integer selfDim) {
        this.selfDim = selfDim;
    }
}
