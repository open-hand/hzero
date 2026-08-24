package org.hzero.dd.dto;

import java.util.List;

/**
 * Created by tx on 2019/11/23 17:23
 */

public class GetDeptUserInfoResultDTO extends DefaultResultDTO {

    /**
     * errcode : 0
     * errmsg : ok
     * hasMore : false
     * userlist : [{"userid":"zhangsan","unionid":"PiiiPyQqBNBii0HnCJ3zljcuAiEiE","mobile":"1xxxxxxxxxx","tel":"xxxx-xxxxxxxx","workPlace":"","remark":"","order":1,"isAdmin":true,"isBoss":false,"isHide":true,"isLeader":true,"name":"张三","active":true,"department":[1,2],"position":"工程师","email":"test@xxx.com","avatar":"xxx","jobnumber":"xxx","extattr":{"爱好":"旅游","年龄":"24"}}]
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
         * unionid : PiiiPyQqBNBii0HnCJ3zljcuAiEiE
         * mobile : 1xxxxxxxxxx
         * tel : xxxx-xxxxxxxx
         * workPlace :
         * remark :
         * order : 1
         * isAdmin : true
         * isBoss : false
         * isHide : true
         * isLeader : true
         * name : 张三
         * active : true
         * department : [1,2]
         * position : 工程师
         * email : test@xxx.com
         * avatar : xxx
         * jobnumber : xxx
         * extattr : {"爱好":"旅游","年龄":"24"}
         */

        private String userid;
        private String unionid;
        private String mobile;
        private String tel;
        private String workPlace;
        private String remark;
        private int order;
        private boolean isAdmin;
        private boolean isBoss;
        private boolean isHide;
        private boolean isLeader;
        private String name;
        private boolean active;
        private String position;
        private String email;
        private String avatar;
        private String jobnumber;
        private ExtattrBean extattr;
        private List<Integer> department;

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getUnionid() {
            return unionid;
        }

        public void setUnionid(String unionid) {
            this.unionid = unionid;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public String getWorkPlace() {
            return workPlace;
        }

        public void setWorkPlace(String workPlace) {
            this.workPlace = workPlace;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public boolean isIsAdmin() {
            return isAdmin;
        }

        public void setIsAdmin(boolean isAdmin) {
            this.isAdmin = isAdmin;
        }

        public boolean isIsBoss() {
            return isBoss;
        }

        public void setIsBoss(boolean isBoss) {
            this.isBoss = isBoss;
        }

        public boolean isIsHide() {
            return isHide;
        }

        public void setIsHide(boolean isHide) {
            this.isHide = isHide;
        }

        public boolean isIsLeader() {
            return isLeader;
        }

        public void setIsLeader(boolean isLeader) {
            this.isLeader = isLeader;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getJobnumber() {
            return jobnumber;
        }

        public void setJobnumber(String jobnumber) {
            this.jobnumber = jobnumber;
        }

        public ExtattrBean getExtattr() {
            return extattr;
        }

        public void setExtattr(ExtattrBean extattr) {
            this.extattr = extattr;
        }

        public List<Integer> getDepartment() {
            return department;
        }

        public void setDepartment(List<Integer> department) {
            this.department = department;
        }

        public static class ExtattrBean {
            /**
             * 爱好 : 旅游
             * 年龄 : 24
             */

            private String 爱好;
            private String 年龄;

            public String get爱好() {
                return 爱好;
            }

            public void set爱好(String 爱好) {
                this.爱好 = 爱好;
            }

            public String get年龄() {
                return 年龄;
            }

            public void set年龄(String 年龄) {
                this.年龄 = 年龄;
            }
        }
    }
}
