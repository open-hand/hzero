package org.hzero.wechat.enterprise.dto;

import java.util.List;

public class GetTagUserListResultDTO extends DefaultResultDTO {

    /**
     * errcode : 0
     * errmsg : ok
     * taglist : [{"tagid":1,"tagname":"a"},{"tagid":2,"tagname":"b"}]
     */

    private List<TaglistBean> taglist;

    public List<TaglistBean> getTaglist() {
        return taglist;
    }

    public void setTaglist(List<TaglistBean> taglist) {
        this.taglist = taglist;
    }

    public static class TaglistBean {
        /**
         * tagid : 1
         * tagname : a
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
}
