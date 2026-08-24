package org.hzero.plugin.platform.hr.api.dto;

import java.util.Map;
import java.util.Objects;

/**
 * 消息接收参数
 *
 * @author xiaoyu.zhao@hand-china.com 2019/06/12 10:35
 */
public class Receiver {

    /**
     * 站内消息接收者：用户ID
     */
    private Long userId;
    /**
     * 目标用户ID
     */
    private Long targetUserTenantId;
    /**
     * 邮箱消息接收者：邮箱
     */
    private String email;
    /**
     * 短信接收者：电话号码
     */
    private String phone;
    /**
     * 短信接收者：国际冠码
     */
    private String idd;

    /**
     * 外部用户ID，钉钉&企业微信
     */
    private String openUserId;

    private Map<String, Object> additionInfo;

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
        if (getUserId() != null) {
            return getUserId().equals(receiver.getUserId());
        }
        return getOpenUserId().equals(receiver.getOpenUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId());
    }

    @Override
    public String toString() {
        return "Receiver{" + "userId=" + userId + ", targetUserTenantId=" + targetUserTenantId + ", email='" + email
                + '\'' + ", phone='" + phone + '\'' + ", idd='" + idd + '\'' + ", additionInfo=" + additionInfo
                + '\'' + ",openUserId='" + openUserId + '\'' + '}';
    }

}
