package org.hzero.dd.dto;

import java.util.List;

/**
 * Created by tx on 2019/11/23 19:49
 */

public class GetAdminScopeResultDTO extends  DefaultResultDTO {
    private List<Integer> dept_ids;

    public List<Integer> getDept_ids() {
        return dept_ids;
    }

    public void setDept_ids(List<Integer> dept_ids) {
        this.dept_ids = dept_ids;
    }

}
