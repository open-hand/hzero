package org.hzero.boot.message.entity;

import java.util.Map;
import javax.validation.constraints.NotNull;

import com.google.common.base.Objects;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * <p>
 * 接收者
 * </p>
 *
 * @author qingsheng.chen 2018/8/6 星期一 21:01
 */
public class Receiver implements Cloneable {

    public Receiver() {
    }

    public Receiver(Receiver receiver) {
        this.userId = receiver.getUserId();
        this.targetUserTenantId = receiver.getTargetUserTenantId();
        this.email = receiver.getEmail();
        this.idd = receiver.getIdd();
        this.phone = receiver.getPhone();
        this.openUserId = receiver.getOpenUserId();
        this.additionInfo = receiver.getAdditionInfo();
    }

    /**
     * 站内消息接收者：用户ID
     */
    @NotNull(groups = MessageSender.WebMessage.class)
    @Encrypt
    private Long userId;
    /**
     * 目标用户ID
     */
    @NotNull(groups = MessageSender.WebMessage.class)
    private Long targetUserTenantId;
    /**
     * 邮箱消息接收者：邮箱
     */
    @NotNull(groups = MessageSender.Email.class)
    private String email;
    /**
     * 短信/语音消息接收者：电话号码
     */
    @NotNull(groups = {MessageSender.Sms.class, MessageSender.Call.class})
    private String phone;
    /**
     * 短信/语音消息接收者：国际冠码
     */
    @NotNull(groups = {MessageSender.Sms.class, MessageSender.Call.class})
    private String idd = "+86";

    private Map<String, Object> additionInfo;

    /**
     * 外部用户ID，钉钉&企业微信
     */
    private String openUserId;

    public Long getUserId() {
        return userId;
    }

    public Receiver setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Long getTargetUserTenantId() {
        return targetUserTenantId;
    }

    public Receiver setTargetUserTenantId(Long targetUserTenantId) {
        this.targetUserTenantId = targetUserTenantId;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Receiver setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public Receiver setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public Map<String, Object> getAdditionInfo() {
        return additionInfo;
    }

    public Receiver setAdditionInfo(Map<String, Object> additionInfo) {
        this.additionInfo = additionInfo;
        return this;
    }

    public String getIdd() {
        return idd;
    }

    public Receiver setIdd(String idd) {
        this.idd = idd;
        return this;
    }

    public String getOpenUserId() {
        return openUserId;
    }

    public Receiver setOpenUserId(String openUserId) {
        this.openUserId = openUserId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Receiver)) {
            return false;
        }
        Receiver receiver = (Receiver) o;
        return Objects.equal(userId, receiver.userId) &&
                Objects.equal(targetUserTenantId, receiver.targetUserTenantId) &&
                Objects.equal(email, receiver.email) &&
                Objects.equal(phone, receiver.phone) &&
                Objects.equal(idd, receiver.idd) &&
                Objects.equal(additionInfo, receiver.additionInfo) &&
                Objects.equal(openUserId, receiver.openUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId, targetUserTenantId, email, phone, idd, additionInfo);
    }

    @Override
    public String toString() {
        return "Receiver{" +
                "userId=" + userId +
                ", targetUserTenantId=" + targetUserTenantId +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", idd='" + idd + '\'' +
                ", additionInfo=" + additionInfo +
                ", openUserId=" + openUserId +
                '}';
    }
}
