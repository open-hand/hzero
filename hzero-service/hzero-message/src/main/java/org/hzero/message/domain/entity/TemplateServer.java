package org.hzero.message.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.util.Regexs;
import org.hzero.message.domain.repository.TemplateServerRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 消息模板账户
 *
 * @author zhiying.dong@hand-china.com 2018-09-07 11:09:29
 */
@ApiModel("消息模板账户")
@VersionAudit
@ModifyAudit
@MultiLanguage
@Table(name = "hmsg_template_server")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemplateServer extends AuditDomain {

    public static final String FIELD_TEMP_SERVER_ID = "tempServerId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_MESSAGE_CODE = "messageCode";
    public static final String FIELD_MESSAGE_NAME = "messageName";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_CATEGORY_CODE = "categoryCode";
    public static final String FIELD_SUBCATEGORY_CODE = "subcategoryCode";
    public static final String FIELD_RECEIVE_CONFIG_FLAG = "receiveConfigFlag";
    public static final String FIELD_DESCRIPTION = "description";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    public void validRepeat(TemplateServerRepository templateServerRepository) {
        int cnt = templateServerRepository.selectCountByCondition(Condition.builder(TemplateServer.class).andWhere(Sqls.custom()
                .andEqualTo(FIELD_MESSAGE_CODE, messageCode)
                .andEqualTo(FIELD_TENANT_ID, tenantId)
                .andNotEqualTo(FIELD_TEMP_SERVER_ID, tempServerId, true))
                .build());
        Assert.isTrue(cnt == 0, HmsgConstant.ErrorCode.REPEAT_TEMPLATE_ASSOCIATION);
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long tempServerId;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id", required = true)
    @NotNull
    @MultiLanguageField
    private Long tenantId;
    @ApiModelProperty(value = "消息代码", required = true)
    @NotBlank
    @Length(max = 30)
    @Pattern(regexp = Regexs.CODE_UPPER)
    private String messageCode;
    @ApiModelProperty(value = "消息名称", required = true)
    @NotBlank
    @Length(max = 120)
    @MultiLanguageField
    private String messageName;
    @ApiModelProperty(value = "启用标识", required = true)
    @NotNull
    @Range(max = 1, min = 0)
    private Integer enabledFlag;
    @ApiModelProperty(value = "分类代码,值集:HMSG.TEMP_SERVER.CATEGORY")
    @Length(max = 30)
    @LovValue("HMSG.TEMP_SERVER.CATEGORY")
    @Pattern(regexp = Regexs.CODE_UPPER)
    private String categoryCode;
    @ApiModelProperty(value = "子类别代码,值集:HMSG.TEMP_SERVER.SUBCATEGORY")
    @Length(max = 30)
    @LovValue("HMSG.TEMP_SERVER.SUBCATEGORY")
    @Pattern(regexp = Regexs.CODE_UPPER)
    private String subcategoryCode;
    @ApiModelProperty(value = "自定义配置接收标识")
    @Range(max = 1, min = 0)
    private Integer receiveConfigFlag;
    @ApiModelProperty(value = "描述")
    @Length(max = 480)
    private String description;
    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    @ApiModelProperty("租户名称")
    private String tenantName;
    @Valid
    @Transient
    private List<TemplateServerLine> serverList;
    @Transient
    private String categoryMeaning;
    @Transient
    private String subcategoryMeaning;
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    public Long getTempServerId() {
        return tempServerId;
    }

    public TemplateServer setTempServerId(Long tempServerId) {
        this.tempServerId = tempServerId;

        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public TemplateServer setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public TemplateServer setMessageCode(String messageCode) {
        this.messageCode = messageCode;
        return this;
    }

    public String getMessageName() {
        return messageName;
    }

    public TemplateServer setMessageName(String messageName) {
        this.messageName = messageName;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public TemplateServer setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public TemplateServer setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
        return this;
    }

    public String getSubcategoryCode() {
        return subcategoryCode;
    }

    public TemplateServer setSubcategoryCode(String subcategoryCode) {
        this.subcategoryCode = subcategoryCode;
        return this;
    }

    public Integer getReceiveConfigFlag() {
        return receiveConfigFlag;
    }

    public TemplateServer setReceiveConfigFlag(Integer receiveConfigFlag) {
        this.receiveConfigFlag = receiveConfigFlag;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public TemplateServer setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public List<TemplateServerLine> getServerList() {
        return serverList;
    }

    public TemplateServer setServerList(List<TemplateServerLine> serverList) {
        this.serverList = serverList;
        return this;
    }

    public String getCategoryMeaning() {
        return categoryMeaning;
    }

    public TemplateServer setCategoryMeaning(String categoryMeaning) {
        this.categoryMeaning = categoryMeaning;
        return this;
    }

    public String getSubcategoryMeaning() {
        return subcategoryMeaning;
    }

    public TemplateServer setSubcategoryMeaning(String subcategoryMeaning) {
        this.subcategoryMeaning = subcategoryMeaning;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public TemplateServer setDescription(String description) {
        this.description = description;
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
