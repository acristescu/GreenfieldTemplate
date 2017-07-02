package io.zenandroid.greenfield.event;

/**
 * Created by acristescu on 24/06/2017.
 */

/**
 * Represents an error that occurred during communication with the backend.
 */
public class ApiError {
	private String message;
	private Throwable cause;
	private Class<?> responseClass;

	public ApiError() {
	}

	public ApiError(String message, Throwable cause, Class<?> responseClass) {
		this.message = message;
		this.cause = cause;
		this.responseClass = responseClass;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Throwable getCause() {
		return cause;
	}

	public void setCause(Throwable cause) {
		this.cause = cause;
	}

	public Class<?> getResponseClass() {
		return responseClass;
	}

	public void setResponseClass(Class<?> responseClass) {
		this.responseClass = responseClass;
	}
}
