package com.fyodor.exception;

public class OutOfMenuRangeException extends RuntimeException{
    public OutOfMenuRangeException(String message) {
        super(message);
    }
}
