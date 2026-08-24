package org.hzero.platform.api.dto.commontemplate;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

/**
 * 渲染结果的默认实现
 *
 * @author bergturing 2020/08/05 10:19
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RenderResult {
    /**
     * 渲染结果标识
     */
    private final Boolean success;

    /**
     * 渲染结果内容
     */
    private final String content;

    /**
     * 渲染结果消息
     */
    private final String message;

    private RenderResult(Boolean success, String content, String message) {
        this.success = success;
        this.content = content;
        this.message = message;
    }

    /**
     * 渲染成功
     *
     * @param content 渲染结果内容
     * @return 渲染结果对象
     */
    public static RenderResult success(String content) {
        return new RenderResult(true, content, null);
    }

    /**
     * 渲染失败
     *
     * @param message 渲染失败消息
     * @return 渲染结果对象
     */
    public static RenderResult failure(String message) {
        return new RenderResult(false, null, message);
    }

    public Boolean isSuccess() {
        return Objects.equals(Boolean.TRUE, this.success);
    }

    public String getContent() {
        return this.content;
    }

    public String getMessage() {
        return this.message;
    }
}
