package com.security.web.v1.controller;

import com.security.jwt.domain.User;
import com.security.jwt.service.UserService;
import com.security.web.v1.model.UserPagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final static Integer DEFAULT_PAGE_SIZE=25;
    private final static Integer DEFAULT_PAGE_NUMBER=0;

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
}
