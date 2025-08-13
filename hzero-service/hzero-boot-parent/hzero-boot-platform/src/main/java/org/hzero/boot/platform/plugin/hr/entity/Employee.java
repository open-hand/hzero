package org.hzero.boot.platform.plugin.hr.entity;

/**
 * @author qingsheng.chen@hand-china.com 2019-05-21 16:34
 */
public class Employee {
    private Long employeeId;
    private Long tenantId;
    private String employeeNum;
    private String name;
    private String nameEn;
    private String email;
    private String mobile;
    private Integer gender;
    private String cid;
    private String quickIndex;
    private String phoneticize;
    private String status;
    private Integer enabledFlag;

    public Long getEmployeeId() {
        return employeeId;
    }

    public Employee setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public Employee setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getEmployeeNum() {
        return employeeNum;
    }

    public Employee setEmployeeNum(String employeeNum) {
        this.employeeNum = employeeNum;
        return this;
    }

    public String getName() {
        return name;
    }

    public Employee setName(String name) {
        this.name = name;
        return this;
    }

    public Employee setEmployeeName(String employeeName) {
        this.name = employeeName;
        return this;
    }

    public String getNameEn() {
        return nameEn;
    }

    public Employee setNameEn(String nameEn) {
        this.nameEn = nameEn;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Employee setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getMobile() {
        return mobile;
    }

    public Employee setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public Integer getGender() {
        return gender;
    }

    public Employee setGender(Integer gender) {
        this.gender = gender;
        return this;
    }

    public String getCid() {
        return cid;
    }

    public Employee setCid(String cid) {
        this.cid = cid;
        return this;
    }

    public String getQuickIndex() {
        return quickIndex;
    }

    public Employee setQuickIndex(String quickIndex) {
        this.quickIndex = quickIndex;
        return this;
    }

    public String getPhoneticize() {
        return phoneticize;
    }

    public Employee setPhoneticize(String phoneticize) {
        this.phoneticize = phoneticize;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public Employee setStatus(String status) {
        this.status = status;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public Employee setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }
}
