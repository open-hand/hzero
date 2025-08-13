package org.hzero.platform.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.constraints.NotBlank;
import org.hzero.platform.domain.vo.DashboardLayoutVO;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 工作台配置
 *
 * @author zhiying.dong@hand-china.com 2018-09-25 10:51:53
 */
@ApiModel("工作台配置")
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_dashboard_layout")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardLayout extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_CODE = "code";
    public static final String FIELD_W = "w";
    public static final String FIELD_H = "h";
    public static final String FIELD_X = "x";
    public static final String FIELD_Y = "y";
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_ROLE_ID = "roleId";
    public static final String FIELD_TENANT_ID = "tenantId";


    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------
    public DashboardLayoutVO convertToDashboardLayoutVO() {
        DashboardLayoutVO dashboardLayoutVO = new DashboardLayoutVO();
        BeanUtils.copyProperties(this, dashboardLayoutVO);
        return dashboardLayoutVO;
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long id;
    @ApiModelProperty(value = "卡片编码")
    @NotBlank
    private String code;
    @ApiModelProperty(value = "卡片宽度")
    @NotNull
    private Integer w;
    @ApiModelProperty(value = "卡片高度")
    @NotNull
    private Integer h;
    @ApiModelProperty(value = "卡片位置X")
    @NotNull
    private Integer x;
    @ApiModelProperty(value = "卡片位置Y")
    @NotNull
    private Integer y;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id")
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "用户Id")
    @NotNull
    @Encrypt
    private Long userId;
    @ApiModelProperty(value = "角色Id")
    @NotNull
    @Encrypt
    private Long roleId;
    @ApiModelProperty(value = "卡片Id，hpfm_dashboard_card.id")
    @NotNull
    @Encrypt
    private Long cardId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    /**
     * 角色的levelPath信息
     */
    @Transient
    @JsonIgnore
    private String levelPath;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getLevelPath() {
        return levelPath;
    }

    public void setLevelPath(String levelPath) {
        this.levelPath = levelPath;
    }

    /**
     * @return 表ID，主键，供其他表做外键
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
     * @return 卡片宽度
     */
    public Integer getW() {
        return w;
    }

    public void setW(Integer w) {
        this.w = w;
    }

    /**
     * @return 卡片高度
     */
    public Integer getH() {
        return h;
    }

    public void setH(Integer h) {
        this.h = h;
    }

    /**
     * @return 卡片位置X
     */
    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    /**
     * @return 卡片位置Y
     */
    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    /**
     * @return 租户ID, hpfm_tenant.tenant_id
     */
    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
