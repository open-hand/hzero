package org.hzero.message.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.util.Regexs;
import org.hzero.mybatis.annotation.DataSecurity;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * webhook配置
 *
 * @author xiaoyu.zhao@hand-china.com 2020-04-26 19:57:46
 */
@ApiModel("webhook配置")
@VersionAudit
@ModifyAudit
@MultiLanguage
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hmsg_webhook_server")
public class WebhookServer extends AuditDomain {

    public static final String FIELD_SERVER_ID = "serverId";
    public static final String FIELD_SERVER_CODE = "serverCode";
    public static final String FIELD_SERVER_NAME = "serverName";
    public static final String FIELD_SERVER_TYPE = "serverType";
    public static final String FIELD_WEBHOOK_ADDRESS = "webhookAddress";
    public static final String FIELD_SECRET = "secret";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_DESCRIPTION = "description";

    public static final String[] UPDATE_FIELDS =
                    {"serverName", "serverType", "webhookAddress", "secret", "description","enabledFlag"};


    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------
    public interface Update{}

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long serverId;
    @ApiModelProperty(value = "webhook编码")
    @NotBlank(groups = Update.class)
    @Length(max = 30)
    @Pattern(regexp = Regexs.CODE)
    private String serverCode;
    @ApiModelProperty(value = "webhook名称")
    @NotBlank(groups = Update.class)
    @Length(max = 60)
    @MultiLanguageField
    private String serverName;
    @ApiModelProperty(value = "webhook类型，HMSG.WEBHOOK_TYPE")
    @LovValue(lovCode = "HMSG.WEBHOOK_TYPE", meaningField = "serverTypeMeaning")
    @NotBlank(groups = Update.class)
    private String serverType;
    @ApiModelProperty(value = "webhook地址")
    @NotBlank
    @Length(max = 480)
    private String webhookAddress;
    @ApiModelProperty(value = "秘钥")
    @Length(max = 240)
    @DataSecurity
    private String secret;
    @ApiModelProperty(value = "启用标识")
    @NotNull(groups = Update.class)
    @Range(max = 1)
    private Integer enabledFlag;
    @ApiModelProperty(value = "租户ID")
    @NotNull(groups = Update.class)
    @MultiLanguageField
    private Long tenantId;
    @ApiModelProperty(value = "webhook描述")
    @Length(max = 240)
    private String description;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    /**
     * webhook类型meaning字段
     */
    @Transient
    private String serverTypeMeaning;

    /**
     * 租户名称
     */
    @Transient
    private String tenantName;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getServerId() {
        return serverId;
    }

    public WebhookServer setServerId(Long serverId) {
        this.serverId = serverId;
        return this;
    }

    /**
     * @return webhook编码
     */
    public String getServerCode() {
        return serverCode;
    }

    public WebhookServer setServerCode(String serverCode) {
        this.serverCode = serverCode;
        return this;
    }

    /**
     * @return webhook名称
     */
    public String getServerName() {
        return serverName;
    }

    public WebhookServer setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    /**
     * @return webhook类型，HMSG.WEBHOOK_TYPE
     */
    public String getServerType() {
        return serverType;
    }

    public WebhookServer setServerType(String serverType) {
        this.serverType = serverType;
        return this;
    }

    /**
     * @return webhook地址
     */
    public String getWebhookAddress() {
        return webhookAddress;
    }

    public WebhookServer setWebhookAddress(String webhookAddress) {
        this.webhookAddress = webhookAddress;
        return this;
    }

    /**
     * @return 秘钥
     */
    public String getSecret() {
        return secret;
    }

    public WebhookServer setSecret(String secret) {
        this.secret = secret;
        return this;
    }

    /**
     * @return 启用标识
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public WebhookServer setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    /**
     * @return 租户ID
     */
    public Long getTenantId() {
        return tenantId;
    }

    public WebhookServer setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * @return webhook描述
     */
    public String getDescription() {
        return description;
    }

    public WebhookServer setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getServerTypeMeaning() {
        return serverTypeMeaning;
    }

    public void setServerTypeMeaning(String serverTypeMeaning) {
        this.serverTypeMeaning = serverTypeMeaning;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
}
