package org.hzero.platform.domain.vo;

/**
 * description
 *
 * @author xiaoyu.zhao@hand-china.com 2019/01/24 15:41
 */
public class DashboardTenantCardVO {
    private Long tenantId;
    private Long cardId;

    public DashboardTenantCardVO(Long tenantId, Long cardId) {
        this.tenantId = tenantId;
        this.cardId = cardId;
    }

    public DashboardTenantCardVO() {
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    @Override
    public String toString() {
        return "DashboardTenantCardVO{" + "tenantId=" + tenantId + ", cardId=" + cardId + '}';
    }
}
