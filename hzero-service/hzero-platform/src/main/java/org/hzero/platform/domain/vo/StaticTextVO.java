package org.hzero.platform.domain.vo;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.common.query.Where;
import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.platform.domain.entity.StaticText;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author FaTao Liu
 * @date 2018/07/23 15:37
 */
@ApiModel("静态文本")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StaticTextVO extends AuditDomain {

    @ApiModelProperty("文本ID")
    @Where
    @Encrypt
    private Long textId;
    @ApiModelProperty("文本编码")
    @Where
    private String textCode;
    @ApiModelProperty("标题")
    private String title;
    @ApiModelProperty("描述")
    private String description;
    @ApiModelProperty("父级ID")
    @Encrypt
    private Long parentId;
    @ApiModelProperty("所有父级ID")
    private String parentIds;
    @ApiModelProperty("有效期起")
    @JsonFormat(pattern = BaseConstants.Pattern.DATETIME)
    private LocalDateTime startDate;
    @ApiModelProperty("有效期止")
    @JsonFormat(pattern = BaseConstants.Pattern.DATETIME)
    private LocalDateTime endDate;
    private Long objectVersionNumber;
    @ApiModelProperty("语言")
    private String lang;
    @ApiModelProperty("富文本")
    private String text;
    private List<StaticTextVO> children;
    @ApiModelProperty("租户ID")
    private Long tenantId;
    private String tenantName;
    @ApiModelProperty("公司ID")
    @Encrypt
    private Long companyId;
    private String companyName;
    @JsonIgnore
    private String paramTextCode;
    @JsonIgnore
    private String paramTitle;
    @JsonIgnore
    private Date now;
    @JsonIgnore
    private StaticTextVO parent;

    @Override
    public Class<? extends SecurityToken> associateEntityClass(){
        return StaticText.class;
    }

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getTextId() {
        return textId;
    }

    public void setTextId(Long textId) {
        this.textId = textId;
    }

    /**
     * @return 编码
     */
    public String getTextCode() {
        return textCode;
    }

    public void setTextCode(String textCode) {
        this.textCode = textCode;
    }

    /**
     * @return 标题
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return 描述
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return 父级ID
     */
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParentIds() {
        return parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<StaticTextVO> getChildren() {
        return children;
    }

    public void setChildren(List<StaticTextVO> children) {
        this.children = children;
    }

    public String getParamTextCode() {
        return paramTextCode;
    }

    public void setParamTextCode(String paramTextCode) {
        this.paramTextCode = paramTextCode;
    }

    public String getParamTitle() {
        return paramTitle;
    }

    public void setParamTitle(String paramTitle) {
        this.paramTitle = paramTitle;
    }

    public Date getNow() {
        return now;
    }

    public void setNow(Date now) {
        this.now = now;
    }

    public StaticTextVO getParent() {
        return parent;
    }

    public void setParent(StaticTextVO parent) {
        this.parent = parent;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
