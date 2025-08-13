package org.hzero.dd.dto;

import java.util.Map;
import java.util.*;

public class SendWorkMessageDTO {
    private long agent_id;
    private String userid_list;
    private String dept_id_list;
    private boolean to_all_user;

    public long getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(long agent_id) {
        this.agent_id = agent_id;
    }

    public String getUserid_list() {
        return userid_list;
    }
    public void setUserid_list(String userid_list) {
        this.userid_list = userid_list;
    }
    public String getDept_id_list() {
        return dept_id_list;
    }
    public void setDept_id_list(String dept_id_list) {
        this.dept_id_list = dept_id_list;
    }

    public boolean isTo_all_user() {
        return to_all_user;
    }

    public void setTo_all_user(boolean to_all_user) {
        this.to_all_user = to_all_user;
    }
}
