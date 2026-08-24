package org.hzero.boot.message.entity;

import org.apache.commons.lang3.StringUtils;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/09/24 13:55
 */
public enum WeChatMsgType {

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
     * 视频消息
     */
    VIDEO("video"),
    /**
     * 文件消息
     */
    FILE("file"),
    /**
     * 文本卡片消息
     */
    TEXT_CARD("textcard"),
    /**
     * 图文消息
     */
    NEWS("news"),
    /**
     * 图文消息
     */
    MP_NEWS("mpnews"),
    /**
     * markdown
     */
    MARK_DOWN("markdown");

    private final String value;

    WeChatMsgType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static WeChatMsgType getType(String value) {
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
            case "video":
                return VIDEO;
            case "file":
                return FILE;
            case "textcard":
                return TEXT_CARD;
            case "news":
                return NEWS;
            case "mpnews":
                return MP_NEWS;
            case "markdown":
            default:
                return MARK_DOWN;
        }
    }
}
