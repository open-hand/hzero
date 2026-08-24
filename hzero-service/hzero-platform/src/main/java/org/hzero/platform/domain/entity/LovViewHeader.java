package org.hzero.platform.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import javax.validation.constraints.NotEmpty;

import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import org.hibernate.validator.constraints.Range;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Regexs;
import org.hzero.platform.domain.repository.LovViewHeaderRepository;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import io.choerodon.mybatis.domain.AuditDomain;

import java.util.Objects;

/**
 * 值集视图头
 *
 * @author gaokuo.dai@hand-china.com 2018年6月13日下午12:40:22
 */
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_lov_view_header")
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("值集视图头")
@MultiLanguage
public class LovViewHeader extends AuditDomain {
    
    public static final String FIELD_VIEW_HEADER_ID = "viewHeaderId";
    public static final String FIELD_VIEW_CODE = "viewCode";
    public static final String FIELD_VIEW_NAME = "viewName";
    public static final String FIELD_LOV_ID = "lovId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_VALUE_FIELD = "valueField";
    public static final String FIELD_DISPLAY_FIELD = "displayField";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_WIDTH = "width";
    public static final String FIELD_HEIGHT = "height";
    public static final String FIELD_PAGE_SIZE = "pageSize";
    public static final String FIELD_DELAY_LOAD_FLAG = "delayLoadFlag";
    public static final String FIELD_CHILDREN_FIELD_NAME = "childrenFieldName";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";


    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------
    
