package org.hzero.iam.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 安全组工作台配置
 *
 * @author xingxing.wu@hand-china.com 2019-10-28 10:00:59
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("安全组工作台配置")
@VersionAudit
@ModifyAudit
@Table(name = "hiam_sec_grp_acl_dashboard")
public class SecGrpAclDashboard extends AuditDomain {
    public static final String ENCRYPT_KEY = "hiam_sec_grp_acl_dashboard";
    public static final String FIELD_SEC_GRP_ACL_DASHBOARD_ID = "secGrpAclDashboardId";
    public static final String FIELD_SEC_GRP_ID = "secGrpId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_CARD_ID = "cardId";
    public static final String FIELD_X = "x";
    public static final String FIELD_Y = "y";
    public static final String FIELD_DEFAULT_DISPLAY_FLAG = "defaultDisplayFlag";
    public static final String FIELD_REMARK = "remark";

    public SecGrpAclDashboard() {
    }

    public SecGrpAclDashboard(Long secGrpId) {
        this.secGrpId = secGrpId;
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long secGrpAclDashboardId;
    @ApiModelProperty(value = "安全组ID", required = true)
    @NotNull
    @Encrypt
    private Long secGrpId;
    @ApiModelProperty(value = "租户", required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "卡片ID，hpfm.hpfm_dashboard_card.id", required = true)
    @NotNull
    private Long cardId;
    @ApiModelProperty(value = "x轴", required = true)
    @NotNull
    private Integer x;
    @ApiModelProperty(value = "y轴", required = true)
    @NotNull
    private Integer y;
    @ApiModelProperty(value = "初始化标识", required = true)
    @NotNull
    private Integer defaultDisplayFlag;
    @ApiModelProperty(value = "备注说明")
    private String remark;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String code;
    @Transient
    private String name;
    @Transient
    @LovValue(lovCode = "HPFM.DASHBOARD_CARD.TYPE", meaningField = "catalogMeaning")
    private String catalogType;
    @Transient
    private Integer w;
    @Transient
    private Integer h;
    @Transient
    private String catalogMeaning;
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getSecGrpAclDashboardId() {
        return secGrpAclDashboardId;
    }

    public void setSecGrpAclDashboardId(Long secGrpAclDashboardId) {
        this.secGrpAclDashboardId = secGrpAclDashboardId;
    }

    /**
     * @return 安全组ID
     */
    public Long getSecGrpId() {
        return secGrpId;
    }

    public void setSecGrpId(Long secGrpId) {
        this.secGrpId = secGrpId;
    }

    /**
     * @return 租户
     */
    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return 卡片ID，hpfm.hpfm_dashboard_card.id
     */
    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    /**
     * @return x轴
     */
    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    /**
     * @return y轴
     */
    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    /**
     * @return 初始化标识
     */
    public Integer getDefaultDisplayFlag() {
        return defaultDisplayFlag;
    }

    public void setDefaultDisplayFlag(Integer defaultDisplayFlag) {
        this.defaultDisplayFlag = defaultDisplayFlag;
    }

    /**
     * @return 备注说明
     */
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatalogType() {
        return catalogType;
    }

    public void setCatalogType(String catalogType) {
        this.catalogType = catalogType;
    }

    public Integer getW() {
        return w;
    }

    public void setW(Integer w) {
        this.w = w;
    }

    public Integer getH() {
        return h;
    }

    public void setH(Integer h) {
        this.h = h;
    }

    public String getCatalogMeaning() {
        return catalogMeaning;
    }

    public void setCatalogMeaning(String catalogMeaning) {
        this.catalogMeaning = catalogMeaning;
    }
}
