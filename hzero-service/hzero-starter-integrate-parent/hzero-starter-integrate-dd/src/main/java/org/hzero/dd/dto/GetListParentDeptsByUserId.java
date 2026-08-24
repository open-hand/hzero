package org.hzero.dd.dto;

import java.util.List;

/**
 * Created by tx on 2019/11/24 16:00
 */

public class GetListParentDeptsByUserId extends DefaultResultDTO {

    private List<List<Long>> department;

    public List<List<Long>> getDepartment() {
        return department;
    }

    public void setDepartment(List<List<Long>> department) {
        this.department = department;
    }
}
