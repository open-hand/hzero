package org.hzero.file.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hzero.mybatis.annotation.Unique;
import org.hzero.starter.keyencrypt.core.Encrypt;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 服务器上传配置明细
 *
 * @author shuangfei.zhu@hand-china.com 2019-07-04 10:10:21
 */
@ApiModel("服务器上传配置明细")
@VersionAudit
@ModifyAudit
@Table(name = "hfle_server_config_line")
public class ServerConfigLine extends AuditDomain {

    public static final String FIELD_CONFIG_LINE_ID = "configLineId";
    public static final String FIELD_CONFIG_ID = "configId";
    public static final String FIELD_SOURCE_ID = "sourceId";
    public static final String FIELD_ROOT_DIR = "rootDir";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
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
    private Long configLineId;
    @ApiModelProperty(value = "服务器上传配置ID", required = true)
    @NotNull
    @Unique
    @Encrypt
    private Long configId;
    @ApiModelProperty(value = "来源ID，根据来源类型判断关联的表", required = true)
    @NotNull
    @Unique
    @Encrypt
    private Long sourceId;
    @ApiModelProperty(value = "根目录")
    @Length(max = 240)
    private String rootDir;
    @ApiModelProperty(value = "启用标识", required = true)
    @NotNull
    private Integer enabledFlag;
    @ApiModelProperty(value = "租户ID，hpfm_tenant.tenant_id", required = true)
    @NotNull
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String sourceCode;
    @Transient
    private String sourceName;

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getConfigLineId() {
        return configLineId;
    }

    public ServerConfigLine setConfigLineId(Long configLineId) {
        this.configLineId = configLineId;
        return this;
    }

    public Long getConfigId() {
        return configId;
    }

    public ServerConfigLine setConfigId(Long configId) {
        this.configId = configId;
        return this;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public ServerConfigLine setSourceId(Long sourceId) {
        this.sourceId = sourceId;
        return this;
    }

    public String getRootDir() {
        return rootDir;
    }

    public ServerConfigLine setRootDir(String rootDir) {
        this.rootDir = rootDir;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public ServerConfigLine setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public ServerConfigLine setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public ServerConfigLine setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
        return this;
    }

    public String getSourceName() {
        return sourceName;
    }

    public ServerConfigLine setSourceName(String sourceName) {
        this.sourceName = sourceName;
        return this;
    }
}
