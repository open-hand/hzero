package org.hzero.plugin.platform.hr.infra.enums;

/**
 * 消息查询返回参数类型枚举
 *
 * @author xiaoyu.zhao@hand-china.com 2019/06/14 10:08
 */
public enum ReceiverTypeCode {

    /**
     * 用户邮箱
     */
    EMAIL("EMAIL"),
    /**
     * 用户手机号
     */
    PHONE("PHONE"),
    /**
     * 国际冠码
     */
    IDD("IDD");

    private final String value;

    ReceiverTypeCode(final String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
