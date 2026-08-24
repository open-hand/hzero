package org.hzero.wechat.enterprise.dto;

public class CreateTagDTO {

    /**
     * tagname : UI
     * tagid : 12
     */

    private String tagname;
    private int tagid;

    public String getTagname() {
        return tagname;
    }

    public void setTagname(String tagname) {
        this.tagname = tagname;
    }

    public int getTagid() {
        return tagid;
    }

    public void setTagid(int tagid) {
        this.tagid = tagid;
    }
}
