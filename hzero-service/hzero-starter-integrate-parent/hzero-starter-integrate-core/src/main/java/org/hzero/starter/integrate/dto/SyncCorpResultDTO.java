package org.hzero.starter.integrate.dto;

import java.util.List;
import java.util.Map;

/**
 * 同步结果对象
 * @author zifeng.ding@hand-china.com 2020/01/13 10:39
 */
public class SyncCorpResultDTO {
    private Boolean deptStatus;
    private Boolean employeeStatus;
    private String log;
    /**
     * 同步成功的部门Id映射关系,key为指定部门id，value为微信新建部门的主键
     */
    private Map<Long, Long> deptIdMap;
    /**
     * 同步成功的用户Id
     */
    private List<String> userIds;

    public Boolean getDeptStatus() {
        return deptStatus;
    }

    public SyncCorpResultDTO setDeptStatus(Boolean deptStatus) {
        this.deptStatus = deptStatus;
        return this;
    }

    public Boolean getEmployeeStatus() {
        return employeeStatus;
    }

    public SyncCorpResultDTO setEmployeeStatus(Boolean employeeStatus) {
        this.employeeStatus = employeeStatus;
        return this;
    }

    public String getLog() {
        return log;
    }

    public SyncCorpResultDTO setLog(String log) {
        this.log = log;
        return this;
    }

    public Map<Long, Long> getDeptIdMap() {
        return deptIdMap;
    }

    public SyncCorpResultDTO setDeptIdMap(Map<Long, Long> deptIdMap) {
        this.deptIdMap = deptIdMap;
        return this;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public SyncCorpResultDTO setUserIds(List<String> userIds) {
        this.userIds = userIds;
        return this;
    }
}
