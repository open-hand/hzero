package org.hzero.message.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.jackson.annotation.Trim;
import org.hzero.core.util.Regexs;
import org.hzero.message.domain.repository.MessageTemplateRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.Objects;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 消息模板
 *
 * @author qingsheng.chen@hand-china 2018-07-25 16:46:08
 */
@VersionAudit
@ModifyAudit
@Table(name = "hmsg_message_template")
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("消息模板实体")
@SuppressWarnings("all")
public class MessageTemplate extends AuditDomain {

    public static final String FIELD_TEMPLATE_ID = "templateId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_TEMPLATE_CODE = "templateCode";
    public static final String FIELD_TEMPLATE_NAME = "templateName";
    public static final String FIELD_TEMPLATE_TITLE = "templateTitle";
    public static final String FIELD_TEMPLATE_CONTENT = "templateContent";
    public static final String FIELD_TEMPLATE_TYPE_CODE = "templateTypeCode";
    public static final String FIELD_EXTERNAL_CODE = "externalCode";
    public static final String FIELD_SERVER_TYPE_CODE = "serverTypeCode";
    public static final String FIELD_SQL_VALUE = "sqlValue";
    public static final String FIELD_LANG = "lang";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_MESSAGE_CATEGORY_CODE = "messageCategoryCode";
    public static final String FIELD_MESSAGE_SUBCATEGORY_CODE = "messageSubcategoryCode";
    public static final String FIELD_EDITOR_TYPE = "editorType";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 验证消息模板是否存在
     *
     * @param repository 仓库
     */
    public MessageTemplate validateMessageTemplateExists(MessageTemplateRepository repository) {
        MessageTemplate messageTemplate = repository.selectOne(new MessageTemplate().setTemplateId(templateId).setTenantId(tenantId));
        if (messageTemplate == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        if (!Objects.equals(messageTemplate.getLang(), lang)) {
            validateTemplateCodeRepeat(repository);
        }
        return messageTemplate;
    }

    /**
     * 校验消息模板 Code 是否存在
     * 消息校验：租户 + 模板编码 + 语言
     *
     * @param repository 仓库
     */
    public void validateTemplateCodeRepeat(MessageTemplateRepository repository) {
        if (!CollectionUtils.isEmpty(repository.select(new MessageTemplate().setTenantId(tenantId).setTemplateCode(templateCode).setLang(lang)))) {
            throw new CommonException(HmsgConstant.ErrorCode.TEMPLATE_REPEAT);
        }
    }

    /**
     * 校验租户id是否相等
     *
     * @param repository MessageTemplateRepository
     */
    public void validateTenant(MessageTemplateRepository repository) {
        MessageTemplate messageTemplate = repository.selectByPrimaryKey(templateId);
        if (!Objects.equals(messageTemplate.getTenantId(), tenantId)) {
            throw new CommonException(HmsgConstant.ErrorCode.TENANT_NO_MATCH);
        }
    }

    /**
     * 校验编码是否存在预定义的数据，是 - 允许删除自定义，否 - 不允许删除
     */
    public void validateCodeExistPredefined(MessageTemplateRepository repository) {
        MessageTemplate messageTemplate = repository.selectOne(new MessageTemplate()
                .setTenantId(BaseConstants.DEFAULT_TENANT_ID).setTemplateCode(templateCode));
        if (messageTemplate == null || templateId.equals(messageTemplate.getTemplateId())) {
            throw new CommonException(HmsgConstant.ErrorCode.DELETE_CONFIG_FAILED);
        }
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @Id
    @GeneratedValue
    @ApiModelProperty("消息模板ID")
    @Encrypt
    private Long templateId;
    @NotNull
    private Long tenantId;
    @NotBlank
    @Size(max = 60)
    @Trim
    @ApiModelProperty("模板编码")
    @Pattern(regexp = Regexs.CODE_UPPER)
    private String templateCode;
    @NotBlank
    @Size(max = 240)
    @ApiModelProperty("模板名称")
    private String templateName;
    @NotBlank
    @Size(max = 480)
    @ApiModelProperty("模板标题")
    private String templateTitle;
    @NotBlank
    @ApiModelProperty("模板内容")
    private String templateContent;
    @Size(max = 30)
    @LovValue(lovCode = "HMSG.MESSAGE_CATEGORY")
    @ApiModelProperty(value = "消息类型，值集:HMSG.MESSAGE_CATEGORY")
    private String messageCategoryCode;
    @Size(max = 30)
    @LovValue(lovCode = "HMSG.MESSAGE_SUBCATEGORY")
    @ApiModelProperty(value = "消息子类型，值集:HMSG.MESSAGE_SUBCATEGORY")
    private String messageSubcategoryCode;
    @Size(max = 60)
    @ApiModelProperty("短信非空，外部代码")
    private String externalCode;
    @Size(max = 2000)
    @ApiModelProperty(value = "取值SQL")
    private String sqlValue;
    @NotBlank
    @Size(max = 30)
    @ApiModelProperty(value = "语言编码")
    private String lang;
    @NotNull
    @Range(min = 0, max = 1)
    @ApiModelProperty(value = "启用标识", allowableValues = "range[0, 1]")
    private Integer enabledFlag;
    @ApiModelProperty(value = "编辑器类型，值集HMSG.TEMPLATE_EDITOR_TYPE")
    @NotNull
    private String editorType;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String tenantName;
    @Transient
    private String messageCategoryMeaning;
    @Transient
    private String messageSubcategoryMeaning;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getTemplateId() {
        return templateId;
    }

    public MessageTemplate setTemplateId(Long templateId) {
        this.templateId = templateId;
        return this;
    }

    /**
     * @return 租户ID
     */
    public Long getTenantId() {
        return tenantId;
    }

    public MessageTemplate setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * @return 模板代码
     */
    public String getTemplateCode() {
        return templateCode;
    }

    public MessageTemplate setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
        return this;
    }

    /**
     * @return 模板名称
     */
    public String getTemplateName() {
        return templateName;
    }

    public MessageTemplate setTemplateName(String templateName) {
        this.templateName = templateName;
        return this;
    }

    /**
     * @return 模板标题
     */
    public String getTemplateTitle() {
        return templateTitle;
    }

    public MessageTemplate setTemplateTitle(String templateTitle) {
        this.templateTitle = templateTitle;
        return this;
    }

    /**
     * @return 模板内容
     */
    public String getTemplateContent() {
        return templateContent;
    }

    public MessageTemplate setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
        return this;
    }

