package org.hzero.platform.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.util.FileUtils;
import org.hzero.core.util.Regexs;
import org.hzero.mybatis.common.query.Where;
import org.hzero.platform.domain.vo.TemplateConfigVO;
import org.hzero.platform.infra.constant.Constants;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.BeanUtils;

/**
 * 模板配置
 *
 * @author xiaoyu.zhao@hand-china.com 2019-06-28 10:42:49
 */
@ApiModel("模板配置")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hpfm_template_config")
public class TemplateConfig extends AuditDomain {

    public static final String FIELD_CONFIG_ID = "configId";
    public static final String FIELD_CONFIG_TYPE_CODE = "configTypeCode";
    public static final String FIELD_CONFIG_CODE = "configCode";
    public static final String FIELD_CONFIG_VALUE = "configValue";
    public static final String FIELD_REMARK = "remark";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_TEMPLATE_ASSIGN_ID = "templateAssignId";
    public static final String FIELD_ORDER_SEQ = "orderSeq";
    public static final String FIELD_LINK = "link";


    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 转换对象
     */
    public TemplateConfigVO convertConfigToVO() {
        // 获取缓存Key，防止数据删除后无法获取redisKey所需参数
        TemplateConfigVO templateConfigVO = new TemplateConfigVO();
        BeanUtils.copyProperties(this, templateConfigVO);
        return templateConfigVO;
    }


    //---------------------构造器---------------------------------

    public TemplateConfig() {
    }

    public TemplateConfig(@NotNull Long tenantId, @NotNull Long templateAssignId) {
        this.tenantId = tenantId;
        this.templateAssignId = templateAssignId;
    }
    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("")
    @Id
    @GeneratedValue
    @Encrypt
    private Long configId;
    @ApiModelProperty(value = "配置类型编码 HPFM.CONFIG_TYPE_CODE")
    @NotBlank
    @LovValue(lovCode = "HPFM.CONFIG_TYPE_CODE", meaningField = "configTypeCodeMeaning")
    @Where
    @Length(max = 30)
    private String configTypeCode;
    @ApiModelProperty(value = "配置编码")
    @NotBlank
    @Pattern(regexp = Regexs.CODE_UPPER)
    @Where
    @Length(max = 60)
    private String configCode;
    @ApiModelProperty(value = "配置值")
    private String configValue;
    @ApiModelProperty(value = "备注说明")
    @Length(max = 1200)
    private String remark;
    @ApiModelProperty(value = "租户ID,hpfm_tenant")
    @NotNull
    @Where
    private Long tenantId;
    @ApiModelProperty(value = "模板配置ID，hpfm_template_assign.template_assign_id")
    @NotNull
    @Where
    @Encrypt
    private Long templateAssignId;
    @ApiModelProperty(value = "排序标识，用于为配置排序")
    @NotNull
    private Integer orderSeq;
    @ApiModelProperty(value = "链接")
    @Length(max = 480)
    private String link;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    @ApiModelProperty(value = "租户名称")
    private String tenantName;
    @Transient
    private String configTypeCodeMeaning;
    @Transient
    private String fileName;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    public Long getTemplateAssignId() {
        return templateAssignId;
    }

    public TemplateConfig setTemplateAssignId(Long templateAssignId) {
        this.templateAssignId = templateAssignId;
        return this;
    }

    public String getConfigTypeCodeMeaning() {
        return configTypeCodeMeaning;
    }

    public void setConfigTypeCodeMeaning(String configTypeCodeMeaning) {
        this.configTypeCodeMeaning = configTypeCodeMeaning;
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
    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    /**
     * @return 配置类型编码 HPFM.CONFIG_TYPE_CODE
     */
    public String getConfigTypeCode() {
        return configTypeCode;
    }

    public TemplateConfig setConfigTypeCode(String configTypeCode) {
        this.configTypeCode = configTypeCode;
        return this;
    }

    /**
     * @return 配置编码
     */
    public String getConfigCode() {
        return configCode;
    }

    public TemplateConfig setConfigCode(String configCode) {
        this.configCode = configCode;
        return this;
    }

    /**
     * @return 配置值
     */
    public String getConfigValue() {
        return configValue;
    }

    public TemplateConfig setConfigValue(String configValue) {
        this.configValue = configValue;
        return this;
    }

    /**
     * @return 备注说明
     */
    public String getRemark() {
        return remark;
    }

    public TemplateConfig setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    /**
     * @return 租户ID,hpfm_tenant
     */
    public Long getTenantId() {
        return tenantId;
    }

    public TemplateConfig setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Integer getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(Integer orderSeq) {
        this.orderSeq = orderSeq;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getFileName() {
        if (configValue != null && Constants.HPFM_FILE_CONFIG_TYPE_CODE.equals(configTypeCode)) {
            return FileUtils.getFileName(configValue);
        }
        return null;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
