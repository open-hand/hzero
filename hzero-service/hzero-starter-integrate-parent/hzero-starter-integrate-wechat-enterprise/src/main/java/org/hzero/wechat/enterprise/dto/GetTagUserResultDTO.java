package org.hzero.wechat.enterprise.dto;

import java.util.List;

public class GetTagUserResultDTO {

    /**
     * errcode : 0
     * errmsg : ok
     * tagname : 乒乓球协会
     * userlist : [{"userid":"zhangsan","name":"李四"}]
     * partylist : [2]
     */

    private int errcode;
    private String errmsg;
    private String tagname;
    private List<UserlistBean> userlist;
    private List<Integer> partylist;

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getTagname() {
        return tagname;
    }

    public void setTagname(String tagname) {
        this.tagname = tagname;
    }

    public List<UserlistBean> getUserlist() {
        return userlist;
    }

    public void setUserlist(List<UserlistBean> userlist) {
        this.userlist = userlist;
    }

    public List<Integer> getPartylist() {
        return partylist;
    }

    public void setPartylist(List<Integer> partylist) {
        this.partylist = partylist;
    }

    public static class UserlistBean {
        /**
         * userid : zhangsan
         * name : 李四
         */

        private String userid;
        private String name;

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
