
package com.practice.minibank.exception;

public class DuplicateEmailException extends RuntimeException {

	public DuplicateEmailException(String email) {
		super("A customer already exists with email: " + email);
	}
}
