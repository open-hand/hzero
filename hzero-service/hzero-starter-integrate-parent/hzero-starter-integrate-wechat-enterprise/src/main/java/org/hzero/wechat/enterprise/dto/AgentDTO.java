package org.hzero.wechat.enterprise.dto;

import java.util.List;
import java.util.Map;

/**
 * @Author J
 * @Date 2019/10/21
 */
public class AgentDTO extends DefaultResultDTO {

    /**
     * errcode : 0
     * agentid : 1000005
     * name : HR助手
     * square_logo_url : https://p.qlogo.cn/bizmail/FicwmI50icF8GH9ib7rUAYR5kicLTgP265naVFQKnleqSlRhiaBx7QA9u7Q/0
     * description : HR服务与员工自助平台
     * allow_userinfos : {"user":[{"userid":"zhangshan"},{"userid":"lisi"}]}
     * allow_partys : {"partyid":[1]}
     * allow_tags : {"tagid":[1,2,3]}
     * close : 0
     * redirect_domain : open.work.weixin.qq.com
     * report_location_flag : 0
     * isreportenter : 0
     * home_url : https://open.work.weixin.qq.com
     */

    private String name;
    private String square_logo_url;
    private String description;
    private AllowUserinfosBean allow_userinfos;
    private AllowPartysBean allow_partys;
    private AllowTagsBean allow_tags;
    private int close;
    private String redirect_domain;
    private int report_location_flag;
    private int isreportenter;
    private String home_url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSquare_logo_url() {
        return square_logo_url;
    }

    public void setSquare_logo_url(String square_logo_url) {
        this.square_logo_url = square_logo_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AllowUserinfosBean getAllow_userinfos() {
        return allow_userinfos;
    }

    public void setAllow_userinfos(AllowUserinfosBean allow_userinfos) {
        this.allow_userinfos = allow_userinfos;
    }

    public AllowPartysBean getAllow_partys() {
        return allow_partys;
    }

    public void setAllow_partys(AllowPartysBean allow_partys) {
        this.allow_partys = allow_partys;
    }

    public AllowTagsBean getAllow_tags() {
        return allow_tags;
    }

    public void setAllow_tags(AllowTagsBean allow_tags) {
        this.allow_tags = allow_tags;
    }

    public int getClose() {
        return close;
    }

    public void setClose(int close) {
        this.close = close;
    }

    public String getRedirect_domain() {
        return redirect_domain;
    }

    public void setRedirect_domain(String redirect_domain) {
        this.redirect_domain = redirect_domain;
    }

    public int getReport_location_flag() {
        return report_location_flag;
    }

    public void setReport_location_flag(int report_location_flag) {
        this.report_location_flag = report_location_flag;
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

    public static class AllowUserinfosBean {
        private List<Map<String,String>> user;

        public List<Map<String, String>> getUser() {
            return user;
        }

        public AllowUserinfosBean setUser(List<Map<String, String>> user) {
            this.user = user;
            return this;
        }
    }

    public static class AllowPartysBean {
        private List<Long> partyid;

        public List<Long> getPartyid() {
            return partyid;
        }

        public AllowPartysBean setPartyid(List<Long> partyid) {
            this.partyid = partyid;
            return this;
        }
    }

    public static class AllowTagsBean {
        private List<Long> tagid;

        public List<Long> getTagid() {
            return tagid;
        }

        public AllowTagsBean setTagid(List<Long> tagid) {
            this.tagid = tagid;
            return this;
        }
    }
}
