package com.security.web.v1.controller;

import com.security.jwt.domain.User;
import com.security.jwt.service.UserService;
import com.security.web.v1.model.UserDto;
import com.security.web.v1.model.UserPagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


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

    private Integer pageNumberValid(Integer pageNumber){
        if(pageNumber==null || pageNumber < 0) return DEFAULT_PAGE_NUMBER;
        else return pageNumber;
    }

    private Integer pageSizeValid(Integer pageSize){
        if(pageSize==null || pageSize < 0) return DEFAULT_PAGE_SIZE;
        else return pageSize;
    }
}
