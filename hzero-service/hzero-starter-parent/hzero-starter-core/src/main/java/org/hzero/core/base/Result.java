package org.hzero.core.base;

import java.io.Serializable;

import org.hzero.core.message.MessageAccessor;

/**
 * 封装返回结果
 *
 * @author bojiangzhou 2019/05/13
 */
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 812868134008268146L;

    private boolean success;
    private String code;
    private String message;
    private T data;

    public Result() {
    }

    public Result(boolean success, String code) {
        this.success = success;
        this.code = code;
        this.message = MessageAccessor.getMessage(code).desc();
    }

    public Result(boolean success, T data) {
        this.success = success;
        this.code = "success";
        this.data = data;
    }

    public Result(boolean success, String code, T data) {
        this.success = success;
        this.code = code;
        this.message = MessageAccessor.getMessage(code).desc();
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
