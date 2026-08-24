package org.hzero.wechat.enterprise.dto;

public class UpdateTagNameDTO {

    /**
     * tagid : 12
     * tagname : UI design
     */

    private int tagid;
    private String tagname;

    public int getTagid() {
        return tagid;
    }

    public void setTagid(int tagid) {
        this.tagid = tagid;
    }

    public String getTagname() {
        return tagname;
    }

    public void setTagname(String tagname) {
        this.tagname = tagname;
    }
}