    /**
     * @return 消息类型，值集:HMSG.MESSAGE_CATEGORY
     */
    public String getMessageCategoryCode() {
        return messageCategoryCode;
    }

    public MessageTemplate setMessageCategoryCode(String messageCategoryCode) {
        this.messageCategoryCode = messageCategoryCode;
        return this;
    }

    /**
     * @return 消息子类型，值集:HMSG.MESSAGE_SUBCATEGORY
     */
    public String getMessageSubcategoryCode() {
        return messageSubcategoryCode;
    }

    public MessageTemplate setMessageSubcategoryCode(String messageSubcategoryCode) {
        this.messageSubcategoryCode = messageSubcategoryCode;
        return this;
    }

    /**
     * @return 短信非空，外部代码
     */
    public String getExternalCode() {
        return externalCode;
    }

    public MessageTemplate setExternalCode(String externalCode) {
        this.externalCode = externalCode;
        return this;
    }

    /**
     * @return 取值SQL
     */
    public String getSqlValue() {
        return sqlValue;
    }

    public MessageTemplate setSqlValue(String sqlValue) {
        this.sqlValue = sqlValue;
        return this;
    }

    /**
     * @return 语言
     */
    public String getLang() {
        return lang;
    }

    public MessageTemplate setLang(String lang) {
        this.lang = lang;
        return this;
    }

    /**
     * @return 启用标识
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public MessageTemplate setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    /**
     * @return 租户名称
     */
    public String getTenantName() {
        return tenantName;
    }

    public MessageTemplate setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public String getMessageCategoryMeaning() {
        return messageCategoryMeaning;
    }

    public MessageTemplate setMessageCategoryMeaning(String messageCategoryMeaning) {
        this.messageCategoryMeaning = messageCategoryMeaning;
        return this;
    }

    public String getMessageSubcategoryMeaning() {
        return messageSubcategoryMeaning;
    }

    public MessageTemplate setMessageSubcategoryMeaning(String messageSubcategoryMeaning) {
        this.messageSubcategoryMeaning = messageSubcategoryMeaning;
        return this;
    }

    public String getEditorType() {
        return editorType;
    }

    public MessageTemplate setEditorType(String editorType) {
        this.editorType = editorType;
        return this;
    }

    @JsonIgnore
    @Override
    public Date getCreationDate() {
        return super.getCreationDate();
    }

    @JsonIgnore
    @Override
    public Long getCreatedBy() {
        return super.getCreatedBy();
    }

    @JsonIgnore
    @Override
    public Date getLastUpdateDate() {
        return super.getLastUpdateDate();
    }

    @JsonIgnore
    @Override
    public Long getLastUpdatedBy() {
        return super.getLastUpdatedBy();
    }
}
