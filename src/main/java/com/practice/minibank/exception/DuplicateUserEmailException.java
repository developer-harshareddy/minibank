
package com.practice.minibank.exception;

public class DuplicateUserEmailException extends RuntimeException {

	public DuplicateUserEmailException() {
		super("An account with this email already exists");
	}
}
