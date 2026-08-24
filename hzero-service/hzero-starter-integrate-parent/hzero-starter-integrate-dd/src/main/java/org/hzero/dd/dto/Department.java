package org.hzero.dd.dto;

public class Department {
    /**
     * {
     *             "id": 3,
     *             "name": "服务端开发组",
     *             "parentid": 2,
     *             "createDeptGroup": false,
     *             "autoAddUser": false
     *         }
     */
    private long id;
    private String name;
    private Long parentid;
    private boolean createDeptGroup;
    private boolean autoAddUser;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
}
