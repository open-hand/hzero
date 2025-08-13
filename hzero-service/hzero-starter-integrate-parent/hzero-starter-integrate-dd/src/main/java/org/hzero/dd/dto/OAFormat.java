package org.hzero.dd.dto;

import java.util.List;

public class OAFormat {

    /**
     * msgtype : oa
     * oa : {"message_url":"http://dingtalk.com","head":{"bgcolor":"FFBBBBBB","text":"头部标题"},"body":{"title":"正文标题","form":[{"key":"姓名:","value":"张三"},{"key":"年龄:","value":"20"},{"key":"身高:","value":"1.8米"},{"key":"体重:","value":"130斤"},{"key":"学历:","value":"本科"},{"key":"爱好:","value":"打球、听音乐"}],"rich":{"num":"15.6","unit":"元"},"content":"大段文本大段文本大段文本大段文本大段文本大段文本大段文本大段文本大段文本大段文本大段文本大段文本","image":"@lADOADmaWMzazQKA","file_count":"3","author":"李四 "}}
     */

    private String msgtype;
    private OaBean oa;

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public OaBean getOa() {
        return oa;
    }

    public void setOa(OaBean oa) {
        this.oa = oa;
    }

    public static class OaBean {
        /**
         * message_url : http://dingtalk.com
         * head : {"bgcolor":"FFBBBBBB","text":"头部标题"}
         * body : {"title":"正文标题","form":[{"key":"姓名:","value":"张三"},{"key":"年龄:","value":"20"},{"key":"身高:","value":"1.8米"},{"key":"体重:","value":"130斤"},{"key":"学历:","value":"本科"},{"key":"爱好:","value":"打球、听音乐"}],"rich":{"num":"15.6","unit":"元"},"content":"大段文本大段文本大段文本大段文本大段文本大段文本大段文本大段文本大段文本大段文本大段文本大段文本","image":"@lADOADmaWMzazQKA","file_count":"3","author":"李四 "}
         */

        private String message_url;
        private HeadBean head;
        private BodyBean body;

        public String getMessage_url() {
            return message_url;
        }

        public void setMessage_url(String message_url) {
            this.message_url = message_url;
        }

        public HeadBean getHead() {
            return head;
        }

        public void setHead(HeadBean head) {
            this.head = head;
        }

        public BodyBean getBody() {
            return body;
        }

        public void setBody(BodyBean body) {
            this.body = body;
        }

        public static class HeadBean {
            /**
             * bgcolor : FFBBBBBB
             * text : 头部标题
             */

            private String bgcolor;
            private String text;

            public String getBgcolor() {
                return bgcolor;
            }

            public void setBgcolor(String bgcolor) {
                this.bgcolor = bgcolor;
            }

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }
        }

        public static class BodyBean {
            /**
             * title : 正文标题
             * form : [{"key":"姓名:","value":"张三"},{"key":"年龄:","value":"20"},{"key":"身高:","value":"1.8米"},{"key":"体重:","value":"130斤"},{"key":"学历:","value":"本科"},{"key":"爱好:","value":"打球、听音乐"}]
             * rich : {"num":"15.6","unit":"元"}
             * content : 大段文本大段文本大段文本大段文本大段文本大段文本大段文本大段文本大段文本大段文本大段文本大段文本
             * image : @lADOADmaWMzazQKA
             * file_count : 3
             * author : 李四
             */

            private String title;
            private RichBean rich;
            private String content;
            private String image;
            private String file_count;
            private String author;
            private List<FormBean> form;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public RichBean getRich() {
                return rich;
            }

            public void setRich(RichBean rich) {
                this.rich = rich;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getFile_count() {
                return file_count;
            }

            public void setFile_count(String file_count) {
                this.file_count = file_count;
            }

            public String getAuthor() {
                return author;
            }

            public void setAuthor(String author) {
                this.author = author;
            }

            public List<FormBean> getForm() {
                return form;
            }

            public void setForm(List<FormBean> form) {
                this.form = form;
            }

            public static class RichBean {
                /**
                 * num : 15.6
                 * unit : 元
                 */

                private String num;
                private String unit;

                public String getNum() {
                    return num;
                }

                public void setNum(String num) {
                    this.num = num;
                }

                public String getUnit() {
                    return unit;
                }

                public void setUnit(String unit) {
                    this.unit = unit;
                }
            }

            public static class FormBean {
                /**
                 * key : 姓名:
                 * value : 张三
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
}
