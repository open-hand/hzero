package org.hzero.dd.dto;

public class SendGroupVoiceMessageDTO extends SendGroupMessageDTO {
    private VoiceFormat msg;

    public VoiceFormat getMsg() {
        return msg;
    }

    public void setMsg(VoiceFormat msg) {
        this.msg = msg;
    }
}
