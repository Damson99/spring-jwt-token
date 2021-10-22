package com.security.jwt.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static com.security.jwt.constant.Constants.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class AuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getServletPath().equals(LOGIN_PATH) || request.getServletPath().equals(REFRESH_TOKEN_PATH)){
            filterChain.doFilter(request, response);
        }
        else {
            String authorizationHeader=request.getHeader(AUTHORIZATION);
            if(authorizationHeader!=null && authorizationHeader.startsWith(BEARER_HEADER)){
                try{
                    String token=authorizationHeader.substring(BEARER_HEADER.length());
                    JWTVerifier verifier=JWT.require(ALGORITHM).build();
                    DecodedJWT decodedJWT=verifier.verify(token);
                    String username=decodedJWT.getSubject();
                    String[] roles=decodedJWT.getClaim(ROLES).asArray(String.class);
                    Collection<SimpleGrantedAuthority> authorities=new ArrayList<>();
                    Arrays.stream(roles).forEach(role ->
                            authorities.add(new SimpleGrantedAuthority(role)));

                    UsernamePasswordAuthenticationToken authenticationToken=
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);
                }
                catch (Exception ex){
                    log.error("Error in authorization filter: "+ex.getMessage());
                    response.setHeader("Error", ex.getMessage());
                    response.setStatus(FORBIDDEN.value());
                    Map<String, String> error=new HashMap<>();
                    error.put("error_message", ex.getMessage());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }
            }
            else {
                filterChain.doFilter(request, response);
            }
        }
    }
}
