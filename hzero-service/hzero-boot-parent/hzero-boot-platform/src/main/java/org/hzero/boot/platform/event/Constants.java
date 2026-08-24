package org.hzero.boot.platform.event;

/**
 * 常量
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/13 10:56
 */
public interface Constants {
    /**
     * 事件存储缓存Key
     */
    String EVENT_KEY = "hpfm:event";

    /**
     * 事件调用类型
     */
    interface CallType {
        /**
         * 方法
         */
        String METHOD = "M";
        /**
         * API
         */
        String API = "A";
        /**
         * WebHook
         */
        String WEB_HOOK = "W";
    }

    /**
     * 1/0
     */
    interface Flag {
        /**
         * 1
         */
        Integer YES = 1;
        /**
         * 0
         */
        Integer NO = 0;
    }
}
