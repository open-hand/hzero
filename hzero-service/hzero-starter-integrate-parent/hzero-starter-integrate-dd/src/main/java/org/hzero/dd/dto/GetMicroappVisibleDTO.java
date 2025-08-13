package org.hzero.dd.dto;
import java.util.*;
public class GetMicroappVisibleDTO extends DefaultResultDTO {
    /**
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "isHidden":false,
     *     "deptVisibleScopes": [1265,9634],
     *     "userVisibleScopes": ["manager6577","766578"]
     */
    private boolean isHidden;
    private List<Long> deptVisibleScopes;
    private List<String> userVisibleScopes;

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public List<Long> getDeptVisibleScopes() {
        return deptVisibleScopes;
    }

    public void setDeptVisibleScopes(List<Long> deptVisibleScopes) {
        this.deptVisibleScopes = deptVisibleScopes;
    }

    public List<String> getUserVisibleScopes() {
        return userVisibleScopes;
    }

    public void setUserVisibleScopes(List<String> userVisibleScopes) {
        this.userVisibleScopes = userVisibleScopes;
    }
}