    /**
     * 值集表级联删除<br/>
     * 需要版本号
     * @return 被删除的<b>头</b>数量
     */
    public int cascadeDelete(LovViewHeaderRepository headerRepository) {
        Assert.notNull(this.viewHeaderId, BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(this.getObjectVersionNumber(), BaseConstants.ErrorCode.OPTIMISTIC_LOCK);
        headerRepository.deleteViewLineByviewHeaderId(this.viewHeaderId);
        return headerRepository.deleteByPrimaryKey(this.viewHeaderId);
    }
    
    /**
     * 数据校验
     * @param lovViewHeaderRepository
     */
    public void validate(LovViewHeaderRepository lovViewHeaderRepository) {
        Assert.notNull(this.viewCode, BaseConstants.ErrorCode.DATA_INVALID);
        if(lovViewHeaderRepository.selectRepeatCodeCount(this) > 0) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR_CODE_REPEAT);
        }
    }

    /**
     * 租户级校验值集视图头的租户Id是否与数据库中值集视图头租户Id相同
     */
    public void checkOrgLovViewHeaderTenant(LovViewHeaderRepository lovViewHeaderRepository) {
        LovViewHeader lovViewHeader = lovViewHeaderRepository.selectByPrimaryKey(this.viewHeaderId);
        Assert.notNull(lovViewHeader, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        // 更新值集值时需要校验传入的值集Id是否与当前租户Id匹配，不匹配则抛出异常
        if (!Objects.equals(lovViewHeader.getTenantId(), this.tenantId)) {
            throw new CommonException(HpfmMsgCodeConstants.ERROR_LOV_TENANT_NOT_MATCH);
        }
    }
    
    //================================
    // db map
    //================================
    
    @Id
    @GeneratedValue
    @ApiModelProperty("视图头ID")
    @Encrypt
    private Long viewHeaderId;
    @NotEmpty
    @Size(max = 80)
    @Pattern(regexp = Regexs.CODE_UPPER)
    @ApiModelProperty("视图代码")
    private String viewCode;
    @NotEmpty
    @Size(max = 240)
    @ApiModelProperty("视图名称")
    @MultiLanguageField
    private String viewName;
    @NotNull
    @ApiModelProperty("值集ID")
    @Encrypt
    private Long lovId;
    @ApiModelProperty("租户ID")
    @MultiLanguageField
    private Long tenantId;
    @NotEmpty
    @Size(max = 30)
    @ApiModelProperty("值字段")
    private String valueField;
    @NotEmpty
    @Size(max = 30)
    @ApiModelProperty("显示字段")
    private String displayField;
    @Size(max = 60)
    @ApiModelProperty("标题")
    @MultiLanguageField
    private String title;
    @ApiModelProperty("宽度")
    private Integer width;
    @ApiModelProperty("高度")
    private Integer height;
    @NotNull
    @ApiModelProperty("分页大小")
    private Integer pageSize;
    @NotNull
    @Range(min = 0, max = 1)
    @ApiModelProperty("是否延迟加载")
    private Integer delayLoadFlag;
    @Size(max = 30)
    @ApiModelProperty("树形查询子字段名")
    private String childrenFieldName;
    @NotNull
    @ApiModelProperty("启用标识")
    private Integer enabledFlag;
    
    @Transient
    @Pattern(regexp = Regexs.CODE)
    @ApiModelProperty("值集代码")
    private String lovCode;
    @Transient
    @ApiModelProperty("值集名称")
    private String lovName;
    @Transient
    @ApiModelProperty("租户名称")
    private String tenantName;
    
    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getViewHeaderId() {
        return viewHeaderId;
    }
    public void setViewHeaderId(Long viewHeaderId) {
        this.viewHeaderId = viewHeaderId;
    }
    /**
     * @return 值集视图代码
     */
    public String getViewCode() {
        return viewCode;
    }
    public void setViewCode(String viewCode) {
        this.viewCode = viewCode;
    }
    /**
     * @return 视图名称
     */
    public String getViewName() {
        return viewName;
    }
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
    /**
     * @return 值集ID,hpfm_lov.lov_id
     */
    public Long getLovId() {
        return lovId;
    }
    public void setLovId(Long lovId) {
        this.lovId = lovId;
    }
    /**
     * @return 租户ID
     */
    public Long getTenantId() {
        return tenantId;
    }
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
    /**
     * @return 值字段 
     */
    public String getValueField() {
        return valueField;
    }
    public void setValueField(String valueField) {
        this.valueField = valueField;
    }
    /**
     * @return 显示字段
     */
    public String getDisplayField() {
        return displayField;
    }
    public void setDisplayField(String displayField) {
        this.displayField = displayField;
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
     * @return 宽度
     */
    public Integer getWidth() {
        return width;
    }
    public void setWidth(Integer width) {
        this.width = width;
    }
    /**
     * @return 高度
     */
    public Integer getHeight() {
        return height;
    }
    public void setHeight(Integer height) {
        this.height = height;
    }
    /**
     * @return 分页大小 
     */
    public Integer getPageSize() {
        return pageSize;
    }
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
    /**
     * @return 是否延迟加载 
     */
    public Integer getDelayLoadFlag() {
        return delayLoadFlag;
    }
    public void setDelayLoadFlag(Integer delayLoadFlag) {
        this.delayLoadFlag = delayLoadFlag;
    }
    /**
     * @return 树形查询子字段名
     */
    public String getChildrenFieldName() {
        return childrenFieldName;
    }
    public void setChildrenFieldName(String childrenFieldName) {
        this.childrenFieldName = childrenFieldName;
    }
    /**
     * @return 是否启用
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }
    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }
    /**
     * @return 查询字段：值集代码
     */
    public String getLovCode() {
        return lovCode;
    }
    public void setLovCode(String lovCode) {
        this.lovCode = lovCode;
    }
    /**
     * @return 查询字段：值集名称
     */
    public String getLovName() {
        return lovName;
    }
    public void setLovName(String lovName) {
        this.lovName = lovName;
    }
    /**
     * @return 查询字段：租户名称
     */
    public String getTenantName() {
        return tenantName;
    }
    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LovViewHeader [viewHeaderId=");
        builder.append(viewHeaderId);
        builder.append(", viewCode=");
        builder.append(viewCode);
        builder.append(", viewName=");
        builder.append(viewName);
        builder.append(", lovId=");
        builder.append(lovId);
        builder.append(", tenantId=");
        builder.append(tenantId);
        builder.append(", valueField=");
        builder.append(valueField);
        builder.append(", displayField=");
        builder.append(displayField);
        builder.append(", title=");
        builder.append(title);
        builder.append(", width=");
        builder.append(width);
        builder.append(", height=");
        builder.append(height);
        builder.append(", pageSize=");
        builder.append(pageSize);
        builder.append(", delayLoadFlag=");
        builder.append(delayLoadFlag);
        builder.append(", childrenFieldName=");
        builder.append(childrenFieldName);
        builder.append(", enabledFlag=");
        builder.append(enabledFlag);
        builder.append(", lovCode=");
        builder.append(lovCode);
        builder.append(", lovName=");
        builder.append(lovName);
        builder.append(", tenantName=");
        builder.append(tenantName);
        builder.append("]");
        return builder.toString();
    }
    
}
