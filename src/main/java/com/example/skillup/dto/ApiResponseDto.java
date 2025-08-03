package com.example.skillup.dto;

public class ApiResponseDto<T> {
    
    private boolean success;
    private String message;
    private T data;
    private String error;
    
    public ApiResponseDto() {}
    
    public ApiResponseDto(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    public static <T> ApiResponseDto<T> success(T data) {
        return new ApiResponseDto<>(true, "Success", data);
    }
    
    public static <T> ApiResponseDto<T> success(String message, T data) {
        return new ApiResponseDto<>(true, message, data);
    }
    
    public static <T> ApiResponseDto<T> success(String message) {
        return new ApiResponseDto<>(true, message, null);
    }
    
    public static <T> ApiResponseDto<T> error(String message) {
        ApiResponseDto<T> response = new ApiResponseDto<>(false, message, null);
        response.setError(message);
        return response;
    }
    
    public static <T> ApiResponseDto<T> error(String message, String error) {
        ApiResponseDto<T> response = new ApiResponseDto<>(false, message, null);
        response.setError(error);
        return response;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
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
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
}
