package es.upo.tfg.rol.exceptions;

public class NotAuthorized extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotAuthorized() {
		// TODO Auto-generated constructor stub
	}

	public NotAuthorized(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public NotAuthorized(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public NotAuthorized(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public NotAuthorized(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
