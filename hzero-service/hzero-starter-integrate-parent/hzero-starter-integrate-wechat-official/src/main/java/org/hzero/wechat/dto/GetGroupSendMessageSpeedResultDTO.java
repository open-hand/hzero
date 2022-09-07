package org.hzero.wechat.dto;

public class GetGroupSendMessageSpeedResultDTO {
    private Long speed;
    private Long realspeed;

    public Long getSpeed() {
        return speed;
    }

    public void setSpeed(Long speed) {
        this.speed = speed;
    }

    public Long getRealspeed() {
        return realspeed;
    }

    public void setRealspeed(Long realspeed) {
        this.realspeed = realspeed;
    }
}
