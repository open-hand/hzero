package org.hzero.wechat.enterprise.dto;

/**
 * @Author J
 * @Date 2019/10/22
 */
public class CreateDeptResultDTO extends DefaultResultDTO{

    /**
     * errcode : 0
     * errmsg : created
     * id : 2
     */

    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
