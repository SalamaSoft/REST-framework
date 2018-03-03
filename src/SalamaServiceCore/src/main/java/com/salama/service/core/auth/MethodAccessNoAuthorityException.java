package com.salama.service.core.auth;

/**
 * 
 * @author XingGu Liu
 *
 */
public class MethodAccessNoAuthorityException extends Exception {
	
	private static final long serialVersionUID = -155288557485762474L;

	public MethodAccessNoAuthorityException() {
		super();
	}
	
	public MethodAccessNoAuthorityException(String errorMsg) {
		super(errorMsg);
	}
	
	public MethodAccessNoAuthorityException(Throwable e) {
		super(e);
	}
	
	public MethodAccessNoAuthorityException(String errorMsg, Throwable e) {
		super(errorMsg, e);
	}

}
