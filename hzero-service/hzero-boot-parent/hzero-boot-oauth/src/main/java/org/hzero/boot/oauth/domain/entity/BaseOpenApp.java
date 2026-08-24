package org.hzero.boot.oauth.domain.entity;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 三方登录缓存实体
 *
 * @author xiaoyu.zhao@hand-china.com 2019/08/27 21:06
 */
@Table(name = "hiam_open_app")
public class BaseOpenApp implements Serializable {

    private static final long serialVersionUID = -5878117775275816474L;

    @Id
    private Long openAppId;
    /**
     * 登录渠道
     */
    private String channel;
    /**
     * 应用编码
     */
    private String appCode;
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 图片地址
     */
    private String appImage;
    /**
     * APP ID
     */
    private String appId;

    private String subAppId;
    /**
     * APP Key
     */
    private String appKey;
    /**
     * scope
     */
    private String scope;
    /**
     * 排序
     */
    private Integer orderSeq;

    public Long getOpenAppId() {
        return openAppId;
    }

    public void setOpenAppId(Long openAppId) {
        this.openAppId = openAppId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppImage() {
        return appImage;
    }

    public void setAppImage(String appImage) {
        this.appImage = appImage;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSubAppId() {
        return subAppId;
    }

    public void setSubAppId(String subAppId) {
        this.subAppId = subAppId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public Integer getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(Integer orderSeq) {
        this.orderSeq = orderSeq;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
