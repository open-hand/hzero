package org.hzero.wechat.dto;

public class GetMaterialListDTO {
    /**
     * type : TYPE
     * offset : OFFSET
     * count : COUNT
     */

    private String type;
    private String offset;
    private String count;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
//    {
//        "type":TYPE,
//            "offset":OFFSET,
//            "count":COUNT
//    }

}
