package org.hzero.dd.dto;

public class SendWorkVoiceMessageDTO extends SendWorkMessageDTO {
    private VoiceFormat msg;

    public VoiceFormat getMsg() {
        return msg;
    }

    public void setMsg(VoiceFormat msg) {
        this.msg = msg;
    }
}
