package org.hzero.message.api.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import io.choerodon.mybatis.domain.AuditDomain;

/**
 * description
 *
 * @author zhiying.dong@hand-china.com 2018/09/07 11:24
 */
public class TemplateServerDTO extends AuditDomain {

    @ApiModelProperty("模板ID")
    private Long tempServerId;

    @ApiModelProperty("模版类型，值集:HMSG.TEMPLATE_TYPE")
    private String typeCode;


    @ApiModelProperty("消息模板ID")
    @Encrypt
    private Long templateId;

    @ApiModelProperty(value = "服务ID")
    @Encrypt
    private Long serverId;

    @ApiModelProperty("启用标识")
    private Integer enabledFlag;

    @ApiModelProperty("备注说明")
    private String remark;

    @ApiModelProperty("租户ID")
    private Long tenantId;

    @ApiModelProperty("模板编码")
    private String templateCode;

    @ApiModelProperty("模板名称")
    private String templateName;

    @ApiModelProperty("服务编码")
    private String serverCode;

    @ApiModelProperty("服务名称")
    private String serverName;

    @ApiModelProperty("租户名称")
    private String tenantName;

    private String codeType;

    public Long getTempServerId() {
        return tempServerId;
    }

    public void setTempServerId(Long tempServerId) {
        this.tempServerId = tempServerId;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
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

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    @Override
    public String toString() {
        return "TemplateServerDTO{" +
                "tempServerId=" + tempServerId +
                ", typeCode='" + typeCode + '\'' +
                ", templateId=" + templateId +
                ", serverId=" + serverId +
                ", enabledFlag=" + enabledFlag +
                ", remark='" + remark + '\'' +
                ", tenantId=" + tenantId +
                ", templateCode='" + templateCode + '\'' +
                ", templateName='" + templateName + '\'' +
                ", serverCode='" + serverCode + '\'' +
                ", serverName='" + serverName + '\'' +
                ", tenantName='" + tenantName + '\'' +
                ", codeType='" + codeType + '\'' +
                '}';
    }
}
