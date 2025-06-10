package com.api.backendCCEP.Util;

public class ApiResponse<T> {

	private boolean success;
	private String message;
	private T data;
	private int code;

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

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public ApiResponse() {
		super();
	}

	public ApiResponse(boolean success, String message, T data, int code) {
		super();
		this.success = success;
		this.message = message;
		this.data = data;
		this.code = code;
	}

}
