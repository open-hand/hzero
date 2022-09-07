package org.hzero.wechat.enterprise.dto;

/**
 * @Author J
 * @Date 2019/10/21
 */
public class TextCardMessageDTO {


    /**
     * touser : UserID1|UserID2|UserID3
     * toparty : PartyID1 | PartyID2
     * totag : TagID1 | TagID2
     * msgtype : textcard
     * agentid : 1
     * textcard : {"title":"领奖通知","description":"<div class=\"gray\">2016年9月26日<\/div> <div class=\"normal\">恭喜你抽中iPhone 7一台，领奖码：xxxx<\/div><div class=\"highlight\">请于2016年10月10日前联系行政同事领取<\/div>","url":"URL","btntxt":"更多"}
     * enable_id_trans : 0
     */

    private String touser;
    private String toparty;
    private String totag;
    private String msgtype;
    private long agentid;
    private TextcardBean textcard;
    private int enable_id_trans;

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

    public long getAgentid() {
        return agentid;
    }

    public void setAgentid(long agentid) {
        this.agentid = agentid;
    }

    public TextcardBean getTextcard() {
        return textcard;
    }

    public void setTextcard(TextcardBean textcard) {
        this.textcard = textcard;
    }

    public int getEnable_id_trans() {
        return enable_id_trans;
    }

    public void setEnable_id_trans(int enable_id_trans) {
        this.enable_id_trans = enable_id_trans;
    }

    public static class TextcardBean {
        /**
         * title : 领奖通知
         * description : <div class="gray">2016年9月26日</div> <div class="normal">恭喜你抽中iPhone 7一台，领奖码：xxxx</div><div class="highlight">请于2016年10月10日前联系行政同事领取</div>
         * url : URL
         * btntxt : 更多
         */

        private String title;
        private String description;
        private String url;
        private String btntxt;

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

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getBtntxt() {
            return btntxt;
        }

        public void setBtntxt(String btntxt) {
            this.btntxt = btntxt;
        }
    }
}
