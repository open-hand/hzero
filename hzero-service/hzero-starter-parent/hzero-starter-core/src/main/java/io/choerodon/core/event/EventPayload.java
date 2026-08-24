package io.choerodon.core.event;

/**
 * @author flyleft
 * 2018/4/10
 */
public class EventPayload <T> {

    private String uuid;

    private String businessType;

    private T data;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "EventPayload{"
                + "uuid='" + uuid + '\''
                + ", businessType='" + businessType + '\''
                + ", data=" + data
                + '}';
    }

    public EventPayload(String businessType, T data) {
        this.businessType = businessType;
        this.data = data;
    }

    public EventPayload() {
    }
}