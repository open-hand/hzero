package org.hzero.iam.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 租户配置扩展表
 *
 * @author xiaoyu.zhao@hand-china.com 2020-04-21 20:46:05
 */
@ApiModel("租户配置扩展表")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hpfm_tenant_config")
public class TenantConfig extends AuditDomain {
    public static final String ENCRYPT_KEY = "hpfm_tenant_config";
    public static final String FIELD_TENANT_CONFIG_ID = "tenantConfigId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_CONFIG_KEY = "configKey";
    public static final String FIELD_CONFIG_VALUE = "configValue";
    public static final String[] SELECT_FIELDS = {"tenantConfigId","tenantId","configKey","configValue"};

    public TenantConfig() {
    }

    public TenantConfig(String configKey, String configValue) {
        this.configKey = configKey;
        this.configValue = configValue;
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long tenantConfigId;
    @ApiModelProperty(value = "租户Id,hpfm_tenant.tenant_id")
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "配置Key")
    @NotBlank
    private String configKey;
    @ApiModelProperty(value = "配置值")
    @NotBlank
    private String configValue;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getTenantConfigId() {
        return tenantConfigId;
    }

    public TenantConfig setTenantConfigId(Long tenantConfigId) {
        this.tenantConfigId = tenantConfigId;
        return this;
    }

    /**
     * @return 租户Id,hpfm_tenant.tenant_id
     */
    public Long getTenantId() {
        return tenantId;
    }

    public TenantConfig setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * @return 配置Key
     */
    public String getConfigKey() {
        return configKey;
    }

    public TenantConfig setConfigKey(String configKey) {
        this.configKey = configKey;
        return this;
    }

    /**
     * @return 配置值
     */
    public String getConfigValue() {
        return configValue;
    }

    public TenantConfig setConfigValue(String configValue) {
        this.configValue = configValue;
        return this;
    }

    @Override
    public String toString() {
        return "TenantConfig{" +
                "tenantConfigId=" + tenantConfigId +
                ", tenantId=" + tenantId +
                ", configKey='" + configKey + '\'' +
                ", configValue='" + configValue + '\'' +
                '}';
    }
}
