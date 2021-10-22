package com.security.web.exceptions;

public class RoleAlreadyExistsException extends RuntimeException {
    public RoleAlreadyExistsException(String s) {
        super(s);
    }
}