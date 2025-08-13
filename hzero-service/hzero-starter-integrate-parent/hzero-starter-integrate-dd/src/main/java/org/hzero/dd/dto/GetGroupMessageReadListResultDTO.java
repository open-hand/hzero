package org.hzero.dd.dto;
import java.util.List;
public class GetGroupMessageReadListResultDTO extends DefaultResultDTO {
    /**
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "next_cursor": 200467002472,
     *     "readUserIdList": [
     *         "08000000000000"
     *     ]
     */

    private  Long next_cursor;
    private List<String> readUserIdList;

    public Long getNext_cursor() {
        return next_cursor;
    }

    public void setNext_cursor(Long next_cursor) {
        this.next_cursor = next_cursor;
    }

    public List<String> getReadUserIdList() {
        return readUserIdList;
    }

    public void setReadUserIdList(List<String> readUserIdList) {
        this.readUserIdList = readUserIdList;
    }
}
