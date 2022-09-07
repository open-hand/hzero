package org.hzero.message.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 消息发送配置webhook
 *
 * @author xiaoyu.zhao@hand-china.com 2020-05-15 10:29:57
 */
@ApiModel("消息发送配置webhook")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hmsg_template_server_wh")
public class TemplateServerWh extends AuditDomain {

    public static final String FIELD_TEMP_SERVER_WH_ID = "tempServerWhId";
    public static final String FIELD_TEMP_SERVER_ID = "tempServerId";
    public static final String FIELD_TEMP_SERVER_LINE_ID = "tempServerLineId";
    public static final String FIELD_SERVER_CODE = "serverCode";
    public static final String FIELD_EXT_INFO = "extInfo";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long tempServerWhId;
    @ApiModelProperty(value = "消息发送配置Id,hpfm_template_server.temp_server_id")
    @NotNull
    @Encrypt
    private Long tempServerId;
    @ApiModelProperty(value = "消息发送配置行Id，hpfm_template_server_line.temp_server_line_id")
    @NotNull
    @Encrypt
    private Long tempServerLineId;
    @ApiModelProperty(value = "关联账户配置编码")
    @NotBlank
    private String serverCode;
    @ApiModelProperty(value = "扩展字段")
    private String extInfo;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id")
    @NotNull
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String serverName;
    @Transient
    @LovValue(lovCode = "HMSG.WEBHOOK_TYPE", meaningField = "serverTypeMeaning")
    private String serverType;
    @Transient
    private String serverTypeMeaning;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getTempServerWhId() {
        return tempServerWhId;
    }

    public void setTempServerWhId(Long tempServerWhId) {
        this.tempServerWhId = tempServerWhId;
    }

    /**
     * @return 消息发送配置Id, hpfm_template_server.temp_server_id
     */
    public Long getTempServerId() {
        return tempServerId;
    }

    public void setTempServerId(Long tempServerId) {
        this.tempServerId = tempServerId;
    }

    /**
     * @return 消息发送配置行Id，hpfm_template_server_line.temp_server_line_id
     */
    public Long getTempServerLineId() {
        return tempServerLineId;
    }

    public void setTempServerLineId(Long tempServerLineId) {
        this.tempServerLineId = tempServerLineId;
    }

    /**
     * @return 关联账户配置编码
     */
    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    /**
     * @return 扩展字段
     */
    public String getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }

    /**
     * @return 租户ID, hpfm_tenant.tenant_id
     */
    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public String getServerTypeMeaning() {
        return serverTypeMeaning;
    }

    public void setServerTypeMeaning(String serverTypeMeaning) {
        this.serverTypeMeaning = serverTypeMeaning;
    }
}
