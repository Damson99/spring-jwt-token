package com.security.jwt.constant;

import com.auth0.jwt.algorithms.Algorithm;

public class Constants {
    public final static String ROLE_ADMIN = "ROLE_ADMIN";
    public final static String LOGIN_PATH = "/api/v1/login";
    public static final String SECRET = "secret";
    public static final String ROLES = "roles";
    public static final int EXPIRATION_TIME = 86_400_000; //24 hours
    public static final int REFRESH_EXPIRATION_TIME = 172_800_000; // 48 hours
    public static final String BEARER_HEADER="Bearer ";
    public static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET.getBytes());
}
