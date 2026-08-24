package org.hzero.dd.dto;
import java.util.*;


public class GetSubDeptDTO extends DefaultResultDTO{

    private List<Long> sub_dept_id_list;

    public List<Long> getSub_dept_id_list() {
        return sub_dept_id_list;
    }

    public void setSub_dept_id_list(List<Long> sub_dept_id_list) {
        this.sub_dept_id_list = sub_dept_id_list;
    }
}
