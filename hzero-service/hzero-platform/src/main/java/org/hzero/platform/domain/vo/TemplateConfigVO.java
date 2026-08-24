package org.hzero.platform.domain.vo;

import java.util.Objects;

/**
 * 模板配置VO
 *
 * @author xiaoyu.zhao@hand-china.com 2019/07/19 11:40
 */
public class TemplateConfigVO {
    private Long configId;
    private String configTypeCode;
    private String configCode;
    private String configValue;
    private String remark;
    private Long tenantId;
    private Long templateAssignId;
    private Integer orderSeq;
    private String link;

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public String getConfigTypeCode() {
        return configTypeCode;
    }

    public void setConfigTypeCode(String configTypeCode) {
        this.configTypeCode = configTypeCode;
    }

    public String getConfigCode() {
        return configCode;
    }

    public void setConfigCode(String configCode) {
        this.configCode = configCode;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getTemplateAssignId() {
        return templateAssignId;
    }

    public void setTemplateAssignId(Long templateAssignId) {
        this.templateAssignId = templateAssignId;
    }

    public Integer getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(Integer orderSeq) {
        this.orderSeq = orderSeq;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TemplateConfigVO)) {
            return false;
        }
        TemplateConfigVO that = (TemplateConfigVO) o;
        return Objects.equals(getConfigId(), that.getConfigId())
                        && Objects.equals(getConfigTypeCode(), that.getConfigTypeCode())
                        && Objects.equals(getConfigCode(), that.getConfigCode())
                        && Objects.equals(getConfigValue(), that.getConfigValue())
                        && Objects.equals(getRemark(), that.getRemark())
                        && Objects.equals(getTenantId(), that.getTenantId())
                        && Objects.equals(getTemplateAssignId(), that.getTemplateAssignId())
                        && Objects.equals(getOrderSeq(), that.getOrderSeq())
                        && Objects.equals(getLink(), that.getLink());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getConfigId(), getConfigTypeCode(), getConfigCode(), getConfigValue(), getRemark(),
                        getTenantId(), getTemplateAssignId(), getOrderSeq(), getLink());
    }
}
