package com.security.web.exceptions;

public class InvalidCredentialsException extends RuntimeException
{
    public InvalidCredentialsException(String s) {
        super(s);
    }
}
