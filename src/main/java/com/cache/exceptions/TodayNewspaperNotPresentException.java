package com.cache.exceptions;

public class TodayNewspaperNotPresentException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TodayNewspaperNotPresentException(String message) {
		super(message);
	}

}
