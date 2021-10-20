package com.security.web.exceptions;

public class UserAlreadyExistsException extends RuntimeException
{
    public UserAlreadyExistsException(String s) {
        super(s);
    }
}