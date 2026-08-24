package org.hzero.iam.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;

/**
 * @author jianyuan.wei@hand-china.com
 * @date 2020/2/13 18:44
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("安全组分配关系")
@VersionAudit
@ModifyAudit
@Table(name = "hiam_sec_grp_assign")
public class SecGrpAssign extends AuditDomain {
    public static final String ENCRYPT_KEY = "hiam_sec_grp_assign";
    public SecGrpAssign() {

    }

    public SecGrpAssign(String assignDimension, Long dimensionValue, Long secGrpId, Long tenantId) {
        this.assignDimension = assignDimension;
        this.dimensionValue = dimensionValue;
        this.secGrpId = secGrpId;
        this.tenantId = tenantId;
    }

    @Id
    @GeneratedValue
    @Encrypt
    private Long userSecGrpId;
    /**
     * 分配维度[USER/ROLE]
     */
    private String assignDimension;
    /**
     * 维度值[USER(用户ID),ROLE(角色ID)]
     */
    private Long dimensionValue;
    /**
     * 安全组ID
     */
    @Encrypt
    private Long secGrpId;
    /**
     * 租户ID
     */
    private Long tenantId;

    public Long getUserSecGrpId() {
        return userSecGrpId;
    }

    public void setUserSecGrpId(Long userSecGrpId) {
        this.userSecGrpId = userSecGrpId;
    }

    public String getAssignDimension() {
        return assignDimension;
    }

    public void setAssignDimension(String assignDimension) {
        this.assignDimension = assignDimension;
    }

    public Long getDimensionValue() {
        return dimensionValue;
    }

    public void setDimensionValue(Long dimensionValue) {
        this.dimensionValue = dimensionValue;
    }

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
}
