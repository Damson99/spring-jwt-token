package com.security.jwt.utils;

import com.security.web.exceptions.InvalidCredentialsException;
import org.springframework.util.ObjectUtils;

public abstract class ObjectOperations {
    public void throwExceptionIfObjectEmpty(Object obj, String s){
        if(ObjectUtils.isEmpty(obj))
            invalidCredentialsException(s+obj.toString());
    }

    public void invalidCredentialsException(String s){
        throw new InvalidCredentialsException(s);
    }
}
