package io.choerodon.core.event;

/**
 * 调用端提供的回查接口返回的实体
 *
 * @author flyleft
 *      当业务A服务调用event store服务创建了服务后出现异常，没有确认或者取消，
 *      此时event store服务会向业务A服务进行回查，此类为业务A服务提供的回查接口返回的实体。
 */
public class EventBackCheckRecord {

    private String uuid;

    private EventStatus status;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public EventBackCheckRecord() {
    }

    public EventBackCheckRecord(String uuid, EventStatus status) {
        this.uuid = uuid;
        this.status = status;
    }

    @Override
    public String toString() {
        return "EventBackCheckRecord{" +
                "uuid='" + uuid + '\'' +
                ", status=" + status +
                '}';
    }
}
