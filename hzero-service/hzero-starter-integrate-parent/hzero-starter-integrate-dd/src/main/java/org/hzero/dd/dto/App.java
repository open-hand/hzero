package org.hzero.dd.dto;

public class App {
    /**
             *             "appIcon": "http://xxx.dingtalk.com/media/xxx_200_200.png",
             *             "agentId": 111,
             *             "appDesc": "应用描述",
             *             "isSelf": true,
             *             "name": "应用名称",
     *
             *             "homepageLink": "http://oa.dingtalk.com/?h5",
             *             "pcHomepageLink": "http://oa.dingtalk.com/?pc",
             *             "appStatus": 1,
             *             "ompLink": "http://oa.dingtalk.com/index"
     * */
    private String appIcon;
    private Long agentId;
    private String appDesc;

    private boolean isSelf;
    private String name;
    private String homepageLink;
    private String pcHomepageLink;

    private int appStatus;
    private String ompLink;

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public String getAppDesc() {
        return appDesc;
    }

    public void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHomepageLink() {
        return homepageLink;
    }

    public void setHomepageLink(String homepageLink) {
        this.homepageLink = homepageLink;
    }

    public String getPcHomepageLink() {
        return pcHomepageLink;
    }

    public void setPcHomepageLink(String pcHomepageLink) {
        this.pcHomepageLink = pcHomepageLink;
    }

    public int getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(int appStatus) {
        this.appStatus = appStatus;
    }

    public String getOmpLink() {
        return ompLink;
    }

    public void setOmpLink(String ompLink) {
        this.ompLink = ompLink;
    }
}
