package org.hzero.wechat.enterprise.dto;


import java.util.List;

/**
 * @Author J
 * @Date 2019/10/21
 */
public class AgentListDTO extends DefaultResultDTO {


    /**
     * errcode : 0
     * agentlist : [{"agentid":1000005,"name":"HR助手","square_logo_url":"https://p.qlogo.cn/bizmail/FicwmI50icF8GH9ib7rUAYR5kicLTgP265naVFQKnleqSlRhiaBx7QA9u7Q/0"}]
     */

    private List<AgentlistBean> agentlist;

    public List<AgentlistBean> getAgentlist() {
        return agentlist;
    }

    public void setAgentlist(List<AgentlistBean> agentlist) {
        this.agentlist = agentlist;
    }

    public static class AgentlistBean {
        /**
         * agentid : 1000005
         * name : HR助手
         * square_logo_url : https://p.qlogo.cn/bizmail/FicwmI50icF8GH9ib7rUAYR5kicLTgP265naVFQKnleqSlRhiaBx7QA9u7Q/0
         */

        private Long agentid;
        private String name;
        private String square_logo_url;


        public Long getAgentid() {
            return agentid;
        }

        public AgentlistBean setAgentid(Long agentid) {
            this.agentid = agentid;
            return this;
        }

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
    }
}
