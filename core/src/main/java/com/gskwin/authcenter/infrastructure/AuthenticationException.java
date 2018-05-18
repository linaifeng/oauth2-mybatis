package com.gskwin.authcenter.infrastructure;

public class AuthenticationException extends RuntimeException {

	private static final long serialVersionUID = -4219322467510732847L;

	private String error;

	/**
	 * Creates a new AuthenticationException.
	 */
	public AuthenticationException() {
		super();
	}

	/**
	 * Constructs a new AuthenticationException.
	 *
	 * @param message
	 *            the reason for the exception
	 */
	public AuthenticationException(String message) {
		super(message);
	}

	public AuthenticationException(String message, String error) {
		super(message);
		this.error = error;
	}

	/**
	 * Constructs a new AuthenticationException.
	 *
	 * @param cause
	 *            the underlying Throwable that caused this exception to be
	 *            thrown.
	 */
	public AuthenticationException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new AuthenticationException.
	 *
	 * @param message
	 *            the reason for the exception
	 * @param cause
	 *            the underlying Throwable that caused this exception to be
	 *            thrown.
	 */
	public AuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
