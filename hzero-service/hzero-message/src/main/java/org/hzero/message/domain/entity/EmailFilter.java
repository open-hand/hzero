package org.hzero.message.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hzero.mybatis.common.query.Comparison;
import org.hzero.mybatis.common.query.Where;
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
 * 邮箱账户黑白名单
 *
 * @author shuangfei.zhu@hand-china.com 2019-06-04 14:23:17
 */
@ApiModel("邮箱账户黑白名单")
@VersionAudit
@ModifyAudit
@Table(name = "hmsg_email_filter")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailFilter extends AuditDomain {

    public static final String FIELD_EMAIL_FILTER_ID = "emailFilterId";
    public static final String FIELD_SERVER_ID = "serverId";
    public static final String FIELD_ADDRESS = "address";



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
    private Long emailFilterId;
    @ApiModelProperty(value = "邮箱账户id，hmsg_email_server.server_id", required = true)
    @NotNull
    @Where
    @Encrypt
    private Long serverId;
    @ApiModelProperty(value = "地址", required = true)
    @NotBlank
    @Length(max = 30)
    @Where(comparison = Comparison.LIKE)
    private String address;
    @ApiModelProperty(value = "租户ID")
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    public Long getEmailFilterId() {
        return emailFilterId;
    }

    public EmailFilter setEmailFilterId(Long emailFilterId) {
        this.emailFilterId = emailFilterId;
        return this;
    }

    public Long getServerId() {
        return serverId;
    }

    public EmailFilter setServerId(Long serverId) {
        this.serverId = serverId;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public EmailFilter setAddress(String address) {
        this.address = address;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public EmailFilter setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }
}
