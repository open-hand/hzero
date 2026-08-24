package org.hzero.dd.dto;

import java.util.List;

/**
 * Created by tx on 2019/11/23 19:46
 */

public class GetAdminListResultDTO extends  DefaultResultDTO {

    private List<AdminListBean> admin_list;

    public List<AdminListBean> getAdmin_list() {
        return admin_list;
    }

    public void setAdmin_list(List<AdminListBean> admin_list) {
        this.admin_list = admin_list;
    }

    public static class AdminListBean {
        /**
         * sys_level : 2
         * userid : userid2
         */

        private int sys_level;
        private String userid;

        public int getSys_level() {
            return sys_level;
        }

        public void setSys_level(int sys_level) {
            this.sys_level = sys_level;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }
    }
}
