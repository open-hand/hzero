package org.hzero.boot.message.entity;

/**
 * 企业微信webhook消息发送
 *
 * 企业微信群机器人： https://work.weixin.qq.com/api/doc/90000/90136/91770
 *
 * @author xiaoyu.zhao@hand-china.com 2020/05/08 10:34
 */
public class WhWeChatSend {
    private String msgtype;
    private WhWeChatSend.WhWeChatMarkdown markdown;

    public static class WhWeChatMarkdown{
        private String content;

        public WhWeChatMarkdown() {
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public WhWeChatMarkdown getMarkdown() {
        return markdown;
    }

    public void setMarkdown(WhWeChatMarkdown markdown) {
        this.markdown = markdown;
    }
}
