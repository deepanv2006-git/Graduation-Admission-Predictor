package com.universitypredictor.exception;

/**
 * Custom exception thrown when user input fails business rule validation.
 * 
 * In an interview, explain:
 * "InvalidInputException is a custom runtime exception that represents
 * invalid business parameters (such as a GRE score outside 260-340).
 * It encapsulates the specific error message and lets our API return
 * a clean HTTP 400 Bad Request to the client without crashing the server."
 */
public class InvalidInputException extends RuntimeException {

    /**
     * Constructs a new InvalidInputException with a descriptive error message.
     * 
     * @param message Plain-English description of the validation failure.
     */
    public InvalidInputException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidInputException with a descriptive error message and underlying cause.
     * 
     * @param message Plain-English description of the validation failure.
     * @param cause The underlying throwable cause.
     */
    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
