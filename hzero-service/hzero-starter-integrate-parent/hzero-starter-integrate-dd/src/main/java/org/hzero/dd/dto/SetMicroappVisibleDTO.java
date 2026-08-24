package org.hzero.dd.dto;
import java.util.*;
public class SetMicroappVisibleDTO {
    /**
     *     "agentId": 16xxxxxx,
     *     "isHidden":false,
     *     "deptVisibleScopes":[1,2],
     *     "userVisibleScopes":["userId1","userId2"]
     */
    private long agentId;
    private boolean isHidden;
    private List<Integer> deptVisibleScopes;
    private List<String> userVisibleScopes;


    public long getAgentId() {
        return agentId;
    }

    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public List<Integer> getDeptVisibleScopes() {
        return deptVisibleScopes;
    }

    public void setDeptVisibleScopes(List<Integer> deptVisibleScopes) {
        this.deptVisibleScopes = deptVisibleScopes;
    }

    public List<String> getUserVisibleScopes() {
        return userVisibleScopes;
    }

    public void setUserVisibleScopes(List<String> userVisibleScopes) {
        this.userVisibleScopes = userVisibleScopes;
    }
}
