package org.hzero.file.domain.entity;

import java.util.List;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.validator.constraints.Length;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.util.Regexs;
import org.hzero.file.infra.constant.HfleConstant;
import org.hzero.mybatis.annotation.Unique;
import org.hzero.starter.keyencrypt.core.Encrypt;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 服务器上传配置
 *
 * @author shuangfei.zhu@hand-china.com 2019-07-03 20:38:55
 */
@ApiModel("服务器上传配置")
@VersionAudit
@ModifyAudit
@Table(name = "hfle_server_config")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerConfig extends AuditDomain {

    public static final String FIELD_CONFIG_ID = "configId";
    public static final String FIELD_CONFIG_CODE = "configCode";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_SOURCE_TYPE = "sourceType";
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
    private Long configId;
    @ApiModelProperty(value = "配置编码", required = true)
    @NotBlank
    @Length(max = 30)
    @Unique
    @Pattern(regexp = Regexs.CODE_UPPER)
    private String configCode;
    @ApiModelProperty(value = "描述")
    @Length(max = 480)
    private String description;
    @ApiModelProperty(value = "来源类型, 值集：HFLE.SERVER.SOURCE_TYPE", required = true)
    @NotBlank
    @LovValue(HfleConstant.SourceType.CODE)
    @Length(max = 30)
    private String sourceType;
    @ApiModelProperty(value = "根目录", required = true)
    @NotBlank
    @Length(max = 240)
    private String rootDir;
    @ApiModelProperty(value = "启用标识", required = true)
    @NotNull
    private Integer enabledFlag;
    @ApiModelProperty(value = "租户ID，hpfm_tenant.tenant_id", required = true)
    @NotNull
    @Unique
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String sourceTypeMeaning;
    @Transient
    private String tenantName;
    @Transient
    private List<ServerConfigLine> serverConfigLineList;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    public Long getConfigId() {
        return configId;
    }

    public ServerConfig setConfigId(Long configId) {
        this.configId = configId;
        return this;
    }

    public String getConfigCode() {
        return configCode;
    }

    public ServerConfig setConfigCode(String configCode) {
        this.configCode = configCode;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ServerConfig setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getSourceType() {
        return sourceType;
    }

    public ServerConfig setSourceType(String sourceType) {
        this.sourceType = sourceType;
        return this;
    }

    public String getRootDir() {
        return rootDir;
    }

    public ServerConfig setRootDir(String rootDir) {
        this.rootDir = rootDir;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public ServerConfig setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public ServerConfig setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getSourceTypeMeaning() {
        return sourceTypeMeaning;
    }

    public ServerConfig setSourceTypeMeaning(String sourceTypeMeaning) {
        this.sourceTypeMeaning = sourceTypeMeaning;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public ServerConfig setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public List<ServerConfigLine> getServerConfigLineList() {
        return serverConfigLineList;
    }

    public ServerConfig setServerConfigLineList(List<ServerConfigLine> serverConfigLineList) {
        this.serverConfigLineList = serverConfigLineList;
        return this;
    }
}
