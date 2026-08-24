package org.hzero.starter.social.wechat.enterprise.api;

import org.hzero.starter.social.core.common.api.SocialUser;

/**
 * WeChat Enterprise 用户信息
 *
 * @author wanshun.zhang@hand-china.com 2019/11/12
 */
public class WechatEnterpriseUser implements SocialUser {

    public static final String NAME = "name";
    public static final String AVATAR = "avatar";
    public static final String MOBILE = "mobile";

    /**
     * 返回码
     */
    private String errcode;
    /**
     * 对返回码的文本描述内容
     */
    private String errmsg;
    /**
     * 成员UserID。对应管理端的帐号，企业内必须唯一。不区分大小写，长度为1~64个字节
     */
    private String userid;
    /**
     * 成员名称
     */
    private String name;
    /**
     * 手机号码，第三方仅通讯录应用可获取
     */
    private String mobile;
    /**
     * 成员所属部门id列表，仅返回该应用有查看权限的部门id
     */
    private String department;
    /**
     * 部门内的排序值，默认为0。数量必须和department一致，数值越大排序越前面。值范围是[0, 2^32)
     */
    private String order;
    /**
     * 职务信息；第三方仅通讯录应用可获取
     */
    private String position;
    /**
     * 性别。0表示未定义，1表示男性，2表示女性
     */
    private String gender;
    /**
     * 邮箱，第三方仅通讯录应用可获取
     */
    private String email;
    /**
     * 头像url。注：如果要获取小图将url最后的”/0”改成”/100”即可。第三方仅通讯录应用可获取
     */
    private String avatar;
    /**
     * 成员启用状态。1表示启用的成员，0表示被禁用。注意，服务商调用接口不会返回此字段
     */
    private String enable;

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }
}
