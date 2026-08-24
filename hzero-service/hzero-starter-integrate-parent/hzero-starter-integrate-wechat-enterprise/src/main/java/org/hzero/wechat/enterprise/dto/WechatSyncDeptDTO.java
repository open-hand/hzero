package org.hzero.wechat.enterprise.dto;

/**
 * @Author J
 * @Date 2019/8/28
 */
public class WechatSyncDeptDTO {


    /**
     * name : 广州研发中心
     * parentid : 1
     * order : 1
     * id : 2
     */

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

    public WechatSyncDeptDTO setName(String name) {
        this.name = name;
        return this;
    }

    public Long getParentid() {
        return parentid;
    }

    public WechatSyncDeptDTO setParentid(Long parentid) {
        this.parentid = parentid;
        return this;
    }

    public Long getOrder() {
        return order;
    }

    public WechatSyncDeptDTO setOrder(Long order) {
        this.order = order;
        return this;
    }

    public Long getId() {
        return id;
    }

    public WechatSyncDeptDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getSyncType() {
        return syncType;
    }

    public WechatSyncDeptDTO setSyncType(String syncType) {
        this.syncType = syncType;
        return this;
    }

    public Long getParentUnitId() {
        return parentUnitId;
    }

    public WechatSyncDeptDTO setParentUnitId(Long parentUnitId) {
        this.parentUnitId = parentUnitId;
        return this;
    }

    @Override
    public String toString() {
        return "WechatSyncDeptDTO{" +
                "name='" + name + '\'' +
                ", parentid=" + parentid +
                ", order=" + order +
                ", id=" + id +
                ", syncType='" + syncType + '\'' +
                ", parentUnitId=" + parentUnitId +
                '}';
    }
}
