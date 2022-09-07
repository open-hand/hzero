package org.hzero.dd.dto;

public class SendVoiceMessageDTO extends SendMessageDTO {
    private VoiceFormat msg;

    public VoiceFormat getMsg() {
        return msg;
    }

    public void setMsg(VoiceFormat msg) {
        this.msg = msg;
    }
}
