package org.hzero.wechat.enterprise.dto;
import java.util.*;
public class TagUserResultDTO extends DefaultResultDTO {
    private String invalidlist;
    private List<Long> invalidparty;

    public String getInvalidlist() {
        return invalidlist;
    }

    public void setInvalidlist(String invalidlist) {
        this.invalidlist = invalidlist;
    }

    public List<Long> getInvalidparty() {
        return invalidparty;
    }

    public void setInvalidparty(List<Long> invalidparty) {
        this.invalidparty = invalidparty;
    }
}
