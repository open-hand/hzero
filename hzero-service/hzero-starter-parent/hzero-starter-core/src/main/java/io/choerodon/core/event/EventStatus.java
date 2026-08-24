package io.choerodon.core.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author flyleft
 */
public enum EventStatus {

    /**
     * 事件已确认
     */
    CONFIRMED(1, "confirmed"),

    /**
     * 事件已取消
     */
    CANCELED(2, "canceled"),;


    @JsonCreator
    public static EventStatus getItem(int code) {
        for (EventStatus item : values()) {
            if (item.code == code) {
                return item;
            }
        }
        return null;
    }

    EventStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    private int code;

    private String name;


    @JsonValue
    public int getCode() {
        return code;
    }


    public String getName() {
        return name;
    }
}

