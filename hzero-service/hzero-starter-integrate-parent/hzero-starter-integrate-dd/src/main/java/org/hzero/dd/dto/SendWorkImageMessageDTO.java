package org.hzero.dd.dto;

public class SendWorkImageMessageDTO extends SendWorkMessageDTO {

  private ImageFormat mag;

    public ImageFormat getMag() {
        return mag;
    }

    public void setMag(ImageFormat mag) {
        this.mag = mag;
    }
}
