package com.asyncworking.exceptions;

public class MessageBoardNotFoundException extends RuntimeException{
    public MessageBoardNotFoundException(String message) {
        super(message);

    }
}
