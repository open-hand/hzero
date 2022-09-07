package org.hzero.boot.message.entity;

/**
 * dingding webhook发送类
 *
 * 钉钉开发文档网址： https://ding-doc.dingtalk.com/doc#/serverapi3/iydd5h
 *
 * @author xiaoyu.zhao@hand-china.com 2020/05/08 10:04
 */
public class WhDingTalkSend {

    /**
     * 消息类型
     */
    private String msgtype;
    /**
     * at 标识
     */
    private WhDingAt at;

    /**
     * markdown类型
     */
    private WhDingMarkdown markdown;

    /**
     * at 标识下属字段
     */
    public static class WhDingAt {
        private String[] atMobiles;
        private Boolean isAtAll;

        public WhDingAt() {
        }

        public String[] getAtMobiles() {
            return atMobiles;
        }

        public void setAtMobiles(String[] atMobiles) {
            this.atMobiles = atMobiles;
        }

        public Boolean getAtAll() {
            return isAtAll;
        }

        public void setAtAll(Boolean atAll) {
            isAtAll = atAll;
        }
    }

    /**
     * markdown下属字段
     */
    public static class WhDingMarkdown {
        private String text;
        private String title;

        public WhDingMarkdown() {
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public WhDingAt getAt() {
        return at;
    }

    public void setAt(WhDingAt at) {
        this.at = at;
    }

    public WhDingMarkdown getMarkdown() {
        return markdown;
    }

    public void setMarkdown(WhDingMarkdown markdown) {
        this.markdown = markdown;
    }
}
