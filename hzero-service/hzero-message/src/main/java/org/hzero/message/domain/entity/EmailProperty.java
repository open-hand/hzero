package org.hzero.message.domain.entity;

import org.hibernate.validator.constraints.Length;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 邮箱属性
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
@VersionAudit
@ModifyAudit
@Table(name = "hmsg_email_property")
public class EmailProperty extends AuditDomain {

    public static final String FIELD_PROPERTY_ID = "propertyId";
    public static final String FIELD_SERVER_ID = "serverId";
    public static final String FIELD_PROPERTY_CODE = "propertyCode";
    public static final String FIELD_PROPERTY_VALUE = "propertyValue";
    public static final String FIELD_TENANT_ID = "tenantId";



    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @Id
    @GeneratedValue
    @Encrypt
    private Long propertyId;
    @Encrypt
    private Long serverId;
    @NotBlank
    @Length(max = 240)
    private String propertyCode;
    @NotBlank
    @Length(max = 60)
    private String propertyValue;
    @NotNull
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    /**
     * @return 邮箱服务ID，hmsg_email_server.server_id
     */
    public Long getServerId() {
        return serverId;
    }

    public EmailProperty setServerId(Long serverId) {
        this.serverId = serverId;
        return this;
    }

    /**
     * @return 属性代码
     */
    public String getPropertyCode() {
        return propertyCode;
    }

    public void setPropertyCode(String propertyCode) {
        this.propertyCode = propertyCode;
    }

    /**
     * @return 属性值
     */
    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    /**
     * @return 租户ID
     */
    public Long getTenantId() {
        return tenantId;
    }

    public EmailProperty setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }
}
