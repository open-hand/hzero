package org.hzero.message.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 消息接受方
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
@VersionAudit
@ModifyAudit
@Table(name = "hmsg_message_receiver")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageReceiver extends AuditDomain {

    public static final String FIELD_RECEIVER_ID = "receiverId";
    public static final String FIELD_MESSAGE_ID = "messageId";
    public static final String FIELD_IDD = "idd";
    public static final String FIELD_RECEIVER_ADDRESS = "receiverAddress";
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
    private Long receiverId;
    @NotNull
    @Encrypt
    private Long messageId;
    private String idd;
    @NotBlank
    private String receiverAddress;
    @NotNull
    private Long tenantId;
    private Integer filterFlag;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String realName;
    @Transient
    private String loginName;
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    /**
     * @return 消息ID，hmsg_message.message_id
     */
    public Long getMessageId() {
        return messageId;
    }

    public MessageReceiver setMessageId(Long messageId) {
        this.messageId = messageId;
        return this;
    }

    /**
     * @return 国际冠码
     */
    public String getIdd() {
        return idd;
    }

    public MessageReceiver setIdd(String idd) {
        this.idd = idd;
        return this;
    }

    /**
     * @return 接收方地址，邮箱/手机号/用户ID
     */
    public String getReceiverAddress() {
        return receiverAddress;
    }

    public MessageReceiver setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
        return this;
    }

    /**
     * @return 租户ID
     */
    public Long getTenantId() {
        return tenantId;
    }

    public MessageReceiver setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getRealName() {
        return realName;
    }

    public MessageReceiver setRealName(String realName) {
        this.realName = realName;
        return this;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Integer getFilterFlag() {
        return filterFlag;
    }

    public MessageReceiver setFilterFlag(Integer filterFlag) {
        this.filterFlag = filterFlag;
        return this;
    }

    @Override
    public String toString() {
        return "MessageReceiver{" +
                "receiverId=" + receiverId +
                ", messageId=" + messageId +
                ", idd='" + idd + '\'' +
                ", receiverAddress='" + receiverAddress + '\'' +
                ", tenantId=" + tenantId +
                ", filterFlag=" + filterFlag +
                ", realName='" + realName + '\'' +
                ", loginName='" + loginName + '\'' +
                "} " + super.toString();
    }
}
