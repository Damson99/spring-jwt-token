package com.security.web.exceptions;


public class RefreshTokenMissingException extends RuntimeException {
    public RefreshTokenMissingException(String s) {
        super(s);
    }
}
