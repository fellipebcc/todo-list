package br.edu.unifalmg.exception;

public class ChoreAlreadyCompletedException extends RuntimeException {
    public ChoreAlreadyCompletedException(String message) {
        super(message);
    }
}
