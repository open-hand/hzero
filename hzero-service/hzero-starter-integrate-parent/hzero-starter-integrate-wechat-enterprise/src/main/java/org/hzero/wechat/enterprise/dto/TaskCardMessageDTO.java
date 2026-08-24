package org.hzero.wechat.enterprise.dto;

import java.util.List;

/**
 * @Author J
 * @Date 2019/10/21
 */
public class TaskCardMessageDTO {

    /**
     * touser : UserID1|UserID2|UserID3
     * toparty : PartyID1 | PartyID2
     * totag : TagID1 | TagID2
     * msgtype : taskcard
     * agentid : 1
     * taskcard : {"title":"赵明登的礼物申请","description":"礼品：A31茶具套装<br>用途：赠与小黑科技张总经理","url":"URL","task_id":"taskid123","btn":[{"key":"key111","name":"批准","replace_name":"已批准","color":"red","is_bold":true},{"key":"key222","name":"驳回","replace_name":"已驳回"}]}
     */

    private String touser;
    private String toparty;
    private String totag;
    private String msgtype;
    private long agentid;
    private TaskcardBean taskcard;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getToparty() {
        return toparty;
    }

    public void setToparty(String toparty) {
        this.toparty = toparty;
    }

    public String getTotag() {
        return totag;
    }

    public void setTotag(String totag) {
        this.totag = totag;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public long getAgentid() {
        return agentid;
    }

    public void setAgentid(long agentid) {
        this.agentid = agentid;
    }

    public TaskcardBean getTaskcard() {
        return taskcard;
    }

    public void setTaskcard(TaskcardBean taskcard) {
        this.taskcard = taskcard;
    }

    public static class TaskcardBean {
        /**
         * title : 赵明登的礼物申请
         * description : 礼品：A31茶具套装<br>用途：赠与小黑科技张总经理
         * url : URL
         * task_id : taskid123
         * btn : [{"key":"key111","name":"批准","replace_name":"已批准","color":"red","is_bold":true},{"key":"key222","name":"驳回","replace_name":"已驳回"}]
         */

        private String title;
        private String description;
        private String url;
        private String task_id;
        private List<BtnBean> btn;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTask_id() {
            return task_id;
        }

        public void setTask_id(String task_id) {
            this.task_id = task_id;
        }

        public List<BtnBean> getBtn() {
            return btn;
        }

        public void setBtn(List<BtnBean> btn) {
            this.btn = btn;
        }

        public static class BtnBean {
            /**
             * key : key111
             * name : 批准
             * replace_name : 已批准
             * color : red
             * is_bold : true
             */

            private String key;
            private String name;
            private String replace_name;
            private String color;
            private boolean is_bold;

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getReplace_name() {
                return replace_name;
            }

            public void setReplace_name(String replace_name) {
                this.replace_name = replace_name;
            }

            public String getColor() {
                return color;
            }

            public void setColor(String color) {
                this.color = color;
            }

            public boolean isIs_bold() {
                return is_bold;
            }

            public void setIs_bold(boolean is_bold) {
                this.is_bold = is_bold;
            }
        }
    }
}
