package org.hzero.platform.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.util.FileUtils;
import org.hzero.core.util.Regexs;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 通用模板
 *
 * @author xiaoyu.zhao@hand-china.com 2018-08-13 15:37:15
 */
@ApiModel("通用模板")
@VersionAudit
@ModifyAudit
@MultiLanguage
@Table(name = "hpfm_template")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContentTemplate extends AuditDomain {

    public static final String FIELD_TEMPLATE_ID = "templateId";
    public static final String FIELD_TEMPLATE_CODE = "templateCode";
    public static final String FIELD_TEMPLATE_NAME = "templateName";
    public static final String FIELD_TEMPLATE_AVATAR = "templateAvatar";
    public static final String FIELD_TEMPLATE_PATH = "templatePath";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_TEMPLATE_LEVEL_CODE = "templateLevelCode";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("模板ID")
    @Id
    @GeneratedValue
    @Encrypt
    private Long templateId;
    @ApiModelProperty(value = "模板编码")
    @NotBlank
    @Length(max = 30)
    @Pattern(regexp = Regexs.CODE_UPPER)
    private String templateCode;
    @ApiModelProperty(value = "模板名称")
    @NotBlank
    @Length(max = 255)
    @MultiLanguageField
    private String templateName;
    @ApiModelProperty(value = "模板缩略图")
    @Length(max = 480)
    private String templateAvatar;
    @ApiModelProperty(value = "模板路径")
    @NotBlank
    @Length(max = 60)
    private String templatePath;
    @ApiModelProperty(value = "启用标识")
    @NotNull
    @Range(max = 1)
    private Integer enabledFlag;
    @NotNull
    @ApiModelProperty(value = "租户Id")
    @MultiLanguageField
    private Long tenantId;
    @ApiModelProperty(value = "模板层级（全局层/租户层）")
    @LovValue(lovCode = "HPFM.DATA_TENANT_LEVEL", meaningField = "templateLevelCodeMeaning")
    @Length(max = 30)
    private String templateLevelCode;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    @ApiModelProperty(value = "模板缩略图名称")
    private String imageName;
    @Transient
    @JsonIgnore
    private Integer siteQueryFlag;
    @Transient
    @ApiModelProperty("模板层级含义")
    private String templateLevelCodeMeaning;
    @Transient
    private String tenantName;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    public String getTemplateLevelCode() {
        return templateLevelCode;
    }

    public void setTemplateLevelCode(String templateLevelCode) {
        this.templateLevelCode = templateLevelCode;
    }

    public String getTemplateLevelCodeMeaning() {
        return templateLevelCodeMeaning;
    }

    public void setTemplateLevelCodeMeaning(String templateLevelCodeMeaning) {
        this.templateLevelCodeMeaning = templateLevelCodeMeaning;
    }

    public Integer getSiteQueryFlag() {
        return siteQueryFlag;
    }

    public void setSiteQueryFlag(Integer siteQueryFlag) {
        this.siteQueryFlag = siteQueryFlag;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return 模板ID
     */
    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    /**
     * @return 模板编码
     */
    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    /**
     * @return 模板名称
     */
    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /**
     * @return 模板缩略图
     */
    public String getTemplateAvatar() {
        return templateAvatar;
    }

    public void setTemplateAvatar(String templateAvatar) {
        this.templateAvatar = templateAvatar;
    }

    /**
     * @return 模板路径
     */
    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    /**
     * @return 启用标识
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getImageName() {
        if(templateAvatar != null){
            return FileUtils.getFileName(templateAvatar);
        }
        return null;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
}
