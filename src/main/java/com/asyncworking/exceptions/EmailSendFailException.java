package com.asyncworking.exceptions;

public class EmailSendFailException extends RuntimeException {
    public EmailSendFailException(Exception e) {
        super("Email send failed because:" + e.getMessage());
    }
}
