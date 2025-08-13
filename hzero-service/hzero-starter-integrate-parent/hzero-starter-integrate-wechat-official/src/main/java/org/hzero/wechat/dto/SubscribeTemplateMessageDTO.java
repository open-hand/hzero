package org.hzero.wechat.dto;

public class SubscribeTemplateMessageDTO {

    /**
     * touser : OPENID
     * template_id : TEMPLATE_ID
     * url : URL
     * miniprogram : {"appid":"xiaochengxuappid12345","pagepath":"index?foo=bar"}
     * scene : SCENE
     * title : TITLE
     * data : {"content":{"value":"VALUE","color":"COLOR"}}
     */

    private String touser;
    private String template_id;
    private String url;
    private MiniprogramBean miniprogram;
    private String scene;
    private String title;
    private DataBean data;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public MiniprogramBean getMiniprogram() {
        return miniprogram;
    }

    public void setMiniprogram(MiniprogramBean miniprogram) {
        this.miniprogram = miniprogram;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class MiniprogramBean {
        /**
         * appid : xiaochengxuappid12345
         * pagepath : index?foo=bar
         */

        private String appid;
        private String pagepath;

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getPagepath() {
            return pagepath;
        }

        public void setPagepath(String pagepath) {
            this.pagepath = pagepath;
        }
    }

    public static class DataBean {
        /**
         * content : {"value":"VALUE","color":"COLOR"}
         */

        private ContentBean content;

        public ContentBean getContent() {
            return content;
        }

        public void setContent(ContentBean content) {
            this.content = content;
        }

        public static class ContentBean {
            /**
             * value : VALUE
             * color : COLOR
             */

            private String value;
            private String color;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public String getColor() {
                return color;
            }

            public void setColor(String color) {
                this.color = color;
            }
        }
    }
}
