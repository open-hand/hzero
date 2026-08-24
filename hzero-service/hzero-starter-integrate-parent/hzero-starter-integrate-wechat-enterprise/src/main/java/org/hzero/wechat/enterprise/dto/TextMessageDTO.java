package org.hzero.wechat.enterprise.dto;

/**
 * @Author J
 * @Date 2019/10/21
 */
public class TextMessageDTO {

    /**
     * touser : UserID1|UserID2|UserID3
     * toparty : PartyID1|PartyID2
     * totag : TagID1 | TagID2
     * msgtype : text
     * agentid : 1
     * text : {"content":"你的快递已到，请携带工卡前往邮件中心领取。\n出发前可查看<a href=\"http://work.weixin.qq.com\">邮件中心视频实况<\/a>，聪明避开排队。"}
     * safe : 0
     * enable_id_trans : 0
     */

    private String touser;
    private String toparty;
    private String totag;
    private String msgtype;
    private long agentid;
    private TextBean text;
    private int safe;
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

    public TextBean getText() {
        return text;
    }

    public void setText(TextBean text) {
        this.text = text;
    }

    public int getSafe() {
        return safe;
    }

    public void setSafe(int safe) {
        this.safe = safe;
    }

    public int getEnable_id_trans() {
        return enable_id_trans;
    }

    public void setEnable_id_trans(int enable_id_trans) {
        this.enable_id_trans = enable_id_trans;
    }

    public static class TextBean {
        /**
         * content : 你的快递已到，请携带工卡前往邮件中心领取。
         出发前可查看<a href="http://work.weixin.qq.com">邮件中心视频实况</a>，聪明避开排队。
         */

        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
