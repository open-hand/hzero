package org.hzero.sso.core.domain;

import java.io.Serializable;

public class SsoTenant implements Serializable {
    private static final long serialVersionUID = 1088718904361897837L;

    private Long tenantId;
    private Long companyId;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    @Override
    public String toString() {
        return "SsoTenant{" +
                "tenantId=" + tenantId +
                ", companyId=" + companyId +
                '}';
    }
}