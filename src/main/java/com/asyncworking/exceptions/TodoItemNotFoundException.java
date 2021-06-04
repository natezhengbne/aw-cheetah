package com.asyncworking.exceptions;

public class TodoItemNotFoundException extends RuntimeException{
    public TodoItemNotFoundException(String message) {
        super(message);
    }
}
