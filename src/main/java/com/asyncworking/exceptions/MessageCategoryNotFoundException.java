package com.asyncworking.exceptions;

public class MessageCategoryNotFoundException extends RuntimeException{
    public MessageCategoryNotFoundException(String message) {
        super(message);
    }
}
