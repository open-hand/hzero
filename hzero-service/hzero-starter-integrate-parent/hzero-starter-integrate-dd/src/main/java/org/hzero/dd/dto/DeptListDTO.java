package org.hzero.dd.dto;
import java.util.*;
public class DeptListDTO extends DefaultResultDTO{
    /**
     * {
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "department": [
     *         {
     *            "id": 2,
     *             "name": "xxx",
     *             "parentid": 1,
     *             "createDeptGroup": true,
     *             "autoAddUser": true
     *         },
     *         {
     *             "id": 3,
     *             "name": "服务端开发组",
     *             "parentid": 2,
     *             "createDeptGroup": false,
     *             "autoAddUser": false
     *         }
     *     ]
     * }
     */

    private List<Department> department;

    public List<Department> getDepartment() {
        return department;
    }

    public void setDepartment(List<Department> department) {
        this.department = department;
    }
}
