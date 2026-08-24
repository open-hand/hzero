package org.hzero.dd.dto;

import java.util.List;
import java.util.Map;

/**
 * @Author J
 * @Date 2019/9/2
 */
public class DingSyncUserDTO {


    /**
     * userid : zhangsan
     * name : 张三
     * orderInDepts : {1:10, 2:20}
     * department : [1,2]
     * position : 产品经理
     * mobile : 1xxxxxxxxxx
     * tel : xxxx-xxxxxxxx
     * workPlace :
     * remark :
     * email : test@xxx.com
     * orgEmail : test@xxx.com
     * jobnumber : xxx
     * isHide : false
     * isSenior : false
     * extattr : {"爱好":"旅游","年龄":"24"}
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
    /**
     * 对象的同步操作，新增：create、更新：update、删除：delete
     */
    private String syncType;

    /**
     * 增量同步时是否已经把平台部门id映射成钉钉部门id标志，与department一一对应
     */
    private List<Boolean> isDepartIdsMap;

    public String getUserid() {
        return userid;
    }

    public DingSyncUserDTO setUserid(String userid) {
        this.userid = userid;
        return this;
    }

    public String getName() {
        return name;
    }

    public DingSyncUserDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getOrderInDepts() {
        return orderInDepts;
    }

    public DingSyncUserDTO setOrderInDepts(String orderInDepts) {
        this.orderInDepts = orderInDepts;
        return this;
    }

    public String getPosition() {
        return position;
    }

    public DingSyncUserDTO setPosition(String position) {
        this.position = position;
        return this;
    }

    public String getMobile() {
        return mobile;
    }

    public DingSyncUserDTO setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public String getTel() {
        return tel;
    }

    public DingSyncUserDTO setTel(String tel) {
        this.tel = tel;
        return this;
    }

    public String getWorkPlace() {
        return workPlace;
    }

    public DingSyncUserDTO setWorkPlace(String workPlace) {
        this.workPlace = workPlace;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public DingSyncUserDTO setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public DingSyncUserDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getOrgEmail() {
        return orgEmail;
    }

    public DingSyncUserDTO setOrgEmail(String orgEmail) {
        this.orgEmail = orgEmail;
        return this;
    }

    public String getJobnumber() {
        return jobnumber;
    }

    public DingSyncUserDTO setJobnumber(String jobnumber) {
        this.jobnumber = jobnumber;
        return this;
    }

    public boolean isHide() {
        return isHide;
    }

    public DingSyncUserDTO setHide(boolean hide) {
        isHide = hide;
        return this;
    }

    public boolean isSenior() {
        return isSenior;
    }

    public DingSyncUserDTO setSenior(boolean senior) {
        isSenior = senior;
        return this;
    }

    public Map<String, String> getExtattr() {
        return extattr;
    }

    public DingSyncUserDTO setExtattr(Map<String, String> extattr) {
        this.extattr = extattr;
        return this;
    }

    public List<Long> getDepartment() {
        return department;
    }

    public DingSyncUserDTO setDepartment(List<Long> department) {
        this.department = department;
        return this;
    }

    public String getSyncType() {
        return syncType;
    }

    public DingSyncUserDTO setSyncType(String syncType) {
        this.syncType = syncType;
        return this;
    }

    public List<Boolean> getIsDepartIdsMap() {
        return isDepartIdsMap;
    }

    public DingSyncUserDTO setIsDepartIdsMap(List<Boolean> isDepartIdsMap) {
        this.isDepartIdsMap = isDepartIdsMap;
        return this;
    }

    @Override
    public String toString() {
        return "DingSyncUserDTO{" +
                "userid='" + userid + '\'' +
                ", name='" + name + '\'' +
                ", orderInDepts='" + orderInDepts + '\'' +
                ", position='" + position + '\'' +
                ", mobile='" + mobile + '\'' +
                ", tel='" + tel + '\'' +
                ", workPlace='" + workPlace + '\'' +
                ", remark='" + remark + '\'' +
                ", email='" + email + '\'' +
                ", orgEmail='" + orgEmail + '\'' +
                ", jobnumber='" + jobnumber + '\'' +
                ", isHide=" + isHide +
                ", isSenior=" + isSenior +
                ", extattr=" + extattr +
                ", department=" + department +
                ", syncType='" + syncType + '\'' +
                ", isDepartIdsMap=" + isDepartIdsMap +
                '}';
    }
}
