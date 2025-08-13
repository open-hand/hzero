package org.hzero.report.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.mybatis.annotation.Unique;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.Date;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 标签权限
 *
 * @author fanghan.liu@hand-china.com 2019-12-02 10:27:44
 */
@ApiModel("标签权限")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hrpt_label_permission")
public class LabelPermission extends AuditDomain {

    public static final String FIELD_PERMISSION_ID = "permissionId";
    public static final String FIELD_LABEL_TEMPLATE_ID = "labelTemplateId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_ROLE_ID = "roleId";
    public static final String FIELD_START_DATE = "startDate";
    public static final String FIELD_END_DATE = "endDate";
    public static final String FIELD_REMARK = "remark";

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
    private Long permissionId;
    @ApiModelProperty(value = "标签模板ID，hrpt_label_template.label_template_id", required = true)
    @NotNull
    @Unique
    @Encrypt
    private Long labelTemplateId;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id", required = true)
    @NotNull
    @Unique
    private Long tenantId;
    @ApiModelProperty(value = "角色ID，iam_role.role_id")
    @Unique
    @Encrypt
    private Long roleId;
    @ApiModelProperty(value = "有效期从")
    private Date startDate;
    @ApiModelProperty(value = "有效期至")
    private Date endDate;
    @ApiModelProperty(value = "备注说明")
    private String remark;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String tenantName;
    @Transient
    private String roleName;

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getPermissionId() {
        return permissionId;
    }

    public LabelPermission setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
        return this;
    }

    public Long getLabelTemplateId() {
        return labelTemplateId;
    }

    public LabelPermission setLabelTemplateId(Long labelTemplateId) {
        this.labelTemplateId = labelTemplateId;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public LabelPermission setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Long getRoleId() {
        return roleId;
    }

    public LabelPermission setRoleId(Long roleId) {
        this.roleId = roleId;
        return this;
    }

    public Date getStartDate() {
        return startDate;
    }

    public LabelPermission setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public Date getEndDate() {
        return endDate;
    }

    public LabelPermission setEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public LabelPermission setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public LabelPermission setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public String getRoleName() {
        return roleName;
    }

    public LabelPermission setRoleName(String roleName) {
        this.roleName = roleName;
        return this;
    }
}
