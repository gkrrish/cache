package com.cache.exceptions;

public class RedisDataRetrivingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RedisDataRetrivingException(String message) {
		super(message);
	}
}
