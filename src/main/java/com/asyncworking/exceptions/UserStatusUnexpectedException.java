package com.asyncworking.exceptions;

import com.asyncworking.constants.Status;

public class UserStatusUnexpectedException extends RuntimeException{

    public UserStatusUnexpectedException (Status expected, Status actual) {
        super("Expect the status of user to be " + expected.toString() + ", actual it is " + actual.toString());
    }

    public UserStatusUnexpectedException (String message) {
        super(message);
    }
}
