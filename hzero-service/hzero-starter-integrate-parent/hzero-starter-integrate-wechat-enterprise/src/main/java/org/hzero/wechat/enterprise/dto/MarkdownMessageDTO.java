package org.hzero.wechat.enterprise.dto;

/**
 * @Author J
 * @Date 2019/10/21
 */
public class MarkdownMessageDTO {


    /**
     * touser : UserID1|UserID2|UserID3
     * toparty : PartyID1|PartyID2
     * totag : TagID1 | TagID2
     * msgtype : markdown
     * agentid : 1
     * markdown : {"content":"您的会议室已经预定，稍后会同步到`邮箱`                  >**事项详情**                  >事　项：<font color=\"info\">开会<\/font>                  >组织者：@miglioguan                  >参与者：@miglioguan、@kunliu、@jamdeezhou、@kanexiong、@kisonwang                  >                  >会议室：<font color=\"info\">广州TIT 1楼 301<\/font>                  >日　期：<font color=\"warning\">2018年5月18日<\/font>                  >时　间：<font color=\"comment\">上午9:00-11:00<\/font>                  >                  >请准时参加会议。                  >                  >如需修改会议信息，请点击：[修改会议信息](https://work.weixin.qq.com)"}
     */

    private String touser;
    private String toparty;
    private String totag;
    private String msgtype;
    private long agentid;
    private MarkdownBean markdown;

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

    public MarkdownBean getMarkdown() {
        return markdown;
    }

    public void setMarkdown(MarkdownBean markdown) {
        this.markdown = markdown;
    }

    public static class MarkdownBean {
        /**
         * content : 您的会议室已经预定，稍后会同步到`邮箱`                  >**事项详情**                  >事　项：<font color="info">开会</font>                  >组织者：@miglioguan                  >参与者：@miglioguan、@kunliu、@jamdeezhou、@kanexiong、@kisonwang                  >                  >会议室：<font color="info">广州TIT 1楼 301</font>                  >日　期：<font color="warning">2018年5月18日</font>                  >时　间：<font color="comment">上午9:00-11:00</font>                  >                  >请准时参加会议。                  >                  >如需修改会议信息，请点击：[修改会议信息](https://work.weixin.qq.com)
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
