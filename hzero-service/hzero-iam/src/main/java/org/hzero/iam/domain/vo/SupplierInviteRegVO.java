package org.hzero.iam.domain.vo;

import java.time.LocalDate;

/**
 * 邀请供应商注册
 */
public class SupplierInviteRegVO {

    /**
     * 检验注册有效期; 0为无效，1为有效
     * 
     * @return boolean
     */
    public boolean compareExpirationDate() {
        return LocalDate.now().isBefore(this.expirationDate);
    }

    private Long inviteRegisterId;
    private String invitationCode;
    private Long tenantId;
    private Long companyId;
    private LocalDate expirationDate;

    public Long getInviteRegisterId() {
        return inviteRegisterId;
    }

    public void setInviteRegisterId(Long inviteRegisterId) {
        this.inviteRegisterId = inviteRegisterId;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

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

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
}
