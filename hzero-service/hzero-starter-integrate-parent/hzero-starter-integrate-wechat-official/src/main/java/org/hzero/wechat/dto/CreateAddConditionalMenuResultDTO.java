package org.hzero.wechat.dto;

public class CreateAddConditionalMenuResultDTO extends DefaultResultDTO {
//    "menuid":"208379533"
    private  String menuid;

    public String getMenuid() {
        return menuid;
    }

    public void setMenuid(String menuid) {
        this.menuid = menuid;
    }
}
