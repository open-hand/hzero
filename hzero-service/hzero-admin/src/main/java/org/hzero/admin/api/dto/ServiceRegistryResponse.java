package org.hzero.admin.api.dto;

import javax.annotation.Nullable;

/**
 * @author XCXCXCXCX
 * @date 2020/6/10 10:05 上午
 */
public class ServiceRegistryResponse<T> {

    private boolean success;

    private T body;

    private String message;

    private ServiceRegistryResponse(T body) {
        this.body = body;
    }

    public static <T> ServiceRegistryResponse<T> create() {
        return create(null);
    }

    public static <T> ServiceRegistryResponse<T> create(@Nullable T body) {
        return new ServiceRegistryResponse<>(body);
    }

    public boolean isSuccess() {
        return success;
    }

    public ServiceRegistryResponse<T> setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public T getBody() {
        return body;
    }

    public ServiceRegistryResponse<T> setBody(T body) {
        this.body = body;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ServiceRegistryResponse<T> setMessage(String message) {
        this.message = message;
        return this;
    }
}
