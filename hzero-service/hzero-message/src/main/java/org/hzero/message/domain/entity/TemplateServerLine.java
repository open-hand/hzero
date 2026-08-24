package org.hzero.message.domain.entity;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Range;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.jackson.annotation.Trim;
import org.hzero.core.util.Regexs;
import org.hzero.message.domain.repository.TemplateServerLineRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * <p>
 * 消息模板账户行
 * </p>
 *
 * @author qingsheng.chen 2018/9/30 星期日 12:39
 */
@ApiModel("消息模板账户行")
@VersionAudit
@ModifyAudit
@Table(name = "hmsg_template_server_line")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemplateServerLine extends AuditDomain {

    public static final String FIELD_TEMP_SERVER_LINE_ID = "tempServerLineId";
    public static final String FIELD_TEMP_SERVER_ID = "tempServerId";
    public static final String FIELD_TYPE_CODE = "typeCode";
    public static final String FIELD_TEMPLATE_CODE = "templateCode";
    public static final String FIELD_SERVER_CODE = "serverCode";
    public static final String FIELD_REMARK = "remark";
    public static final String FIELD_TRY_TIMES = "tryTimes";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";


    /**
     * 校验模板服务关联是否重复
     *
     * @param templateServerLineRepository 模板服务关联
     */
    public void validTemplateServer(TemplateServerLineRepository templateServerLineRepository) {
        if (!Objects.equals(typeCode, HmsgConstant.MessageType.WEB) && !Objects.equals(typeCode, HmsgConstant.MessageType.WEB_HOOK)) {
            Assert.isTrue(StringUtils.isNotBlank(serverCode), HmsgConstant.ErrorCode.TEMPLATE_NO_SERVER);
        }
        int cnt;
        if (Objects.equals(typeCode, HmsgConstant.MessageType.WEB_HOOK)) {
            // webhook允许多个，但模板编码不可重复
            cnt = templateServerLineRepository.selectCountByCondition(Condition.builder(TemplateServerLine.class).andWhere(Sqls.custom()
                    .andEqualTo(FIELD_TEMP_SERVER_ID, tempServerId)
                    .andEqualTo(FIELD_TYPE_CODE, typeCode)
                    .andEqualTo(FIELD_TEMPLATE_CODE, templateCode)
                    .andNotEqualTo(FIELD_TEMP_SERVER_LINE_ID, tempServerLineId, true))
                    .build());
        } else {
            cnt = templateServerLineRepository.selectCountByCondition(Condition.builder(TemplateServerLine.class).andWhere(Sqls.custom()
                    .andEqualTo(FIELD_TEMP_SERVER_ID, tempServerId)
                    .andEqualTo(FIELD_TYPE_CODE, typeCode)
                    .andNotEqualTo(FIELD_TEMP_SERVER_LINE_ID, tempServerLineId, true))
                    .build());
        }
        Assert.isTrue(cnt == 0, HmsgConstant.ErrorCode.REPEAT_SERVER_ASSOCIATION);
    }

    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long tempServerLineId;
    @ApiModelProperty(value = "消息模板账户,hmsg_template_server.temp_server_id", required = true)
    @Encrypt
    private Long tempServerId;
    @ApiModelProperty(value = "模版类型，值集:HMSG.MESSAGE_TYPE", required = true)
    @NotBlank
    @Size(max = 30)
    @LovValue(lovCode = "HMSG.MESSAGE_TYPE")
    private String typeCode;
    @NotBlank
    @Size(max = 60)
    @Trim
    @ApiModelProperty("模板编码")
    @Pattern(regexp = Regexs.CODE_UPPER)
    private String templateCode;
    @ApiModelProperty("服务编码")
    private String serverCode;
    @ApiModelProperty(value = "备注说明")
    private String remark;
    @ApiModelProperty(value = "重试次数")
    private Integer tryTimes;
    @ApiModelProperty(value = "启用标识", required = true)
    @NotNull
    @Range(max = 1, min = 0)
    private Integer enabledFlag;

    @Transient
    @ApiModelProperty("消息类型")
    private String typeMeaning;
    @Transient
    @ApiModelProperty("服务名称")
    private String serverName;
    @Transient
    @ApiModelProperty("模板名称")
    private String templateName;
    @ApiModelProperty(value = "租户ID")
    private Long tenantId;

    public Long getTempServerLineId() {
        return tempServerLineId;
    }

    public TemplateServerLine setTempServerLineId(Long tempServerLineId) {
        this.tempServerLineId = tempServerLineId;
        return this;
    }

    public Long getTempServerId() {
        return tempServerId;
    }

    public TemplateServerLine setTempServerId(Long tempServerId) {
        this.tempServerId = tempServerId;
        return this;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public TemplateServerLine setTypeCode(String typeCode) {
        this.typeCode = typeCode;
        return this;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public TemplateServerLine setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
        return this;
    }

    public String getServerCode() {
        return serverCode;
    }

    public TemplateServerLine setServerCode(String serverCode) {
        this.serverCode = serverCode;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public TemplateServerLine setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public Integer getTryTimes() {
        return tryTimes;
    }

    public TemplateServerLine setTryTimes(Integer tryTimes) {
        this.tryTimes = tryTimes;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public TemplateServerLine setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public String getTypeMeaning() {
        return typeMeaning;
    }

    public TemplateServerLine setTypeMeaning(String typeMeaning) {
        this.typeMeaning = typeMeaning;
        return this;
    }

    public String getServerName() {
        return serverName;
    }

    public TemplateServerLine setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    public String getTemplateName() {
        return templateName;
    }

    public TemplateServerLine setTemplateName(String templateName) {
        this.templateName = templateName;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public TemplateServerLine setTenantId(Long tenantId) {
        this.tenantId = tenantId;
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
