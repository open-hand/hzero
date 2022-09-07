package org.hzero.starter.integrate.dto;

import java.util.List;

/**
 * hzero平台员工同步对象
 * @author zifeng.ding@hand-china.com 2020/01/13 10:39
 */
public class SyncUserDTO {

    private String userid;
    private String name;
    private String mobile;
    private String position;
    private Integer gender;
    private String email;
    private Integer enable;
    private List<Long> department;
    /**
     * 对象的同步操作，新增：create、更新：update、删除：delete
     */
    private String syncType;
    /**
     * 增量同步时是否已经把平台部门id映射成微信部门id标志，与department一一对应
     */
    private List<Boolean> isDepartIdsMap;

    public String getUserid() {
        return userid;
    }

    public SyncUserDTO setUserid(String userid) {
        this.userid = userid;
        return this;
    }

    public String getName() {
        return name;
    }

    public SyncUserDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getMobile() {
        return mobile;
    }

    public SyncUserDTO setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public String getPosition() {
        return position;
    }

    public SyncUserDTO setPosition(String position) {
        this.position = position;
        return this;
    }


    public Integer getGender() {
        return gender;
    }

    public SyncUserDTO setGender(Integer gender) {
        this.gender = gender;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public SyncUserDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public Integer getEnable() {
        return enable;
    }

    public SyncUserDTO setEnable(Integer enable) {
        this.enable = enable;
        return this;
    }

    public List<Long> getDepartment() {
        return department;
    }

    public SyncUserDTO setDepartment(List<Long> department) {
        this.department = department;
        return this;
    }

    public List<Boolean> getIsDepartIdsMap() {
        return isDepartIdsMap;
    }

    public SyncUserDTO setIsDepartIdsMap(List<Boolean> isDepartIdsMap) {
        this.isDepartIdsMap = isDepartIdsMap;
        return this;
    }

    public String getSyncType() {
        return syncType;
    }

    public SyncUserDTO setSyncType(String syncType) {
        this.syncType = syncType;
        return this;
    }

    @Override
    public String toString() {
        return "SyncUserDTO{" +
                "userid='" + userid + '\'' +
                ", name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                ", position='" + position + '\'' +
                ", gender=" + gender +
                ", email='" + email + '\'' +
                ", enable=" + enable +
                ", department=" + department +
                ", syncType='" + syncType + '\'' +
                ", isDepartIdsMap=" + isDepartIdsMap +
                '}';
    }
}