package org.hzero.boot.imported.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hzero.core.util.Regexs;
import org.hzero.mybatis.common.query.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hzero.starter.keyencrypt.core.Encrypt;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author shuangfei.zhu@hand-china.com 2018-12-18 16:00:39
 */
@ApiModel("导入模板")
@VersionAudit
@ModifyAudit
@Table(name = "himp_local_template")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocalTemplate extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_TEMPLATE_CODE = "templateCode";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_TEMPLATE_JSON = "templateJson";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("主键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long id;
    @ApiModelProperty(value = "模板编码", required = true)
    @NotBlank
    @Length(max = 30)
    @Where
    @Pattern(regexp = Regexs.CODE_UPPER)
    private String templateCode;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id", required = true)
    @NotNull
    @Where
    private Long tenantId;
    @ApiModelProperty(value = "模板JSON内容", required = true)
    @JsonIgnore
    private String templateJson;

    @Transient
    @NotNull
    private Template template;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 主键
     */
    public Long getId() {
        return id;
    }

    public LocalTemplate setId(Long id) {
        this.id = id;
        return this;
    }

    /**
     * @return 模板编码
     */
    public String getTemplateCode() {
        return templateCode;
    }

    public LocalTemplate setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
        return this;
    }

    /**
     * @return 租户ID, hpfm_tenant.tenant_id
     */
    public Long getTenantId() {
        return tenantId;
    }

    public LocalTemplate setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * @return 模板JSON内容
     */
    public String getTemplateJson() {
        return templateJson;
    }

    public LocalTemplate setTemplateJson(String templateJson) {
        this.templateJson = templateJson;
        return this;
    }

    public Template getTemplate() {
        return template;
    }

    public LocalTemplate setTemplate(Template template) {
        this.template = template;
        return this;
    }
}
