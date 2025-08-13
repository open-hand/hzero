package org.hzero.message.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Regexs;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.Objects;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 接收者明细
 *
 * @author fanghan.liu@hand-china.com 2020-07-01 13:38:29
 */
@ApiModel("接收者明细")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hmsg_receiver_detail")
public class ReceiverDetail extends AuditDomain {

    public static final String FIELD_RECEIVER_DETAIL_ID = "receiverDetailId";
    public static final String FIELD_RECEIVER_TYPE_ID = "receiverTypeId";
    public static final String FIELD_ACCOUNT_TYPE_CODE = "accountTypeCode";
    public static final String FIELD_ACCOUNT_NUM = "accountNum";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    public void validAccountNum() {
        if (HmsgConstant.ReceiverAccountType.EMAIL.equals(accountTypeCode)) {
            if (!Regexs.isEmail(accountNum)) {
                throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
            }
        } else if (HmsgConstant.ReceiverAccountType.PHONE.equals(accountTypeCode)) {
            if (!Regexs.isMobile(accountNum)) {
                throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
            }
        }
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long receiverDetailId;
    @ApiModelProperty(value = "接收者ID，hmsg_receiver_type.receiver_type_id", required = true)
    @NotNull
    @Encrypt
    private Long receiverTypeId;
    @ApiModelProperty(value = "账户类型，值集：HMSG.RECEIVER.ACCOUNT_TYPE", required = true)
    @NotBlank
    @LovValue(lovCode = "HMSG.RECEIVER.ACCOUNT_TYPE")
    private String accountTypeCode;
    @ApiModelProperty(value = "账户", required = true)
    @NotBlank
    private String accountNum;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "租户ID", required = true)
    @NotNull
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String accountTypeMeaning;

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getReceiverDetailId() {
        return receiverDetailId;
    }

    public ReceiverDetail setReceiverDetailId(Long receiverDetailId) {
        this.receiverDetailId = receiverDetailId;
        return this;
    }

    public Long getReceiverTypeId() {
        return receiverTypeId;
    }

    public ReceiverDetail setReceiverTypeId(Long receiverTypeId) {
        this.receiverTypeId = receiverTypeId;
        return this;
    }

    public String getAccountTypeCode() {
        return accountTypeCode;
    }

    public ReceiverDetail setAccountTypeCode(String accountTypeCode) {
        this.accountTypeCode = accountTypeCode;
        return this;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public ReceiverDetail setAccountNum(String accountNum) {
        this.accountNum = accountNum;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ReceiverDetail setDescription(String description) {
        this.description = description;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public ReceiverDetail setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getAccountTypeMeaning() {
        return accountTypeMeaning;
    }

    public ReceiverDetail setAccountTypeMeaning(String accountTypeMeaning) {
        this.accountTypeMeaning = accountTypeMeaning;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReceiverDetail)) {
            return false;
        }
        ReceiverDetail that = (ReceiverDetail) o;
        return Objects.equals(receiverDetailId, that.receiverDetailId) &&
                Objects.equals(receiverTypeId, that.receiverTypeId) &&
                Objects.equals(accountTypeCode, that.accountTypeCode) &&
                Objects.equals(accountNum, that.accountNum) &&
                Objects.equals(description, that.description) &&
                Objects.equals(tenantId, that.tenantId) &&
                Objects.equals(accountTypeMeaning, that.accountTypeMeaning);
    }

    @Override
    public int hashCode() {
        return Objects.hash(receiverDetailId, receiverTypeId, accountTypeCode, accountNum, description, tenantId, accountTypeMeaning);
    }
}
