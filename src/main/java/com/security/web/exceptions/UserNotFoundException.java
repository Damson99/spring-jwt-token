package com.security.web.exceptions;

public class UserNotFoundException extends RuntimeException
{
    public UserNotFoundException(String s) {
        super(s);
    }
}
