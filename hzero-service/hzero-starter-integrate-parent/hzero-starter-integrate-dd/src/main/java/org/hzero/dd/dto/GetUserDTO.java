package org.hzero.dd.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class GetUserDTO extends DefaultResultDTO{
    /**
     * "errcode": 0,
     *     "unionid": "PiiiPyQqBNBii0HnCJ3zljcuAiEiE",
     *     "remark": "remark",
     *     "userid": "zhangsan",
     *     "isLeaderInDepts": "{1:false}",
     *     "isBoss": false,
     *     "hiredDate": 1520265600000,
     *     "isSenior": false,
     *     "tel": "xxx-xxxxxxxx",
     *     "department": [1,2],
     *     "workPlace": "place",
     *     "email": "test@xxx.com",
     *     "orderInDepts": "{1:71738366882504}",
     *     "mobile": "1xxxxxxxxxx",
     *     "errmsg": "ok",
     *     "active": false,
     *     "avatar": "xxx",
     *     "isAdmin": false,
     *     "isHide": false,
     *     "jobnumber": "001",
     *     "name": "张三",
     *     "extattr": {},
     *     "stateCode": "86",
     *     "position": "manager",
     *     "roles": [
     *         {
     *             "id": 149507744,
     *             "name": "总监",
     *             "groupName": "职务"
     *         }
     *     ]
     */
    private String userid;
    private String name;
    private String orderInDepts;
    private String position;
    private String mobile;
    private String tel;
    private String workPlace;
    private String remark;
    private String email;
    private String orgEmail;
    private String jobnumber;
    private boolean isHide;
    private boolean isSenior;
    private Map<String,String> extattr;
    private List<Long> department;
    private String unionid;
    private String isLeaderInDepts;
    private Boolean isBoss;
    private Date hiredDate;

    private String active;
    private String avatar;
    private Boolean isAdmin;
    private String stateCode;
    private List<Roles> roles;

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

    public String getOrderInDepts() {
        return orderInDepts;
    }

    public void setOrderInDepts(String orderInDepts) {
        this.orderInDepts = orderInDepts;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getWorkPlace() {
        return workPlace;
    }

    public void setWorkPlace(String workPlace) {
        this.workPlace = workPlace;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrgEmail() {
        return orgEmail;
    }

    public void setOrgEmail(String orgEmail) {
        this.orgEmail = orgEmail;
    }

    public String getJobnumber() {
        return jobnumber;
    }

    public void setJobnumber(String jobnumber) {
        this.jobnumber = jobnumber;
    }

    public boolean isHide() {
        return isHide;
    }

    public void setHide(boolean hide) {
        isHide = hide;
    }

    public boolean isSenior() {
        return isSenior;
    }

    public void setSenior(boolean senior) {
        isSenior = senior;
    }

    public Map<String, String> getExtattr() {
        return extattr;
    }

    public void setExtattr(Map<String, String> extattr) {
        this.extattr = extattr;
    }

    public List<Long> getDepartment() {
        return department;
    }

    public void setDepartment(List<Long> department) {
        this.department = department;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getIsLeaderInDepts() {
        return isLeaderInDepts;
    }

    public void setIsLeaderInDepts(String isLeaderInDepts) {
        this.isLeaderInDepts = isLeaderInDepts;
    }

    public Boolean getBoss() {
        return isBoss;
    }

    public void setBoss(Boolean boss) {
        isBoss = boss;
    }

    public Date getHiredDate() {
        return hiredDate;
    }

    public void setHiredDate(Date hiredDate) {
        this.hiredDate = hiredDate;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public List<Roles> getRoles() {
        return roles;
    }

    public void setRoles(List<Roles> roles) {
        this.roles = roles;
    }
}
