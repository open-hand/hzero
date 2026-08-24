package org.hzero.platform.domain.vo;

/**
 * 后端消息对象，添加到缓存
 *
 * @author xiaoyu.zhao@hand-china.com 2019/01/09 9:02
 */
public class MessageVO {

    private String type;
    private String desc;
    private String code;

    public MessageVO() {
    }

    public MessageVO(String type, String desc, String code) {
        this.type = type;
        this.desc = desc;
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override public String toString() {
        return "MessageVO{" + "type='" + type + '\'' + ", desc='" + desc + '\'' + ", code='" + code + '\'' + '}';
    }
}
