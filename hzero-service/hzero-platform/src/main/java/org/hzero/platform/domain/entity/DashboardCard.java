package org.hzero.platform.domain.entity;

import java.util.List;
import java.util.Objects;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Regexs;
import org.hzero.mybatis.common.query.Where;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.platform.domain.repository.DashboardCardRepository;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("平台卡片")
@VersionAudit
@ModifyAudit
@MultiLanguage
@Table(name = "hpfm_dashboard_card")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardCard extends AuditDomain {

    public static final String FIELD_ID="id";
    public static final String FIELD_CODE = "code";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_W = "w";
    public static final String FIELD_H = "h";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_CATALOGTYPE = "catalogType";
    public static final String FIELD_LEVEL= "level";
    public static final String FIELD_LOGO="logo";
    public static final String FIELD_ENABLED_FLAG="enabledFlag";
    public static final String FIELD_TENANT_ID="tenantId";
    public static final String FIELD_CARD_PARAMS="cardParams";


    /**
     * 校验code是否重复，重复则不可保存
     */
    public void validate(DashboardCardRepository cardRepository) {
        int count = cardRepository.selectCountByCondition(Condition.builder(DashboardCard.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DashboardCard.FIELD_CODE, this.code)
                        .andEqualTo(DashboardCard.FIELD_CARD_PARAMS, this.cardParams)
                        .andEqualTo(DashboardCard.FIELD_TENANT_ID, this.tenantId)
                ).build());
        if (count != 0) {
            throw new CommonException(HpfmMsgCodeConstants.DASHBOARD_CODE_REPEAT, code);
        }
    }

    /**
     * 校验数据合法性（传入的租户id是否与数据库中的租户id匹配）
     *
     * @param incomingTenantId 传入的租户id
     */
    public void judgeCardValidity(Long incomingTenantId) {
        Assert.isTrue(Objects.equals(this.tenantId, incomingTenantId), BaseConstants.ErrorCode.DATA_INVALID);
    }

    @ApiModelProperty(value = "表ID，主键")
    @Id
    @GeneratedValue
    @Where
    @Encrypt
    private Long id;
    @ApiModelProperty(value = "卡片编码")
    @NotBlank
    @Length(max = 250)
    @Pattern(regexp = Regexs.CODE)
    @Where
    @Column(updatable = false)
    private String code;
    @ApiModelProperty(value = "卡片名称")
    @NotBlank
    @MultiLanguageField
    @Length(max = 32)
    private String name;
    @ApiModelProperty(value = "卡片描述")
    @Length(max = 250)
    @MultiLanguageField
    private String description;
    @ApiModelProperty(value = "目录类型")
    @NotBlank
    @Length(max = 32)
    @LovValue(lovCode = "HPFM.DASHBOARD_CARD.TYPE", meaningField = "catalogMeaning")
    private String catalogType;
    @ApiModelProperty(value = "所属级别")
    @NotBlank
    @Length(max = 32)
    @Column(name = "fd_level")
    @LovValue(lovCode = "HPFM.DATA_TENANT_LEVEL", meaningField = "levelMeaning")
    @Where
    private String level;
    @ApiModelProperty(value = "宽")
    @NotNull
    private Integer w;
    @ApiModelProperty(value = "高")
    @NotNull
    private Integer h;
    @ApiModelProperty("卡片图标")
    @Length(max = 300)
    private String logo;
    @NotNull
    @ApiModelProperty("是否启用标识，1启用 0停用")
    @Range(max = 1)
    @Where
    private Integer enabledFlag;
    @NotNull
    @ApiModelProperty("租户ID hpfm_tenant.tenant_id")
    @MultiLanguageField
    private Long tenantId;
    /**
     * 该字段中可以传入卡片所需的基本参数，例如卡片下关联哪张报表等
     * 前端未传值则设置默认值为"none"，传值则以传递的值为准
     */
    @ApiModelProperty("卡片参数字段，用于传递卡片所需参数")
    private String cardParams = "none";

    @Transient
    @ApiModelProperty("是否已分配标识")
    private Integer assigned;
    @Transient
    @ApiModelProperty("层级含义")
    private String levelMeaning;
    @Transient
    @ApiModelProperty("类型含义")
    private String catalogMeaning;
    @Transient
    @Encrypt
    private List<Long> excludeCardIds;
    /**
     * 条目层级与条目Id，用于判断获取可分配卡片逻辑
     * 若是租户级条目调用获取可分配卡片接口时需要传递clauseLevel参数以及clauseId参数
     * clauseLevel 条目层级
     * clauseId 条目Id
     */
    @Transient
    @JsonIgnore
    private String clauseLevel;
    @Transient
    @JsonIgnore
    @Encrypt
    private Long clauseId;

    /**
     * @return 主键id
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return 卡片编码
     */
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return 卡片所属级别
     */
    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    /**
     * @return 卡片宽
     */
    public Integer getW() {
        return w;
    }

    public void setW(Integer w) {
        this.w = w;
    }

    /**
     * @return 卡片高
     */
    public Integer getH() {
        return h;
    }

    public void setH(Integer h) {
        this.h = h;
    }

    /**
     * @return 卡片图标
     */
    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    /**
     * @return 卡片启用标识
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    /**
     * @return 已分配标识
     */
    public Integer getAssigned() {
        return assigned;
    }

    public void setAssigned(Integer assigned) {
        this.assigned = assigned;
    }

    /**
     * @return 获取卡片名称
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return 获取卡片所属目录类型
     */
    public String getCatalogType() {
        return catalogType;
    }

    public void setCatalogType(String catalogType) {
        this.catalogType = catalogType;
    }

    /**
     * @return 获取卡片描述
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DashboardCard() {
    }

    public String getLevelMeaning() {
        return levelMeaning;
    }

    public void setLevelMeaning(String levelMeaning) {
        this.levelMeaning = levelMeaning;
    }

    public String getCatalogMeaning() {
        return catalogMeaning;
    }

    public void setCatalogMeaning(String catalogMeaning) {
        this.catalogMeaning = catalogMeaning;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public List<Long> getExcludeCardIds() {
        return excludeCardIds;
    }

    public void setExcludeCardIds(List<Long> excludeCardIds) {
        this.excludeCardIds = excludeCardIds;
    }

    public String getClauseLevel() {
        return clauseLevel;
    }

    public void setClauseLevel(String clauseLevel) {
        this.clauseLevel = clauseLevel;
    }

    public Long getClauseId() {
        return clauseId;
    }

    public void setClauseId(Long clauseId) {
        this.clauseId = clauseId;
    }

    public String getCardParams() {
        return cardParams;
    }

    public void setCardParams(String cardParams) {
        this.cardParams = cardParams;
    }
}
