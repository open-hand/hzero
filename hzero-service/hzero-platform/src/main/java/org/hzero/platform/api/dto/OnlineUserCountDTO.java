package org.hzero.platform.api.dto;

/**
 * 在线用户统计DTO
 *
 * @author fanghan.liu 2019/12/17 17:50
 */
public class OnlineUserCountDTO {

    private String hour;

    private Long quantity;

    public OnlineUserCountDTO(String hour, Long quantity) {
        this.hour = hour;
        this.quantity = quantity;
    }

    public OnlineUserCountDTO() {
    }

    public String getHour() {
        return hour;
    }

    public OnlineUserCountDTO setHour(String hour) {
        this.hour = hour;
        return this;
    }

    public Long getQuantity() {
        return quantity;
    }

    public OnlineUserCountDTO setQuantity(Long quantity) {
        this.quantity = quantity;
        return this;
    }

    @Override
    public String toString() {
        return "OnlineUserCountDTO{" +
                "hour='" + hour + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
