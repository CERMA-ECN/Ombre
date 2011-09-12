package fr.ecn.ombre.android.utils;

public class ValidationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ValidationException() {
	}

	public ValidationException(String detailMessage) {
		super(detailMessage);
	}

	public ValidationException(Throwable throwable) {
		super(throwable);
	}

	public ValidationException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
