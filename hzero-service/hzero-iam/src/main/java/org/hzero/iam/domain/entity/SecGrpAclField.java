package org.hzero.iam.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 安全组字段权限
 *
 * @author xingxing.wu@hand-china.com 2019-10-28 10:00:59
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("安全组字段权限")
@VersionAudit
@ModifyAudit
@Table(name = "hiam_sec_grp_acl_field")
public class SecGrpAclField extends AuditDomain {
    public static final String ENCRYPT_KEY = "hiam_sec_grp_acl_field";
    public static final String FIELD_SEC_GRP_ACL_FIELD_ID = "secGrpAclFieldId";
    public static final String FIELD_SEC_GRP_ID = "secGrpId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_FIELD_ID = "fieldId";
    public static final String FIELD_PERMISSION_ID = "permissionId";
    public static final String FIELD_PERMISSION_TYPE = "permissionType";
    public static final String FIELD_PERMISSION_RULE = "permissionRule";
    public static final String FIELD_ASSIGN_TYPE_CODE = "assignTypeCode";
    public static final String FIELD_REMARK = "remark";

    public SecGrpAclField() {
    }

    public SecGrpAclField(Long secGrpId) {
        this.secGrpId = secGrpId;
    }


    public static SecGrpAclField initFrom(SecGrp secGrp, SecGrpAclField secGrpAclField) {
        SecGrpAclField field = new SecGrpAclField();
        field.setSecGrpId(secGrp.getSecGrpId());
        field.setTenantId(secGrp.getTenantId());
        field.setFieldId(secGrpAclField.getFieldId());
        field.setPermissionId(secGrpAclField.getPermissionId());
        field.setPermissionType(secGrpAclField.getPermissionType());
        field.setPermissionRule(secGrpAclField.getPermissionRule());
        field.setRemark(secGrpAclField.getRemark());
        return field;
    }

    @ApiModelProperty(value = "权限分配类型，Code：HAIM.SEC_GRP.ASSIGN_TYPE_CODE ([SELF/自己创建]、[PARENT/父类分配]、[SELF_PARENT/自己创建之后，父类也创建])")
    private String assignTypeCode;

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long secGrpAclFieldId;
    @ApiModelProperty(value = "安全组ID", required = true)
    @NotNull
    @Encrypt
    private Long secGrpId;
    @ApiModelProperty(value = "租户", required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "字段ID，hiam_field.field_id", required = true)
    @NotNull
    @Encrypt
    private Long fieldId;
    @ApiModelProperty(value = "API权限ID，iam_permission.id", required = true)
    @NotNull
    @Encrypt
    private Long permissionId;
    @ApiModelProperty(value = "权限类型，值集HIAM.FIELD.PERMISSION_TYPE[HIDDEN]", required = true)
    @NotBlank
    @LovValue(lovCode = "HIAM.FIELD.PERMISSION_TYPE", meaningField = "permissionTypeMeaning")
    private String permissionType;
    @ApiModelProperty(value = "权限规则（脱敏预留）")
    private String permissionRule;
    @ApiModelProperty(value = "备注说明")
    private String remark;

    @JsonIgnore
    public boolean isAutoAssign() {
        return !Constants.SecGrpAssignTypeCode.SELF.equals(this.assignTypeCode);
    }

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------
    @Transient
    private String fieldName;
    @Transient
    @LovValue(lovCode = "HIAM.FIELD.TYPE", meaningField = "fieldTypeMeaning")
    private String fieldType;
    @Transient
    private String fieldDescription;

    @Transient
    private String fieldTypeMeaning;
    @Transient
    private String permissionTypeMeaning;
    @Transient
    private String secGrpName;

    /**
     * 是否可删除标志 1-可删除 0-不可删除
     */
    @Transient
    private Integer deleteEnableFlag;
    /**
     * 用于标记处理是否被屏蔽  1-是 0-否
     */
    @Transient
    private Integer shieldFlag;
    /**
     * 创建角色ID
     */
    @Transient
    private Long roleId;
    /**
     * 创建角色父角色ID
     */
    @Transient
    private Long parentRoleId;


    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public String getSecGrpName() {
        return secGrpName;
    }

    public void setSecGrpName(String secGrpName) {
        this.secGrpName = secGrpName;
    }

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getSecGrpAclFieldId() {
        return secGrpAclFieldId;
    }

    public void setSecGrpAclFieldId(Long secGrpAclFieldId) {
        this.secGrpAclFieldId = secGrpAclFieldId;
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
     * @return 字段ID，hiam_field.field_id
     */
    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    /**
     * @return API权限ID，iam_permission.id
     */
    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    /**
     * @return 权限类型，值集HIAM.FIELD.PERMISSION_TYPE[HIDDEN]
     */
    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }

    /**
     * @return 权限规则（脱敏预留）
     */
    public String getPermissionRule() {
        return permissionRule;
    }

    public void setPermissionRule(String permissionRule) {
        this.permissionRule = permissionRule;
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

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldDescription() {
        return fieldDescription;
    }

    public void setFieldDescription(String fieldDescription) {
        this.fieldDescription = fieldDescription;
    }

    public String getFieldTypeMeaning() {
        return fieldTypeMeaning;
    }

    public void setFieldTypeMeaning(String fieldTypeMeaning) {
        this.fieldTypeMeaning = fieldTypeMeaning;
    }

    public String getPermissionTypeMeaning() {
        return permissionTypeMeaning;
    }

    public void setPermissionTypeMeaning(String permissionTypeMeaning) {
        this.permissionTypeMeaning = permissionTypeMeaning;
    }

    public String getAssignTypeCode() {
        return assignTypeCode;
    }

    public void setAssignTypeCode(String assignTypeCode) {
        this.assignTypeCode = assignTypeCode;
    }

    public Integer getDeleteEnableFlag() {
        return deleteEnableFlag;
    }

    public void setDeleteEnableFlag(Integer deleteEnableFlag) {
        this.deleteEnableFlag = deleteEnableFlag;
    }

    public Integer getShieldFlag() {
        return shieldFlag;
    }

    public void setShieldFlag(Integer shieldFlag) {
        this.shieldFlag = shieldFlag;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getParentRoleId() {
        return parentRoleId;
    }

    public void setParentRoleId(Long parentRoleId) {
        this.parentRoleId = parentRoleId;
    }
}
