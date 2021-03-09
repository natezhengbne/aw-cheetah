package com.asyncworking.exceptions;

public class NoCompanyWithSuchUserException extends Throwable{
    public NoCompanyWithSuchUserException(String message) {
        super(message);
    }
}
