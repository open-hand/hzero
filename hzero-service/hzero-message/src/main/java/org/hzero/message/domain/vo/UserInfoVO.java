package org.hzero.message.domain.vo;

/**
 * <p>
 * 用户接收人配置 VO
 * </p>
 *
 * @author qingsheng.chen 2019/3/20 星期三 20:54
 */
public class UserInfoVO {
    private Long id;
    private String email;
    private String phone;
    /**
     * 外部用户ID，企业微信&钉钉
     */
    private String openUserId;

    public Long getId() {
        return id;
    }

    public UserInfoVO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserInfoVO setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public UserInfoVO setPhone(String phone) {
        this.phone = phone;
        return this;
    }



    public String getOpenUserId() {
        return openUserId;
    }

    public UserInfoVO setOpenUserId(String openUserId) {
        this.openUserId = openUserId;
        return this;
    }

    @Override
    public String toString() {
        return "UserInfoVO{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", openUserId='" + openUserId + '\'' +
                '}';
    }
}
