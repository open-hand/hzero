package org.hzero.dd.dto;

import java.util.List;

/**
 * Created by tx on 2019/11/24 15:53
 */

public class GetListParentDeptsByDeptIdResultDTO extends DefaultResultDTO {


    private List<Long> parentIds;

    public List<Long> getParentIds() {
        return parentIds;
    }

    public void setParentIds(List<Long> parentIds) {
        this.parentIds = parentIds;
    }
}
