package org.hzero.boot.platform.event.provider;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * 调度结果
 *
 * @author bergturing 2020/08/11 11:21
 */
public class ScheduleResult {
    /**
     * 是否调度成功
     */
    private final boolean success;

    /**
     * 调度结果
     */
    private final Object result;

    /**
     * 调度失败消息
     */
    private final String message;

    private ScheduleResult(boolean success, Object result, String message) {
        this.success = success;
        this.result = result;
        this.message = message;
    }

    /**
     * 成功
     *
     * @param result 结果对象
     * @return 调用结果对象
     */
    public static ScheduleResult success(Object result) {
        return new ScheduleResult(true, result, null);
    }

    /**
     * 失败
     *
     * @param message 失败消息
     * @param args    消息参数
     * @return 调用结果对象
     */
    public static ScheduleResult failure(String message, Object... args) {
        return new ScheduleResult(false, null, String.format(message, args));
    }

    /**
     * 处理结果
     *
     * @param success 成功时处理逻辑
     * @param failure 失败时的处理逻辑
     * @return 成功时返回结果，失败时返回null
     */
    public Object result(Consumer<Object> success, Consumer<String> failure) {
        if (Objects.nonNull(this.result)) {
            if (this.success && Objects.nonNull(success)) {
                success.accept(this.result);
            } else if (Objects.nonNull(failure)) {
                failure.accept(this.message);
            }
        }

        return this.result;
    }
}
