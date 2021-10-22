package com.security.web.exceptions;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String s) {
        super(s);
    }
}
