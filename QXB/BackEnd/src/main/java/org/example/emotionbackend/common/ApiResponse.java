package org.example.emotionbackend.common;


import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private Integer code;
    private String message;
    private T data;

    public ApiResponse() {}

    public ApiResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200,"success",null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200,"success",data);
    }

    public static <T> ApiResponse<T> failed(String msg) {
        return new ApiResponse<>(400,msg,null);
    }

    public static <T> ApiResponse<T> failed(Integer code,String msg) {
        return new ApiResponse<>(code,msg,null);
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public ApiResponse<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    public ApiResponse<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public ApiResponse<T> setData(T data) {
        this.data = data;
        return this;
    }
}
