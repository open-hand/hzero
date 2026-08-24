package org.hzero.wechat.dto;

public class CustomServiceMessageStatusDTO {

    /**
     * touser : OPENID
     * command : Typing
     */

    private String touser;
    private String command;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
