package com.asyncworking.exceptions;

public class TodoBoardNotFoundException extends RuntimeException{
    public TodoBoardNotFoundException(String message) {
        super(message);
    }
}
