package org.hzero.message.domain.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.Objects;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 公告接收记录
 *
 * @author minghui.qiu@hand-china.com 2019-06-10 16:18:09
 */
@ApiModel("公告接收记录")
@VersionAudit
@ModifyAudit
@Table(name = "hmsg_notice_receiver")
public class NoticeReceiver extends AuditDomain {

    public static final String FIELD_RECEIVER_ID = "receiverId";
    public static final String FIELD_PUBLISHED_ID = "publishedId";
    public static final String FIELD_RECEIVE_TYPE = "receiverTypeCode";
    public static final String FIELD_RECEIVER_SOURCE_ID = "receiverSourceId";
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
    @Encrypt
    private Long publishedId;
    @ApiModelProperty(value = "接收方类型,值集：HMSG.NOTICE.RECEIVER_RECORD_TYPE")
    @LovValue(lovCode = "HMSG.NOTICE.RECEIVER_RECORD_TYPE")
    private String receiverTypeCode;
    @NotNull
    @Encrypt
    private Long receiverSourceId;

    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------
    @Transient
    private String receiverTypeMeaning;
    @Transient
    private String receiverSourceName;
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return
     */
    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    /**
     * @return 公告发布记录hmsg_notice_published_record.published_id
     */
    public Long getPublishedId() {
        return publishedId;
    }

    public NoticeReceiver setPublishedId(Long publishedId) {
        this.publishedId = publishedId;
        return this;
    }

    /**
     * @return 租户ID
     */
    public Long getTenantId() {
        return tenantId;
    }

    public NoticeReceiver setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getReceiverTypeCode() {
        return receiverTypeCode;
    }

    public NoticeReceiver setReceiverTypeCode(String receiverTypeCode) {
        this.receiverTypeCode = receiverTypeCode;
        return this;
    }

    public Long getReceiverSourceId() {
        return receiverSourceId;
    }

    public NoticeReceiver setReceiverSourceId(Long receiverSourceId) {
        this.receiverSourceId = receiverSourceId;
        return this;
    }

    public String getReceiverSourceName() {
        return receiverSourceName;
    }

    public void setReceiverSourceName(String receiverSourceName) {
        this.receiverSourceName = receiverSourceName;
    }

    public String getReceiverTypeMeaning() {
        return receiverTypeMeaning;
    }

    public void setReceiverTypeMeaning(String receiverTypeMeaning) {
        this.receiverTypeMeaning = receiverTypeMeaning;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NoticeReceiver)) {
            return false;
        }
        NoticeReceiver that = (NoticeReceiver) o;
        return Objects.equals(receiverId, that.receiverId) &&
                Objects.equals(publishedId, that.publishedId) &&
                Objects.equals(receiverTypeCode, that.receiverTypeCode) &&
                Objects.equals(receiverSourceId, that.receiverSourceId) &&
                Objects.equals(tenantId, that.tenantId) &&
                Objects.equals(receiverTypeMeaning, that.receiverTypeMeaning) &&
                Objects.equals(receiverSourceName, that.receiverSourceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(receiverId, publishedId, receiverTypeCode, receiverSourceId, tenantId, receiverTypeMeaning, receiverSourceName);
    }
}
