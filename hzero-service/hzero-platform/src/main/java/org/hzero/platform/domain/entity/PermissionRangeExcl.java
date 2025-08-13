package org.hzero.platform.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 屏蔽范围黑名单
 *
 * @author qingsheng.chen@hand-china.com 2020-06-10 10:17:25
 */
@ApiModel("屏蔽范围黑名单")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hpfm_permission_range_excl")
public class PermissionRangeExcl extends AuditDomain {

    public static final String FIELD_RANGE_EXCLUDE_ID = "rangeExcludeId";
    public static final String FIELD_RANGE_ID = "rangeId";
    public static final String FIELD_SERVICE_NAME = "serviceName";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_SQL_ID = "sqlId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long rangeExcludeId;
    @ApiModelProperty(value = "屏蔽范围ID,hpfm_permission_range_excl.range_id", required = true)
    @NotNull
    @Encrypt
    private Long rangeId;
    @ApiModelProperty(value = "服务名称")
    private String serviceName;
    @ApiModelProperty(value = "租户id，hpfm_tenant.tenant_id")
    private Long tenantId;
    @ApiModelProperty(value = "屏蔽sqlId")
    private String sqlId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------
    @Transient
    private String tenantName;
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getRangeExcludeId() {
        return rangeExcludeId;
    }

    public void setRangeExcludeId(Long rangeExcludeId) {
        this.rangeExcludeId = rangeExcludeId;
    }

    /**
     * @return 屏蔽范围ID, hpfm_permission_range_excl.range_id
     */
    public Long getRangeId() {
        return rangeId;
    }

    public PermissionRangeExcl setRangeId(Long rangeId) {
        this.rangeId = rangeId;
        return this;
    }

    /**
     * @return 服务名称
     */
    public String getServiceName() {
        return serviceName;
    }

    public PermissionRangeExcl setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    /**
     * @return 租户id，hpfm_tenant.tenant_id
     */
    public Long getTenantId() {
        return tenantId;
    }

    public PermissionRangeExcl setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * @return 屏蔽sqlId
     */
    public String getSqlId() {
        return sqlId;
    }

    public PermissionRangeExcl setSqlId(String sqlId) {
        this.sqlId = sqlId;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public PermissionRangeExcl setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    @Override
    @JsonInclude
    public Date getCreationDate() {
        return super.getCreationDate();
    }

    @Override
    @JsonInclude
    public Long getCreatedBy() {
        return super.getCreatedBy();
    }

    @Override
    @JsonInclude
    public Date getLastUpdateDate() {
        return super.getLastUpdateDate();
    }

    @Override
    @JsonInclude
    public Long getLastUpdatedBy() {
        return super.getLastUpdatedBy();
    }
}
