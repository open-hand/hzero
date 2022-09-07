package org.hzero.boot.message.entity;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Objects;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import org.hzero.core.util.Regexs;

/**
 * <p>
 * 消息模板账户行
 * </p>
 *
 * @author qingsheng.chen 2018/9/30 星期日 12:39
 */
@ApiModel("消息模板账户行")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemplateServerLine {


    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long tempServerLineId;
    @ApiModelProperty(value = "消息模板账户,hmsg_template_server.temp_server_id", required = true)
    private Long tempServerId;
    @ApiModelProperty(value = "模版类型，值集:HMSG.MESSAGE_TYPE", required = true)
    @NotBlank
    @Size(max = 30)
    private String typeCode;
    @NotBlank
    @Size(max = 60)
    @ApiModelProperty("模板编码")
    @Pattern(regexp = Regexs.CODE_UPPER)
    private String templateCode;
    @ApiModelProperty(value = "服务ID,hmsg_email_server或hmsg_sms_server", required = true)
    private Long serverId;
    @ApiModelProperty(value = "备注说明")
    private String remark;
    @ApiModelProperty("消息类型")
    private String typeMeaning;
    @ApiModelProperty("服务编码")
    private String serverCode;
    @ApiModelProperty("服务名称")
    private String serverName;

    public Long getTempServerLineId() {
        return tempServerLineId;
    }

    public TemplateServerLine setTempServerLineId(Long tempServerLineId) {
        this.tempServerLineId = tempServerLineId;
        return this;
    }

    public Long getTempServerId() {
        return tempServerId;
    }

    public TemplateServerLine setTempServerId(Long tempServerId) {
        this.tempServerId = tempServerId;
        return this;
    }

    /**
     * @return 模版类型，值集:HMSG.TEMPLATE_TYPE
     */
    public String getTypeCode() {
        return typeCode;
    }

    public TemplateServerLine setTypeCode(String typeCode) {
        this.typeCode = typeCode;
        return this;
    }

    /**
     * @return 消息模板ID，hmsg_message_template.template_id
     */
    public String getTemplateCode() {
        return templateCode;
    }

    public TemplateServerLine setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
        return this;
    }

    /**
     * @return 服务ID, hmsg_email_server或hmsg_sms_server
     */
    public Long getServerId() {
        return serverId;
    }

    public TemplateServerLine setServerId(Long serverId) {
        this.serverId = serverId;
        return this;
    }

    /**
     * @return 备注说明
     */
    public String getRemark() {
        return remark;
    }

    public TemplateServerLine setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public String getTypeMeaning() {
        return typeMeaning;
    }

    public TemplateServerLine setTypeMeaning(String typeMeaning) {
        this.typeMeaning = typeMeaning;
        return this;
    }

    public String getServerCode() {
        return serverCode;
    }

    public TemplateServerLine setServerCode(String serverCode) {
        this.serverCode = serverCode;
        return this;
    }

    public String getServerName() {
        return serverName;
    }

    public TemplateServerLine setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TemplateServerLine)) {
            return false;
        }
        TemplateServerLine that = (TemplateServerLine) o;
        return Objects.equal(tempServerLineId, that.tempServerLineId) &&
                Objects.equal(tempServerId, that.tempServerId) &&
                Objects.equal(typeCode, that.typeCode) &&
                Objects.equal(templateCode, that.templateCode) &&
                Objects.equal(serverId, that.serverId) &&
                Objects.equal(remark, that.remark) &&
                Objects.equal(typeMeaning, that.typeMeaning) &&
                Objects.equal(serverCode, that.serverCode) &&
                Objects.equal(serverName, that.serverName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tempServerLineId, tempServerId, typeCode, templateCode, serverId, remark, typeMeaning, serverCode, serverName);
    }

    @Override
    public String toString() {
        return "TemplateServerLine{" +
                "tempServerLineId=" + tempServerLineId +
                ", tempServerId=" + tempServerId +
                ", typeCode='" + typeCode + '\'' +
                ", templateCode='" + templateCode + '\'' +
                ", serverId=" + serverId +
                ", remark='" + remark + '\'' +
                ", typeMeaning='" + typeMeaning + '\'' +
                ", serverCode='" + serverCode + '\'' +
                ", serverName='" + serverName + '\'' +
                '}';
    }
}
