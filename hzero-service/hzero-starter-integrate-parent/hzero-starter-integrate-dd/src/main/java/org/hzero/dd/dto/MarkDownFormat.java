package org.hzero.dd.dto;

public class MarkDownFormat {

    /**
     * msgtype : markdown
     * markdown : {"title":"首屏会话透出的展示内容","text":"# 这是支持markdown的文本 \n## 标题2  \n* 列表1 \n![alt 啊](https://img.alicdn.com/tps/TB1XLjqNVXXXXc4XVXXXXXXXXXX-170-64.png)"}
     */

    private String msgtype;
    private MarkdownBean markdown;

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public MarkdownBean getMarkdown() {
        return markdown;
    }

    public void setMarkdown(MarkdownBean markdown) {
        this.markdown = markdown;
    }

    public static class MarkdownBean {
        /**
         * title : 首屏会话透出的展示内容
         * text : # 这是支持markdown的文本
         ## 标题2
         * 列表1
         ![alt 啊](https://img.alicdn.com/tps/TB1XLjqNVXXXXc4XVXXXXXXXXXX-170-64.png)
         */

        private String title;
        private String text;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
