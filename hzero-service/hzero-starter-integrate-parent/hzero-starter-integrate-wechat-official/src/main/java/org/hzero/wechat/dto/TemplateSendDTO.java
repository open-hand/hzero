package org.hzero.wechat.dto;

import java.util.Map;

/**
 * @Author J
 * @Date 2019/10/15
 */
public class TemplateSendDTO {

    /**
     * touser : OPENID
     * template_id : ngqIpbwh8bUfcSsECmogfXcV14J0tQlEpBO27izEYtY
     * url : http://weixin.qq.com/download
     * miniprogram : {"appid":"xiaochengxuappid12345","pagepath":"index?foo=bar"}
     * data : {"first":{"value":"恭喜你购买成功！","color":"#173177"},"keyword1":{"value":"巧克力","color":"#173177"},"keyword2":{"value":"39.8元","color":"#173177"},"keyword3":{"value":"2014年9月22日","color":"#173177"},"remark":{"value":"欢迎再次购买！","color":"#173177"}}
     */

    /**
     * touser	是	接收者openid
     * template_id	是	模板ID
     * url	否	模板跳转链接（海外帐号没有跳转能力）
     * miniprogram	否	跳小程序所需数据，不需跳小程序可不用传该数据
     * appid	是	所需跳转到的小程序appid（该小程序appid必须与发模板消息的公众号是绑定关联关系，暂不支持小游戏）
     * pagepath	否	所需跳转到小程序的具体页面路径，支持带参数,（示例index?foo=bar），要求该小程序已发布，暂不支持小游戏
     * data	是	模板数据
     * color	否	模板内容字体颜色，不填默认为黑色
     */

    private String touser;
    private String template_id;
    private String url;
    private MiniprogramBean miniprogram;
    private Map<String ,Map<String,String>> data;


    public String getTouser() {
        return touser;
    }

    public TemplateSendDTO setTouser(String touser) {
        this.touser = touser;
        return this;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public TemplateSendDTO setTemplate_id(String template_id) {
        this.template_id = template_id;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public TemplateSendDTO setUrl(String url) {
        this.url = url;
        return this;
    }

    public MiniprogramBean getMiniprogram() {
        return miniprogram;
    }

    public TemplateSendDTO setMiniprogram(MiniprogramBean miniprogram) {
        this.miniprogram = miniprogram;
        return this;
    }

    public Map<String, Map<String, String>> getData() {
        return data;
    }

    public TemplateSendDTO setData(Map<String, Map<String, String>> data) {
        this.data = data;
        return this;
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


}
