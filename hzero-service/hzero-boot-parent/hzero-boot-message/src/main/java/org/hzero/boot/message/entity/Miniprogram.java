package org.hzero.boot.message.entity;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/10/17 16:51
 */
public class Miniprogram {

    /**
     * appid : xiaochengxuappid12345
     * pagepath : index?foo=bar
     */

    private String appid;
    private String pagepath;

    public String getAppid() {
        return appid;
    }

    public Miniprogram setAppid(String appid) {
        this.appid = appid;
        return this;
    }

    public String getPagepath() {
        return pagepath;
    }

    public Miniprogram setPagepath(String pagepath) {
        this.pagepath = pagepath;
        return this;
    }
}
