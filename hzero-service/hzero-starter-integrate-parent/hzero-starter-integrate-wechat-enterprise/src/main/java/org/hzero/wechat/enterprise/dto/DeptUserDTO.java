package org.hzero.wechat.enterprise.dto;

import java.util.List;

/**
 * @Author J
 * @Date 2019/10/22
 */
public class DeptUserDTO extends DefaultResultDTO{

    /**
     * errcode : 0
     * userlist : [{"userid":"zhangsan","name":"李四","department":[1,2]}]
     */

    private List<UserlistBean> userlist;

    public List<UserlistBean> getUserlist() {
        return userlist;
    }

    public void setUserlist(List<UserlistBean> userlist) {
        this.userlist = userlist;
    }

    public static class UserlistBean {
        /**
         * userid : zhangsan
         * name : 李四
         * department : [1,2]
         */

        private String userid;
        private String name;
        private List<Long> department;

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


        public List<Long> getDepartment() {
            return department;
        }

        public UserlistBean setDepartment(List<Long> department) {
            this.department = department;
            return this;
        }
    }
}
