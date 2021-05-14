package com.asyncworking.exceptions;

public class TodoListNotFoundException extends RuntimeException {
    public TodoListNotFoundException(String message) {
        super(message);
    }
}
