package org.hzero.iam.domain.entity;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 租户访问审计
 *
 * @author qingsheng.chen@hand-china.com 2019-03-06 13:53:40
 */
@ApiModel("租户访问审计")
@VersionAudit
@ModifyAudit
@Table(name = "hiam_tenant_access")
public class TenantAccess extends AuditDomain {
    public static final String ENCRYPT_KEY = "hiam_tenant_access";
    public static final String FIELD_ACCESS_ID = "accessId";
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_ACCESS_DATETIME = "accessDatetime";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long accessId;
    @ApiModelProperty(value = "用户ID,iam_user.id", required = true)
    @NotNull
    private Long userId;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id", required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "访问时间", required = true)
    @NotNull
    private Date accessDatetime;
    @ApiModelProperty(value = "访问次数")
    @NotNull
    private Integer accessCount;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getAccessId() {
        return accessId;
    }

    /**
     * @return 用户ID, iam_user.id
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @return 租户ID, hpfm_tenant.tenant_id
     */
    public Long getTenantId() {
        return tenantId;
    }

    /**
     * @return 访问时间
     */
    public Date getAccessDatetime() {
        return accessDatetime;
    }

    public TenantAccess setAccessId(Long accessId) {
        this.accessId = accessId;
        return this;
    }

    public TenantAccess setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public TenantAccess setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public TenantAccess setAccessDatetime(Date accessDatetime) {
        this.accessDatetime = accessDatetime;
        return this;
    }

    public Integer getAccessCount() {
        return accessCount;
    }

    public TenantAccess setAccessCount(Integer accessCount) {
        this.accessCount = accessCount;
        return this;
    }
}
