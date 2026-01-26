package com.demo.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResponse<T> implements Serializable {

    private boolean success;
    private String code;
    private String message;
    private T data;
    public BaseResponse() {
    }
    public BaseResponse(String code, String message) {
        this(true, code, message, null);
    }
    public BaseResponse(T data) {
        this(true, "200", "Request successful.", data);
    }
    private BaseResponse(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public static <T> BaseResponse<T> success() {
        return new BaseResponse<>(true, "200", "Operation successful.", null);
    }
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(true, "200", "Operation successful.", data);
    }

    public static <T> BaseResponse<T> error(String code, String message) {
        return new BaseResponse<>(false, code, message, null);
    }
}
