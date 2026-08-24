package org.hzero.wechat.enterprise.dto;

import java.util.List;

/**
 * @Author J
 * @Date 2019/10/21
 */
public class MiniProgramNoticeMessageDTO {


    /**
     * touser : zhangsan|lisi
     * toparty : 1|2
     * totag : 1|2
     * msgtype : miniprogram_notice
     * miniprogram_notice : {"appid":"wx123123123123123","page":"pages/index?userid=zhangsan&orderid=123123123","title":"会议室预订成功通知","description":"4月27日 16:16","emphasis_first_item":true,"content_item":[{"key":"会议室","value":"402"},{"key":"会议地点","value":"广州TIT-402会议室"},{"key":"会议时间","value":"2018年8月1日 09:00-09:30"},{"key":"参与人员","value":"周剑轩"}]}
     */

    private String touser;
    private String toparty;
    private String totag;
    private String msgtype;
    private MiniprogramNoticeBean miniprogram_notice;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getToparty() {
        return toparty;
    }

    public void setToparty(String toparty) {
        this.toparty = toparty;
    }

    public String getTotag() {
        return totag;
    }

    public void setTotag(String totag) {
        this.totag = totag;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public MiniprogramNoticeBean getMiniprogram_notice() {
        return miniprogram_notice;
    }

    public void setMiniprogram_notice(MiniprogramNoticeBean miniprogram_notice) {
        this.miniprogram_notice = miniprogram_notice;
    }

    public static class MiniprogramNoticeBean {
        /**
         * appid : wx123123123123123
         * page : pages/index?userid=zhangsan&orderid=123123123
         * title : 会议室预订成功通知
         * description : 4月27日 16:16
         * emphasis_first_item : true
         * content_item : [{"key":"会议室","value":"402"},{"key":"会议地点","value":"广州TIT-402会议室"},{"key":"会议时间","value":"2018年8月1日 09:00-09:30"},{"key":"参与人员","value":"周剑轩"}]
         */

        private String appid;
        private String page;
        private String title;
        private String description;
        private boolean emphasis_first_item;
        private List<ContentItemBean> content_item;

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isEmphasis_first_item() {
            return emphasis_first_item;
        }

        public void setEmphasis_first_item(boolean emphasis_first_item) {
            this.emphasis_first_item = emphasis_first_item;
        }

        public List<ContentItemBean> getContent_item() {
            return content_item;
        }

        public void setContent_item(List<ContentItemBean> content_item) {
            this.content_item = content_item;
        }

        public static class ContentItemBean {
            /**
             * key : 会议室
             * value : 402
             */

            private String key;
            private String value;

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
    }
}
