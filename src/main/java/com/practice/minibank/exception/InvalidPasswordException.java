
package com.practice.minibank.exception;

public class InvalidPasswordException extends RuntimeException {

	public InvalidPasswordException() {
		super("Password exceeds the maximum supported length");
	}
}
