package org.hzero.iam.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 单据类型分配
 *
 * @author min.wang01@hand-china.com 2018-08-08 19:55:53
 */
@ApiModel("单据类型分配")
@VersionAudit
@ModifyAudit
@Table(name = "hiam_doc_type_assign")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocTypeAssign extends AuditDomain {
    public static final String ENCRYPT_KEY = "hiam_doc_type_assign";
    public static final String FIELD_ASSIGN_ID = "assignId";
    public static final String FIELD_DOC_TYPE_ID = "docTypeId";
    public static final String FIELD_ASSIGN_VALUE_ID = "assignValueId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------
    public interface Insert{}
    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long assignId;
    @ApiModelProperty(value = "单据类型ID，HIAM_DOC_TYPE.DOC_TYPE_ID")
    @NotNull
    @Encrypt
    private Long docTypeId;
    @ApiModelProperty(value = "分配值ID，当前level_code=TENANT，则此处为租户ID，HPFM_TENANT.TENANT_ID")
    @NotNull
    private Long assignValueId;
    @ApiModelProperty(value = "租户ID")
    @NotNull(groups = Insert.class)
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String tenantNum;
    @Transient
    private String tenantName;
    @Transient
    private Integer actionType;
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getAssignId() {
        return assignId;
    }

    /**
     * @return 单据类型ID，HIAM_DOC_TYPE.DOC_TYPE_ID
     */
    public Long getDocTypeId() {
        return docTypeId;
    }

    /**
     * @return 分配值ID，当前level_code=TENANT，则此处为租户ID，HPFM_TENANT.TENANT_ID
     */
    public Long getAssignValueId() {
        return assignValueId;
    }

    /**
     * @return 租户代码
     */
    public String getTenantNum() {
        return tenantNum;
    }

    /**
     * @return 租户名称
     */
    public String getTenantName() {
        return tenantName;
    }


    /**
     * @return 动作类型 0/1 -> 0:删除；1:分配
     */
    public Integer getActionType() {
        return actionType;
    }

    public DocTypeAssign setAssignId(Long assignId) {
        this.assignId = assignId;
        return this;
    }

    public DocTypeAssign setDocTypeId(Long docTypeId) {
        this.docTypeId = docTypeId;
        return this;
    }

    public DocTypeAssign setAssignValueId(Long assignValueId) {
        this.assignValueId = assignValueId;
        return this;
    }

    public DocTypeAssign setTenantNum(String tenantNum) {
        this.tenantNum = tenantNum;
        return this;
    }

    public DocTypeAssign setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public DocTypeAssign setActionType(Integer actionType) {
        this.actionType = actionType;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public DocTypeAssign setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DocTypeAssign that = (DocTypeAssign) o;

        return new EqualsBuilder()
                .append(assignId, that.assignId)
                .append(docTypeId, that.docTypeId)
                .append(assignValueId, that.assignValueId)
                .append(tenantNum, that.tenantNum)
                .append(tenantName, that.tenantName)
                .append(actionType, that.actionType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(assignId)
                .append(docTypeId)
                .append(assignValueId)
                .append(tenantNum)
                .append(tenantName)
                .append(actionType)
                .toHashCode();
    }
}
