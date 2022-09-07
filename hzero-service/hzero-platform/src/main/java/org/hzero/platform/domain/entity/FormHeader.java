package org.hzero.platform.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.util.Regexs;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 表单配置头
 *
 * @author xiaoyu.zhao@hand-china.com 2019-07-11 17:50:08
 */
@ApiModel("表单配置头")
@VersionAudit
@ModifyAudit
@MultiLanguage
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hpfm_form_header")
public class FormHeader extends AuditDomain {

    public static final String FIELD_FORM_HEADER_ID = "formHeaderId";
    public static final String FIELD_FORM_CODE = "formCode";
    public static final String FIELD_FORM_NAME = "formName";
    public static final String FIELD_FORM_GROUP_CODE = "formGroupCode";
    public static final String FIELD_FORM_DESCRIPTION = "formDescription";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_TENANT_ID = "tenantId";


    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("")
    @Id
    @GeneratedValue
    @Encrypt
    private Long formHeaderId;
    @ApiModelProperty(value = "表单编码")
    @Pattern(regexp = Regexs.CODE)
    @NotBlank
    @Length(max = 30)
    private String formCode;
    @ApiModelProperty(value = "表单名称")
    @NotBlank
    @Length(max = 255)
    @MultiLanguageField
    private String formName;
    @ApiModelProperty(value = "表单归类，HPFM.FORM_GROUP")
    @NotBlank
    @LovValue(lovCode = "HPFM.FORM_GROUP", meaningField = "formGroupMeaning")
    @Length(max = 30)
    private String formGroupCode;
    @ApiModelProperty(value = "表单描述")
    @Length(max = 480)
    @MultiLanguageField
    private String formDescription;
    @ApiModelProperty(value = "是否启用 1:启用 0：不启用")
    @NotNull
    @Range(max = 1)
    private Integer enabledFlag;
    @ApiModelProperty(value = "租户ID")
    @NotNull
    @MultiLanguageField
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------
    @Transient
    private String tenantName;
    @Transient
    private String formGroupMeaning;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    public String getFormGroupMeaning() {
        return formGroupMeaning;
    }

    public void setFormGroupMeaning(String formGroupMeaning) {
        this.formGroupMeaning = formGroupMeaning;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    /**
     * @return
     */
    public Long getFormHeaderId() {
        return formHeaderId;
    }

    public FormHeader setFormHeaderId(Long formHeaderId) {
        this.formHeaderId = formHeaderId;
        return this;
    }

    /**
     * @return 表单编码
     */
    public String getFormCode() {
        return formCode;
    }

    public FormHeader setFormCode(String formCode) {
        this.formCode = formCode;
        return this;
    }

    /**
     * @return 表单名称
     */
    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    /**
     * @return 表单归类，HPFM.FORM_GROUP
     */
    public String getFormGroupCode() {
        return formGroupCode;
    }

    public void setFormGroupCode(String formGroupCode) {
        this.formGroupCode = formGroupCode;
    }

    /**
     * @return 表单描述
     */
    public String getFormDescription() {
        return formDescription;
    }

    public void setFormDescription(String formDescription) {
        this.formDescription = formDescription;
    }

    /**
     * @return 是否启用 1:启用 0：不启用
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public FormHeader setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    /**
     * @return 租户ID
     */
    public Long getTenantId() {
        return tenantId;
    }

    public FormHeader setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

}
