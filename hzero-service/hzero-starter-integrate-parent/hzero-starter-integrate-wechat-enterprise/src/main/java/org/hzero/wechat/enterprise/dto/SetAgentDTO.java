package org.hzero.wechat.enterprise.dto;

/**
 * @Author J
 * @Date 2019/10/21
 */
public class SetAgentDTO {


    /**
     * agentid : 1000005
     * report_location_flag : 0
     * logo_mediaid : j5Y8X5yocspvBHcgXMSS6z1Cn9RQKREEJr4ecgLHi4YHOYP-plvom-yD9zNI0vEl
     * name : 财经助手
     * description : 内部财经服务平台
     * redirect_domain : open.work.weixin.qq.com
     * isreportenter : 0
     * home_url : https://open.work.weixin.qq.com
     */

    private long agentid;
    private int report_location_flag;
    private String logo_mediaid;
    private String name;
    private String description;
    private String redirect_domain;
    private int isreportenter;
    private String home_url;

    public long getAgentid() {
        return agentid;
    }

    public void setAgentid(long agentid) {
        this.agentid = agentid;
    }

    public int getReport_location_flag() {
        return report_location_flag;
    }

    public void setReport_location_flag(int report_location_flag) {
        this.report_location_flag = report_location_flag;
    }

    public String getLogo_mediaid() {
        return logo_mediaid;
    }

    public void setLogo_mediaid(String logo_mediaid) {
        this.logo_mediaid = logo_mediaid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRedirect_domain() {
        return redirect_domain;
    }

    public void setRedirect_domain(String redirect_domain) {
        this.redirect_domain = redirect_domain;
    }

    public int getIsreportenter() {
        return isreportenter;
    }

    public void setIsreportenter(int isreportenter) {
        this.isreportenter = isreportenter;
    }

    public String getHome_url() {
        return home_url;
    }

    public void setHome_url(String home_url) {
        this.home_url = home_url;
    }
}
