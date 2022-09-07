package org.hzero.iam.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.List;

/**
 * 安全组查询DTO
 *
 * @author xingxingwu.hand-china.com 2019/10/17 11:44
 */
public class SecGrpQueryDTO {
    private String secGrpCode;
    private String secGrpName;
    private Long tenantId;
    private String secGrpLevel;

    @Encrypt
    private Long currentRoleId;
    private Long currentRoleTenantId;
    /**
     * 查询层级
     */
    private String queryLevel;
    /**
     * 安全组来源 Constants.SecGrpSource self：当前角色 children：子角色 parent：父级角色
     */
    private String secGrpSource;

    private Integer enabledFlag;

    @JsonIgnore
    private boolean excludeAssigned = true;

    @Encrypt
    private Long roleId;

    @Encrypt
    private List<Long> secGrpIds;

    public void setupCurrentRole() {
        SecGrp secGrp = new SecGrp();
        secGrp.setRoleId(this.roleId);
        secGrp.setupCurrentRole(null);

        this.secGrpLevel = secGrp.getSecGrpLevel();
        this.roleId = secGrp.getRoleId();
        this.currentRoleId = this.roleId;
    }

    public void selectEnabled() {
        this.enabledFlag = BaseConstants.Flag.YES;
    }

    public String getSecGrpCode() {
        return secGrpCode;
    }

    public void setSecGrpCode(String secGrpCode) {
        this.secGrpCode = secGrpCode;
    }

    public String getSecGrpName() {
        return secGrpName;
    }

    public void setSecGrpName(String secGrpName) {
        this.secGrpName = secGrpName;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getSecGrpLevel() {
        return secGrpLevel;
    }

    public void setSecGrpLevel(String secGrpLevel) {
        this.secGrpLevel = secGrpLevel;
    }

    public String getQueryLevel() {
        return queryLevel;
    }

    public void setQueryLevel(String queryLevel) {
        this.queryLevel = queryLevel;
    }

    public Long getCurrentRoleId() {
        return currentRoleId;
    }

    public void setCurrentRoleId(Long currentRoleId) {
        this.currentRoleId = currentRoleId;
    }

    public String getSecGrpSource() {
        return secGrpSource;
    }

    public void setSecGrpSource(String secGrpSource) {
        this.secGrpSource = secGrpSource;
    }

    public Long getCurrentRoleTenantId() {
        return currentRoleTenantId;
    }

    public void setCurrentRoleTenantId(Long currentRoleTenantId) {
        this.currentRoleTenantId = currentRoleTenantId;
    }

    public List<Long> getSecGrpIds() {
        return secGrpIds;
    }

    public void setSecGrpIds(List<Long> secGrpIds) {
        this.secGrpIds = secGrpIds;
    }

    public boolean isExcludeAssigned() {
        return excludeAssigned;
    }

    public void setExcludeAssigned(boolean excludeAssigned) {
        this.excludeAssigned = excludeAssigned;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
