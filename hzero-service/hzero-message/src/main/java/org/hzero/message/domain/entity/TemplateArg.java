package org.hzero.message.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 消息模板参数
 *
 * @author fanghan.liu@hand-china.com 2019-10-08 16:49:41
 */
@ApiModel("消息模板参数")
@MultiLanguage
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hmsg_template_arg")
public class TemplateArg extends AuditDomain {

    public static final String FIELD_ARG_ID = "argId";
    public static final String FIELD_TEMPLATE_ID = "templateId";
    public static final String FIELD_ARG_NAME = "argName";
    public static final String FIELD_DESCRIPTION = "description";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("主键，自增")
    @Id
    @GeneratedValue
    @Encrypt
    private Long argId;
    @ApiModelProperty(value = "消息模板ID,hmsg_message_template.template_id", required = true)
    @NotNull
    @Encrypt
    private Long templateId;
    @ApiModelProperty(value = "参数名称", required = true)
    @NotBlank
    @Length(max = 30)
    private String argName;
    @ApiModelProperty(value = "参数描述")
    @MultiLanguageField
    @Length(max = 240)
    private String description;
    @ApiModelProperty(value = "租户ID")
    @MultiLanguageField
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 参数ID
     */
    public Long getArgId() {
        return argId;
    }

    public void setArgId(Long argId) {
        this.argId = argId;
    }

    /**
     * @return 消息模板ID, hmsg_message_template.template_id
     */
    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    /**
     * @return 参数名称
     */
    public String getArgName() {
        return argName;
    }

    public void setArgName(String argName) {
        this.argName = argName;
    }

    /**
     * @return 参数描述
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public TemplateArg setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }
}
