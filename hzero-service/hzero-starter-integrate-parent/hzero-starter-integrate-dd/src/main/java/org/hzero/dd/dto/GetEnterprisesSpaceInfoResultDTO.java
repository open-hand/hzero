package org.hzero.dd.dto;

/**
 * Created by tx on 2019/12/4 18:43
 */

public class GetEnterprisesSpaceInfoResultDTO extends DefaultResultDTO {

          private Long used_size;

    public Long getUsed_size() {
        return used_size;
    }

    public void setUsed_size(Long used_size) {
        this.used_size = used_size;
    }
}
