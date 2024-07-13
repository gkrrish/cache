package com.cache.exceptions;

public class ActiveUsersNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ActiveUsersNotFoundException(String message) {
		super(message);
	}

}
