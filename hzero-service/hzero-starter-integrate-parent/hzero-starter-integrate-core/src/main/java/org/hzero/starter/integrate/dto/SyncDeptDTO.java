package org.hzero.starter.integrate.dto;

/**
 * hzero平台同步部门对象
 * @author zifeng.ding@hand-china.com 2020/01/13 10:39
 */
public class SyncDeptDTO {

    private String name;
    private Long parentid;
    private Long order;
    private Long id;
    /**
     * 对象的同步操作，新增：create、更新：update、删除：delete
     */
    private String syncType;
    /**
     * hzero平台部门的父部门id
     */
    private Long parentUnitId;

    public String getName() {
        return name;
    }

    public SyncDeptDTO setName(String name) {
        this.name = name;
        return this;
    }

    public Long getParentid() {
        return parentid;
    }

    public SyncDeptDTO setParentid(Long parentid) {
        this.parentid = parentid;
        return this;
    }

    public Long getOrder() {
        return order;
    }

    public SyncDeptDTO setOrder(Long order) {
        this.order = order;
        return this;
    }

    public Long getId() {
        return id;
    }

    public SyncDeptDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getSyncType() {
        return syncType;
    }

    public SyncDeptDTO setSyncType(String syncType) {
        this.syncType = syncType;
        return this;
    }

    public Long getParentUnitId() {
        return parentUnitId;
    }

    public SyncDeptDTO setParentUnitId(Long parentUnitId) {
        this.parentUnitId = parentUnitId;
        return this;
    }

    @Override
    public String toString() {
        return "SyncDeptDTO{" +
                "name='" + name + '\'' +
                ", parentid=" + parentid +
                ", order=" + order +
                ", id=" + id +
                ", syncType='" + syncType + '\'' +
                ", parentUnitId=" + parentUnitId +
                '}';
    }
}