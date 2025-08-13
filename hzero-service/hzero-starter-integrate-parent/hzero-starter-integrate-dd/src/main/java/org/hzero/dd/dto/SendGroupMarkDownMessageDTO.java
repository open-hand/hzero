package org.hzero.dd.dto;

public class SendGroupMarkDownMessageDTO extends  SendGroupMessageDTO {

  private MarkDownFormat markDownFormat;

    public MarkDownFormat getMarkDownFormat() {
        return markDownFormat;
    }

    public void setMarkDownFormat(MarkDownFormat markDownFormat) {
        this.markDownFormat = markDownFormat;
    }
}
