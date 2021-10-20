package com.security.web.v1.controller;

import com.security.jwt.domain.Role;
import com.security.jwt.service.RoleService;
import com.security.web.v1.model.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/role")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<Role> saveRole(@RequestBody Role role){
        return new ResponseEntity<>(roleService.saveRole(role), HttpStatus.CREATED);
    }
}
