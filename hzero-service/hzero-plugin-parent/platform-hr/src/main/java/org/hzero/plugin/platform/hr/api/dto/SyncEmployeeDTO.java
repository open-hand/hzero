package org.hzero.plugin.platform.hr.api.dto;

public class SyncEmployeeDTO {
    private String employeeNum;
    private String name;
    private String mobile;
    private String positionName;
    private long unitId;
    private String email;
    private int enabledFlag;
    private int gender;

    public String getEmployeeNum() {
        return this.employeeNum;
    }

    public void setEmployeeNum(String employeeNum) {
        this.employeeNum = employeeNum;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPositionName() {
        return this.positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public long getUnitId() {
        return this.unitId;
    }

    public void setUnitId(long unitId) {
        this.unitId = unitId;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getEnabledFlag() {
        return this.enabledFlag;
    }

    public void setEnabledFlag(int enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public int getGender() {
        return this.gender;
    }

    public SyncEmployeeDTO setGender(int gender) {
        this.gender = gender;
        return this;
    }
}
