package org.hzero.scheduler.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;
import org.hzero.core.base.BaseConstants;
import org.hzero.scheduler.domain.repository.ConcPermissionRepository;
import org.hzero.scheduler.infra.constant.HsdrErrorCode;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 并发请求权限
 *
 * @author shuangfei.zhu@hand-china.com 2019-01-31 09:47:02
 */
@ApiModel("并发请求权限")
@VersionAudit
@ModifyAudit
@Table(name = "hsdr_conc_permission")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConcPermission extends AuditDomain {

    public static final String FIELD_PERMISSION_ID = "permissionId";
    public static final String FIELD_CONCURRENT_ID = "concurrentId";
    public static final String FIELD_LIMIT_QUANTITY = "limitQuantity";
    public static final String FIELD_ROLE_ID = "roleId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_START_DATE = "startDate";
    public static final String FIELD_END_DATE = "endDate";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 唯一性校验
     */
    public void validate(ConcPermissionRepository repository) {
        Assert.isTrue(repository.selectCount(new ConcPermission().setConcurrentId(concurrentId).setTenantId(tenantId).setRoleId(roleId)) == BaseConstants.Digital.ZERO, HsdrErrorCode.PERMISSION_TENANT_REPEAT);
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long permissionId;
    @ApiModelProperty(value = "并发程序ID,hsdr_concurrent.concurrent_id")
    @NotNull(groups = {Validate.class})
    @Encrypt
    private Long concurrentId;
    @ApiModelProperty(value = "限制次数")
    private Integer limitQuantity;
    @ApiModelProperty(value = "角色ID，iam_role.role_id")
    @Encrypt
    private Long roleId;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id")
    @NotNull(groups = {Validate.class})
    private Long tenantId;
    @ApiModelProperty(value = "有效期从")
    private LocalDate startDate;
    @ApiModelProperty(value = "有效期至")
    private LocalDate endDate;
    @ApiModelProperty(value = "启用标识")
    @NotNull(groups = {Validate.class})
    @Range(max = 1)
    private Integer enabledFlag;

    public interface Validate {
    }

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String tenantName;
    @Transient
    private String roleName;
    @Transient
    private String concCode;
    @Transient
    private String concName;

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getPermissionId() {
        return permissionId;
    }

    public ConcPermission setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
        return this;
    }

    public Long getConcurrentId() {
        return concurrentId;
    }

    public ConcPermission setConcurrentId(Long concurrentId) {
        this.concurrentId = concurrentId;
        return this;
    }

    public Integer getLimitQuantity() {
        return limitQuantity;
    }

    public ConcPermission setLimitQuantity(Integer limitQuantity) {
        this.limitQuantity = limitQuantity;
        return this;
    }

    public Long getRoleId() {
        return roleId;
    }

    public ConcPermission setRoleId(Long roleId) {
        this.roleId = roleId;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public ConcPermission setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public ConcPermission setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public ConcPermission setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public ConcPermission setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public ConcPermission setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public String getRoleName() {
        return roleName;
    }

    public ConcPermission setRoleName(String roleName) {
        this.roleName = roleName;
        return this;
    }

    public String getConcCode() {
        return concCode;
    }

    public ConcPermission setConcCode(String concCode) {
        this.concCode = concCode;
        return this;
    }

    public String getConcName() {
        return concName;
    }

    public ConcPermission setConcName(String concName) {
        this.concName = concName;
        return this;
    }
}
