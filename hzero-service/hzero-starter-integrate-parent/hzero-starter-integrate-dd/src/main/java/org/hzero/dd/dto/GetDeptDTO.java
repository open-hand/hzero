package org.hzero.dd.dto;

public class GetDeptDTO extends DefaultResultDTO {
    /**
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "id": 2,
     *     "name": "xxx",
     *     "order" : 10,
     *     "parentid": 1,
     *     "createDeptGroup": true,
     *     "autoAddUser": true,
     *     "deptHiding" : true,
     *     "deptPermits" : "3|4",
     *     "userPermits" : "userid1|userid2",
     *     "outerDept" : true,
     *     "outerPermitDepts" : "1|2",
     *     "outerPermitUsers" : "userid3|userid4",
     *     "orgDeptOwner" : "manager1122",
     *     "deptManagerUseridList" : "manager1122|manager3211",
     *     "sourceIdentifier" : "source"
     */
    private Long id;
    private String name;
    private Long parentid;
    private Long order;
    private boolean createDeptGroup;
    private boolean autoAddUser;
    private boolean deptHiding;
    private String deptPermits;

    private String userPermits;
    private boolean outerDept;
    private String outerPermitDepts;
    private String outerPermitUsers;
    private String orgDeptOwner;
    private String deptManagerUseridList;
    private String sourceIdentifier;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentid() {
        return parentid;
    }

    public void setParentid(Long parentid) {
        this.parentid = parentid;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public boolean isCreateDeptGroup() {
        return createDeptGroup;
    }

    public void setCreateDeptGroup(boolean createDeptGroup) {
        this.createDeptGroup = createDeptGroup;
    }

    public boolean isAutoAddUser() {
        return autoAddUser;
    }

    public void setAutoAddUser(boolean autoAddUser) {
        this.autoAddUser = autoAddUser;
    }

    public boolean isDeptHiding() {
        return deptHiding;
    }

    public void setDeptHiding(boolean deptHiding) {
        this.deptHiding = deptHiding;
    }

    public String getDeptPermits() {
        return deptPermits;
    }

    public void setDeptPermits(String deptPermits) {
        this.deptPermits = deptPermits;
    }

    public String getUserPermits() {
        return userPermits;
    }

    public void setUserPermits(String userPermits) {
        this.userPermits = userPermits;
    }

    public boolean isOuterDept() {
        return outerDept;
    }

    public void setOuterDept(boolean outerDept) {
        this.outerDept = outerDept;
    }

    public String getOuterPermitDepts() {
        return outerPermitDepts;
    }

    public void setOuterPermitDepts(String outerPermitDepts) {
        this.outerPermitDepts = outerPermitDepts;
    }

    public String getOuterPermitUsers() {
        return outerPermitUsers;
    }

    public void setOuterPermitUsers(String outerPermitUsers) {
        this.outerPermitUsers = outerPermitUsers;
    }

    public String getOrgDeptOwner() {
        return orgDeptOwner;
    }

    public void setOrgDeptOwner(String orgDeptOwner) {
        this.orgDeptOwner = orgDeptOwner;
    }

    public String getDeptManagerUseridList() {
        return deptManagerUseridList;
    }

    public void setDeptManagerUseridList(String deptManagerUseridList) {
        this.deptManagerUseridList = deptManagerUseridList;
    }

    public String getSourceIdentifier() {
        return sourceIdentifier;
    }

    public void setSourceIdentifier(String sourceIdentifier) {
        this.sourceIdentifier = sourceIdentifier;
    }
}
