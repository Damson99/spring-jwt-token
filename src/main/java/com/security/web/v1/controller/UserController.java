package com.security.web.v1.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.jwt.domain.Role;
import com.security.jwt.domain.User;
import com.security.jwt.service.UserService;
import com.security.web.exceptions.RefreshTokenMissingException;
import com.security.web.v1.model.UserDto;
import com.security.web.v1.model.UserPagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.security.jwt.constant.Constants.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final static Integer DEFAULT_PAGE_SIZE=25;
    private final static Integer DEFAULT_PAGE_NUMBER=0;


    @GetMapping(path = "/{username}", produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<UserDto> getUserByUsername(@RequestParam("username") String username){
        return new ResponseEntity<>(userService.getUserByUsername(username), HttpStatus.OK);
    }

    @GetMapping(produces = {"application/json"})
    public ResponseEntity<UserPagedList> getUsersPerPage(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                         @RequestParam(value = "pageSize", required = false) Integer pageSize){
        pageNumber=pageNumberValid(pageNumber);
        pageSize=pageSizeValid(pageSize);
        log.debug("Finding users for the "+pageNumber+" page with "+pageSize+" records");
        return new ResponseEntity<>(userService.getUsersPerPage(PageRequest.of(pageNumber, pageSize)),
                HttpStatus.OK);
    }

    private Integer pageNumberValid(Integer pageNumber){
        if(pageNumber==null || pageNumber < 0) return DEFAULT_PAGE_NUMBER;
        else return pageNumber;
    }

    private Integer pageSizeValid(Integer pageSize){
        if(pageSize==null || pageSize < 0) return DEFAULT_PAGE_SIZE;
        else return pageSize;
    }

//    todo refactor code. The same code is in the AuthorizationFilter
    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader=request.getHeader(AUTHORIZATION);
        if(authorizationHeader!=null && authorizationHeader.startsWith(BEARER_HEADER)){
            try{
                String refreshToken=authorizationHeader.substring(BEARER_HEADER.length());
                JWTVerifier verifier=JWT.require(ALGORITHM).build();
                DecodedJWT decodedJWT=verifier.verify(refreshToken);
                String username=decodedJWT.getSubject();
                UserDto userDto=userService.getUserByUsername(username);

                String accessToken=JWT.create()
                        .withSubject(userDto.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim(ROLES, userDto.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
//                        .withClaim(ROLES, ROLE_ADMIN)
                        .sign(ALGORITHM);

                Map<String, String> tokens=new HashMap<>();
                tokens.put("access_token", accessToken);
                tokens.put("refresh_token", refreshToken);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            }
            catch (Exception ex){
                log.error("Error in authorization filter: "+ex.getMessage());
                response.setHeader("error", ex.getMessage());
                response.setStatus(FORBIDDEN.value());
                Map<String, String> error=new HashMap<>();
                error.put("error_message", ex.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        }
        else{
            throw new RefreshTokenMissingException("Refresh token is missing");
        }
    }

    @PostMapping(produces = {"application/json"})
    public ResponseEntity<UserDto> saveUser(@RequestBody @Validated User user){
        return new ResponseEntity<>(userService.saveUser(user), HttpStatus.CREATED);
    }

    @PostMapping(path = "/role/{roleName}/{username}", produces = {"application/json"})
    public ResponseEntity<UserDto> addRoleToUser(@PathVariable("roleName") String roleName,
                                                 @PathVariable("username") String username){
        return new ResponseEntity<>(userService.addRoleToUser(username, roleName), HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/{id}", produces = {"application/json"})
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") UUID id, @RequestBody @Validated UserDto userDto){
        return new ResponseEntity<>(userService.updateUser(id, userDto), HttpStatus.NO_CONTENT);
    }
}
