package org.hzero.dd.dto;

/**
 * @Author J
 * @Date 2019/8/28
 */
public class DingSyncDeptDTO {

    /**
     * id : 2
     * name : 钉钉事业部
     * parentid : 1
     * order : 1
     * createDeptGroup : true
     * deptHiding : true
     * deptPermits : 3|4
     * userPermits : userid1|userid2
     * outerDept : true
     * outerPermitDepts : 1|2
     * outerPermitUsers : userid3|userid4
     * sourceIdentifier : source
     */


    private Long id;
    private String name;
    private Long parentid;
    private Long order;
    private boolean createDeptGroup;
    private boolean deptHiding;
    private String deptPermits;
    private String userPermits;
    private boolean outerDept;
    private String outerPermitDepts;
    private String outerPermitUsers;
    private String sourceIdentifier;
    /**
     * 对象的同步操作，新增：create、更新：update、删除：delete
     */
    private String syncType;
    /**
     * hzero平台部门的父部门id
     */
    private Long parentUnitId;

    public Long getId() {
        return id;
    }

    public DingSyncDeptDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public DingSyncDeptDTO setName(String name) {
        this.name = name;
        return this;
    }

    public Long getParentid() {
        return parentid;
    }

    public DingSyncDeptDTO setParentid(Long parentid) {
        this.parentid = parentid;
        return this;
    }

    public Long getOrder() {
        return order;
    }

    public DingSyncDeptDTO setOrder(Long order) {
        this.order = order;
        return this;
    }

    public boolean isCreateDeptGroup() {
        return createDeptGroup;
    }

    public DingSyncDeptDTO setCreateDeptGroup(boolean createDeptGroup) {
        this.createDeptGroup = createDeptGroup;
        return this;
    }

    public boolean isDeptHiding() {
        return deptHiding;
    }

    public DingSyncDeptDTO setDeptHiding(boolean deptHiding) {
        this.deptHiding = deptHiding;
        return this;
    }

    public String getDeptPermits() {
        return deptPermits;
    }

    public DingSyncDeptDTO setDeptPermits(String deptPermits) {
        this.deptPermits = deptPermits;
        return this;
    }

    public String getUserPermits() {
        return userPermits;
    }

    public DingSyncDeptDTO setUserPermits(String userPermits) {
        this.userPermits = userPermits;
        return this;
    }

    public boolean isOuterDept() {
        return outerDept;
    }

    public DingSyncDeptDTO setOuterDept(boolean outerDept) {
        this.outerDept = outerDept;
        return this;
    }

    public String getOuterPermitDepts() {
        return outerPermitDepts;
    }

    public DingSyncDeptDTO setOuterPermitDepts(String outerPermitDepts) {
        this.outerPermitDepts = outerPermitDepts;
        return this;
    }

    public String getOuterPermitUsers() {
        return outerPermitUsers;
    }

    public DingSyncDeptDTO setOuterPermitUsers(String outerPermitUsers) {
        this.outerPermitUsers = outerPermitUsers;
        return this;
    }

    public String getSourceIdentifier() {
        return sourceIdentifier;
    }

    public DingSyncDeptDTO setSourceIdentifier(String sourceIdentifier) {
        this.sourceIdentifier = sourceIdentifier;
        return this;
    }

    public String getSyncType() {
        return syncType;
    }

    public DingSyncDeptDTO setSyncType(String syncType) {
        this.syncType = syncType;
        return this;
    }

    public Long getParentUnitId() {
        return parentUnitId;
    }

    public DingSyncDeptDTO setParentUnitId(Long parentUnitId) {
        this.parentUnitId = parentUnitId;
        return this;
    }

    @Override
    public String toString() {
        return "DingSyncDeptDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parentid=" + parentid +
                ", order=" + order +
                ", createDeptGroup=" + createDeptGroup +
                ", deptHiding=" + deptHiding +
                ", deptPermits='" + deptPermits + '\'' +
                ", userPermits='" + userPermits + '\'' +
                ", outerDept=" + outerDept +
                ", outerPermitDepts='" + outerPermitDepts + '\'' +
                ", outerPermitUsers='" + outerPermitUsers + '\'' +
                ", sourceIdentifier='" + sourceIdentifier + '\'' +
                ", syncType='" + syncType + '\'' +
                ", parentUnitId=" + parentUnitId +
                '}';
    }
}
