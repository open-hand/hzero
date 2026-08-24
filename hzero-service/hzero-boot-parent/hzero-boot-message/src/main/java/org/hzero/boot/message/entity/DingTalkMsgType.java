package org.hzero.boot.message.entity;

import org.apache.commons.lang3.StringUtils;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/09/24 13:54
 */
public enum DingTalkMsgType {

    /**
     * 文本消息
     */
    TEXT("text"),
    /**
     * 图片消息
     */
    IMAGE("image"),
    /**
     * 语音消息
     */
    VOICE("voice"),
    /**
     * 文件消息
     */
    FILE("file"),
    /**
     * 链接消息
     */
    LINK("link"),
    /**
     * OA消息
     */
    OA("oa"),
    /**
     * 卡片消息
     */
    ACTION_CARD("action_card"),
    /**
     * markdown
     */
    MARK_DOWN("markdown");

    private final String value;

    DingTalkMsgType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static DingTalkMsgType getType(String value) {
        if (StringUtils.isBlank(value)) {
            return MARK_DOWN;
        }
        switch (value) {
            case "text":
                return TEXT;
            case "image":
                return IMAGE;
            case "voice":
                return VOICE;
            case "file":
                return FILE;
            case "link":
                return LINK;
            case "oa":
                return OA;
            case "action_card":
                return ACTION_CARD;
            case "markdown":
            default:
                return MARK_DOWN;
        }
    }
}
