package org.hzero.platform.domain.entity;

import java.util.Objects;
import java.util.regex.Matcher;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.common.HZeroConstant;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.message.MessageAccessor;
import org.hzero.core.util.Regexs;
import org.hzero.platform.domain.repository.LovRepository;
import org.hzero.platform.infra.constant.Constants;
import org.hzero.platform.infra.constant.FndConstants;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 值集实体
 *
 * @author gaokuo.dai@hand-china.com    2018年6月5日下午8:09:30
 * @version 1.0
 */

@ApiModel("值集实体")
@VersionAudit
@ModifyAudit
@MultiLanguage
@Table(name = "hpfm_lov")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Lov extends AuditDomain {
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("^/[a-z0-9-]*/\\*\\*$");

    public static final String FIELD_LOV_ID = "lovId";
    public static final String FIELD_LOV_CODE = "lovCode";
    public static final String FIELD_LOV_TYPE_CODE = "lovTypeCode";
    public static final String FIELD_ROUTE_NAME = "routeName";
    public static final String FIELD_LOV_NAME = "lovName";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_PARENT_LOV_CODE = "parentLovCode";
    public static final String FIELD_PARENT_TENANT_ID = "parentTenantId";
    public static final String FIELD_CUSTOM_SQL = "customSql";
    public static final String FIELD_CUSTOM_URL = "customUrl";
    public static final String FIELD_VALUE_FIELD = "valueField";
    public static final String FIELD_DISPLAY_FIELD = "displayField";
    public static final String FIELD_ENCRYPT_FIELD = "encryptField";
    public static final String FIELD_MUST_PAGE_FLAG = "mustPageFlag";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_TRANSLATION_SQL = "translationSql";

    public static final String FIELD_VALUE = "value";
    public static final String FIELD_MEANING = "meaning";

    public static final String FIELD_PUBLIC_FLAG = "publicFlag";

    //================================
    // public business methods
    //================================

    /**
     * 数据校验
     *
     * @param lovRepository
     */
    public void validate(LovRepository lovRepository) {
        Assert.notNull(this.lovCode, BaseConstants.ErrorCode.DATA_INVALID);
        // 路由校验
        if (!Objects.equals(this.lovTypeCode, FndConstants.LovTypeCode.INDEPENDENT) && StringUtils.isEmpty(this.routeName)) {
            throw new CommonException(HpfmMsgCodeConstants.ERROR_LOV_INVALID_ROUTE_NAME);
        }
        // 20200624 添加值集校验逻辑，URL值集校验URL必输，SQL值集校验SQL必输
        if (Objects.equals(this.lovTypeCode, FndConstants.LovTypeCode.URL) && StringUtils.isEmpty(this.customUrl)) {
            throw new CommonException(HpfmMsgCodeConstants.ERROR_LOV_URL_NOT_NULL);
        }

        if (Objects.equals(this.lovTypeCode, FndConstants.LovTypeCode.SQL) && StringUtils.isEmpty(this.customSql)) {
            throw new CommonException(HpfmMsgCodeConstants.ERROR_LOV_SQL_NOT_NULL);
        }
        // 编码重复性校验
        if (lovRepository.selectRepeatCodeCount(this) > 0) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR_CODE_REPEAT);
        }
        // 父值集校验, parentLovCode和parentTenantId必须同时存在或同时不存在
        boolean parentLovCodeExists = StringUtils.isNotEmpty(this.parentLovCode);
        boolean parentTenantIdExists = (this.parentTenantId != null);
        Assert.isTrue(parentLovCodeExists == parentTenantIdExists, BaseConstants.ErrorCode.DATA_INVALID);
        // RouteName 裁剪
        if (StringUtils.isNotBlank(routeName)) {
            Matcher matcher = PATTERN.matcher(routeName);
            if (matcher.find()) {
                routeName = routeName.substring(1, routeName.length() - 3);
            }
        }
    }

    /**
     * 得到真正的数据请求Url地址[带路由信息]
     *
     * @return 真正的数据请求Url地址
     */
    public String convertTrueUrl() {
        Assert.notNull(this.lovCode, BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(this.lovTypeCode, BaseConstants.ErrorCode.DATA_INVALID);
        if (StringUtils.isEmpty(this.routeName)) {
            this.routeName = Constants.APP_CODE;
        }
        String queryUrl;
        switch (this.lovTypeCode) {
            case FndConstants.LovTypeCode.INDEPENDENT:
                queryUrl = Constants.APP_CODE + HZeroConstant.Lov.ApiAddress.LOV_VALUE_SERVICE_ADDRESS + "/page";
                break;
            case FndConstants.LovTypeCode.URL:
                queryUrl = this.routeName + this.customUrl;
                break;
            case FndConstants.LovTypeCode.SQL:
                if (this.tenantId == null || Objects.equals(this.tenantId, BaseConstants.DEFAULT_TENANT_ID)) {
                    queryUrl = this.routeName + HZeroConstant.Lov.ApiAddress.SQL_LOV_SERVICE_ADDRESS;
                } else {
                    queryUrl = this.routeName + HZeroConstant.Lov.ApiAddress.SQL_ORG_LOV_SERVICE_ADDRESS;
                }
                break;
            default:
                queryUrl = null;
                break;
        }
        return "/" + queryUrl;
    }

    /**
     * 转换为缓存需要的字段
     *
     * @return
     */
    public Lov convertToCacheVo() {
        Lov vo = new Lov();
        vo.lovId = this.lovId;
        vo.tenantId = this.tenantId;
        vo.lovCode = this.lovCode;
        vo.lovTypeCode = this.lovTypeCode;
        vo.customUrl = this.customUrl;
        vo.routeName = this.routeName;
        vo.mustPageFlag = this.mustPageFlag;
        vo.valueField = this.valueField;
        vo.displayField = this.displayField;
        vo.encryptField = this.encryptField;
        vo.publicFlag = this.publicFlag;
        vo.lovName = this.lovName;
        return vo;
    }

    /**
     * 租户级校验值集头租户Id是否与数据库中值集Id相同
     */
    public void checkOrgLovTenant(LovRepository lovRepository) {
        Lov lov = lovRepository.selectByPrimaryKey(this.lovId);
        Assert.notNull(lov, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        // 更新值集值时需要校验传入的值集Id是否与当前租户Id匹配，不匹配则抛出异常
        if (!Objects.equals(lov.getTenantId(), this.tenantId)) {
            throw new CommonException(HpfmMsgCodeConstants.ERROR_LOV_TENANT_NOT_MATCH);
        }
    }

    //================================
    // db map
    //================================

    @Id
    @GeneratedValue
    @ApiModelProperty("值集ID")
    @Encrypt
    private Long lovId;
    @NotEmpty
    @Length(max = 60)
    @Pattern(regexp = Regexs.CODE_UPPER)
    @ApiModelProperty("值集代码")
    private String lovCode;
    @NotEmpty
    @Size(max = 30)
    @ApiModelProperty("值集类型")
    @LovValue(lovCode = "HPFM.LOV.LOV_TYPE")
    private String lovTypeCode;
    @Size(max = 120)
    @ApiModelProperty("目标路由")
    private String routeName;
    @NotEmpty
    @Size(max = 240)
    @MultiLanguageField
    @ApiModelProperty("值集名称")
    private String lovName;
    @Size(max = 480)
    @MultiLanguageField
    @ApiModelProperty("描述")
    private String description;
    @ApiModelProperty("租户ID")
    @MultiLanguageField
    private Long tenantId;
    @Size(max = 60)
    @Pattern(regexp = Regexs.CODE_UPPER)
    @ApiModelProperty("父值集代码")
    private String parentLovCode;
    @ApiModelProperty("父级值集租户ID")
    private Long parentTenantId;
    @ApiModelProperty("自定义SQL")
    private String customSql;
    @Size(max = 1800)
    @ApiModelProperty("查询URL")
    private String customUrl;
    @Size(max = 30)
    @ApiModelProperty("值字段")
    private String valueField;
    @Size(max = 30)
    @ApiModelProperty("显示字段")
    private String displayField;
    @Length(max = 480)
    @ApiModelProperty("加密字段")
    private String encryptField;
    @NotNull
    @Range(min = 0, max = 1)
    @ApiModelProperty("是否必须分页")
    private Integer mustPageFlag;
    @NotNull
    @Range(min = 0, max = 1)
    @ApiModelProperty("是否启用")
    private Integer enabledFlag;
    @ApiModelProperty("翻译sql")
    private String translationSql;
    @ApiModelProperty("公共标记")
    private Integer publicFlag;

    //================================
    // Transient field
    //================================

    @Transient
    @ApiModelProperty("值集类型含义")
    private String lovTypeMeaning;
    @Transient
    @ApiModelProperty("父值集名称")
    private String parentLovName;
    @Transient
    @ApiModelProperty("租户名称")
    private String tenantName;
    @Transient
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private LovAccessStatus accessStatus;
    @Transient
    @ApiModelProperty(hidden = true)
    private Integer siteQueryFlag;
    /**
     * 限定查询类型是否是值集查询，若是值集查询则需传递该参数为1，在mapper.xml中限定lov查询逻辑
     */
    @Transient
    @ApiModelProperty(hidden = true)
    private Integer lovQueryFlag;

    /**
     * 是否控制 SQL 类型值集，不允许新建，不允许查看SQL
     */
    @Transient
    @ApiModelProperty(hidden = true)
    private boolean sqlTypeControl = false;

    /**
     * 值集可访问状态
     *
     * @author gaokuo.dai@hand-china.com 2019年3月1日上午12:09:24
     */
    public enum LovAccessStatus {
        /**
         * 可访问
         */
        ACCESS,
        /**
         * 禁止访问
         */
        FORBIDDEN,
        /**
         * 未找到
         */
        NOT_FOUND
    }

    /**
     * 构造函数
     */
    public Lov() {
    }


    /**
     * 构造函数
     *
     * @param accessStatus 可访问状态
     */
    public Lov(LovAccessStatus accessStatus) {
        this.accessStatus = accessStatus;
    }

    /**
     * 构造函数
     *
     * @param lovId    主键
     * @param lovCode  LOV代码
     * @param tenantId 租户ID
     */
    public Lov(Long lovId, String lovCode, Long tenantId) {
        this.lovId = lovId;
        this.lovCode = lovCode;
        this.tenantId = tenantId;
    }

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getLovId() {
        return lovId;
    }

    public Lov setLovId(Long lovId) {
        this.lovId = lovId;
        return this;
    }

    /**
     * @return 值集代码
     */
    public String getLovCode() {
        return lovCode;
    }

    public Lov setLovCode(String lovCode) {
        this.lovCode = lovCode;
        return this;
    }

    /**
     * @return 值集名称
     */
    public String getLovName() {
        return lovName;
    }

    public Lov setLovName(String lovName) {
        this.lovName = lovName;
        return this;
    }

    /**
     * @return 值集类型: URL/SQL/IDP
     */
    public String getLovTypeCode() {
        return lovTypeCode;
    }

    public Lov setLovTypeCode(String lovTypeCode) {
        this.lovTypeCode = lovTypeCode;
        return this;
    }

    /**
     * @return 目标路由
     */
    public String getRouteName() {
        return routeName;
    }

    public Lov setRouteName(String routeName) {
        this.routeName = routeName;
        return this;
    }

    /**
     * @return 描述
     */
    public String getDescription() {
        return description;
    }

    public Lov setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * @return 租户ID, null/0时代表全局值集
     */
    public Long getTenantId() {
        return tenantId;
    }

    public Lov setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * @return 父级值集代码
     */
    public String getParentLovCode() {
        return parentLovCode;
    }

    public Lov setParentLovCode(String parentLovCode) {
        this.parentLovCode = parentLovCode;
        return this;
    }

    /**
     * @return 父级值集租户ID
     */
    public Long getParentTenantId() {
        return parentTenantId;
    }

    public Lov setParentTenantId(Long parentTenantId) {
        this.parentTenantId = parentTenantId;
        return this;
    }

    /**
     * @return 自定义sql
     */
    public String getCustomSql() {
        return customSql;
    }

    public Lov setCustomSql(String customSql) {
        this.customSql = customSql;
        return this;
    }

    /**
     * @return 查询URL
     */
    public String getCustomUrl() {
        return customUrl;
    }

    public Lov setCustomUrl(String customUrl) {
        this.customUrl = customUrl;
        return this;
    }

    /**
     * @return 是否必须分页
     */
    public Integer getMustPageFlag() {
        return mustPageFlag;
    }

    public Lov setMustPageFlag(Integer mustPageFlag) {
        this.mustPageFlag = mustPageFlag;
        return this;
    }

    /**
     * @return 是否启用
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public Lov setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    /**
     * @return 翻译sql
     */
    public String getTranslationSql() {
        return translationSql;
    }

    public Lov setTranslationSql(String translationSql) {
        this.translationSql = translationSql;
        return this;
    }

    public Integer getPublicFlag() {
        return publicFlag;
    }

    public Lov setPublicFlag(Integer publicFlag) {
        this.publicFlag = publicFlag;
        return this;
    }

    /**
     * @return 查询字段: lov类型含义
     */
    public String getLovTypeMeaning() {
        return lovTypeMeaning;
    }

    public Lov setLovTypeMeaning(String lovTypeMeaning) {
        this.lovTypeMeaning = lovTypeMeaning;
        return this;
    }

    /**
     * @return 查询字段: 父级值集名
     */
    public String getParentLovName() {
        return parentLovName;
    }

    public Lov setParentLovName(String parentLovName) {
        this.parentLovName = parentLovName;
        return this;
    }

    /**
     * @return 查询字段: 租户名称
     */
    public String getTenantName() {
        return tenantName;
    }

    public Lov setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    /**
     * @return 值字段
     */
    public String getValueField() {
        return valueField;
    }

    public Lov setValueField(String valueField) {
        this.valueField = valueField;
        return this;
    }

    /**
     * @return 显示字段
     */
    public String getDisplayField() {
        return displayField;
    }

    public Lov setDisplayField(String displayField) {
        this.displayField = displayField;
        return this;
    }

    public String getEncryptField() {
        return encryptField;
    }

    public Lov setEncryptField(String encryptField) {
        this.encryptField = encryptField;
        return this;
    }

    /**
     * @return 可访问状态
     */
    public LovAccessStatus getAccessStatus() {
        return accessStatus;
    }

    public Lov setAccessStatus(LovAccessStatus accessStatus) {
        this.accessStatus = accessStatus;
        return this;
    }

    /**
     * @return 平台级查询标识, 用以区分平台级/租户级汇总查询
     */
    public Integer getSiteQueryFlag() {
        return siteQueryFlag;
    }

    public Lov setSiteQueryFlag(Integer siteQueryFlag) {
        this.siteQueryFlag = siteQueryFlag;
        return this;
    }

    /**
     * @return lov查询标识，区分是普通列表查询还是url值集调用
     */
    public Integer getLovQueryFlag() {
        return lovQueryFlag;
    }

    public void setLovQueryFlag(Integer lovQueryFlag) {
        this.lovQueryFlag = lovQueryFlag;
    }

    public boolean isSqlTypeControl() {
        return sqlTypeControl;
    }

    public Lov setSqlTypeControl(boolean sqlTypeControl) {
        this.sqlTypeControl = sqlTypeControl;
        return this;
    }

    /**
     * 自定义：当前租户ID = 数据租户ID
     * 预定义：当前租户ID != 数据租户ID
     *
     * @return 返回是否自定义
     */
    public String getSource() {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        if (customUserDetails != null) {
            if (Objects.equals(customUserDetails.getTenantId(), tenantId)) {
                return MessageAccessor.getMessage("hpfm.info.customize").desc();
            }
        }
        return MessageAccessor.getMessage("hpfm.info.predefined").desc();
    }

    @Override
    public String toString() {
        return "Lov{" +
                "lovId=" + lovId +
                ", lovCode='" + lovCode + '\'' +
                ", lovTypeCode='" + lovTypeCode + '\'' +
                ", routeName='" + routeName + '\'' +
                ", lovName='" + lovName + '\'' +
                ", description='" + description + '\'' +
                ", tenantId=" + tenantId +
                ", parentLovCode='" + parentLovCode + '\'' +
                ", parentTenantId=" + parentTenantId +
                ", customSql='" + customSql + '\'' +
                ", customUrl='" + customUrl + '\'' +
                ", valueField='" + valueField + '\'' +
                ", displayField='" + displayField + '\'' +
                ", encryptField='" + encryptField + '\'' +
                ", mustPageFlag=" + mustPageFlag +
                ", enabledFlag=" + enabledFlag +
                ", translationSql='" + translationSql + '\'' +
                ", publicFlag=" + publicFlag +
                ", lovTypeMeaning='" + lovTypeMeaning + '\'' +
                ", parentLovName='" + parentLovName + '\'' +
                ", tenantName='" + tenantName + '\'' +
                ", accessStatus=" + accessStatus +
                ", siteQueryFlag=" + siteQueryFlag +
                ", lovQueryFlag=" + lovQueryFlag +
                ", sqlTypeControl=" + sqlTypeControl +
                '}';
    }
}
