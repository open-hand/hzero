package org.hzero.dd.dto;
import java.util.List;

public class GetUserListDTO extends DefaultResultDTO{
    /**
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "userIds": ["1","2"]
     */

    private  List<String> userIds;

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }
}
