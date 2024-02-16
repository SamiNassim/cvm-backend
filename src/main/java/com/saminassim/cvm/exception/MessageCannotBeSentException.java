package com.saminassim.cvm.exception;

public class MessageCannotBeSentException extends RuntimeException {
    public MessageCannotBeSentException(String message) {
        super(message);
    }
}