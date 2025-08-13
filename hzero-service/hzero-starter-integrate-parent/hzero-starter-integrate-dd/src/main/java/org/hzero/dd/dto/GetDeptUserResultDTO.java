package org.hzero.dd.dto;

import java.util.List;

/**
 * Created by tx on 2019/11/23 17:44
 */

public class GetDeptUserResultDTO extends DefaultResultDTO {

    /**
     * hasMore : false
     * userlist : [{"userid":"zhangsan","name":"张三"}]
     */

    private boolean hasMore;
    private List<UserlistBean> userlist;

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public List<UserlistBean> getUserlist() {
        return userlist;
    }

    public void setUserlist(List<UserlistBean> userlist) {
        this.userlist = userlist;
    }

    public static class UserlistBean {
        /**
         * userid : zhangsan
         * name : 张三
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
