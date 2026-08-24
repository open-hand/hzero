package org.hzero.boot.platform.event.vo;

import org.hzero.boot.platform.event.EventScheduler;

import java.util.Map;

/**
 * 事件调度参数，用于限制 {@link EventScheduler#scheduler} 只能传入 {@link MethodParam} 或者 {@link ApiParam}
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/15 11:26
 * @see MethodParam
 * @see ApiParam
 * @see WebHookParam
 */
public interface EventParam extends Map<String, Object> {

    /**
     * 获取参数类型
     *
     * @return 参数类型
     */
    String getType();
}
