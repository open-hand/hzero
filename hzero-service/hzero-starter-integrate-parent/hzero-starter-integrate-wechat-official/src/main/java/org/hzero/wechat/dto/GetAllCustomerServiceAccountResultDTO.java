package org.hzero.wechat.dto;

import java.util.List;

public class GetAllCustomerServiceAccountResultDTO {
    private List<KfListBean> kf_list;
    public List<KfListBean> getKf_list() {
        return kf_list;
    }

    public void setKf_list(List<KfListBean> kf_list) {
        this.kf_list = kf_list;
    }


    public static class KfListBean {
        /**
         * kf_account : test1@test
         * kf_nick : ntest1
         * kf_id : 1001
         * kf_headimgurl :  http://mmbiz.qpic.cn/mmbiz/4whpV1VZl2iccsvYbHvnphkyGtnvjfUS8Ym0GSaLic0FD3vN0V8PILcibEGb2fPfEOmw/0
         */
        private String kf_account;
        private String kf_nick;
        private String kf_id;
        private String kf_headimgurl;

        public String getKf_account() {
            return kf_account;
        }

        public void setKf_account(String kf_account) {
            this.kf_account = kf_account;
        }

        public String getKf_nick() {
            return kf_nick;
        }

        public void setKf_nick(String kf_nick) {
            this.kf_nick = kf_nick;
        }

        public String getKf_id() {
            return kf_id;
        }

        public void setKf_id(String kf_id) {
            this.kf_id = kf_id;
        }

        public String getKf_headimgurl() {
            return kf_headimgurl;
        }

        public void setKf_headimgurl(String kf_headimgurl) {
            this.kf_headimgurl = kf_headimgurl;
        }
    }
}
