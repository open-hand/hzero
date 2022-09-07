package org.hzero.message.domain.entity;

import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 消息事务
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
@VersionAudit
@ModifyAudit
@Table(name = "hmsg_message_transaction")
public class MessageTransaction extends AuditDomain {

    public static final String FIELD_TRANSACTION_ID = "transactionId";
    public static final String FIELD_MESSAGE_ID = "messageId";
    public static final String FIELD_TRX_STATUS_CODE = "trxStatusCode";
    public static final String FIELD_TRANSACTION_MESSAGE = "transactionMessage";
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
    private Long transactionId;
    @NotNull
    @Encrypt
    private Long messageId;
    @NotBlank
    @Pattern(regexp = org.hzero.core.util.Regexs.CODE)
    private String trxStatusCode;
    private String transactionMessage;
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
    public Long getTransactionId() {
        return transactionId;
    }

    public MessageTransaction setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    /**
     * @return 消息ID，hmsg_message.message_id
     */
    public Long getMessageId() {
        return messageId;
    }

    public MessageTransaction setMessageId(Long messageId) {
        this.messageId = messageId;
        return this;
    }

    /**
     * @return 事务状态，值集：HMSG.TRANSACTION_STATUS P:就绪 S:成功 F:失败
     */
    public String getTrxStatusCode() {
        return trxStatusCode;
    }

    public MessageTransaction setTrxStatusCode(String trxStatusCode) {
        this.trxStatusCode = trxStatusCode;
        return this;
    }

    /**
     * @return 事务消息
     */
    public String getTransactionMessage() {
        return transactionMessage;
    }

    public MessageTransaction setTransactionMessage(String transactionMessage) {
        this.transactionMessage = transactionMessage;
        return this;
    }

    /**
     * @return 租户ID
     */
    public Long getTenantId() {
        return tenantId;
    }

    public MessageTransaction setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }
}
