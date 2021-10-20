package com.security.jwt.service;

import com.security.jwt.domain.Role;
import com.security.jwt.domain.User;
import com.security.web.v1.model.UserDto;
import com.security.web.v1.model.UserPagedList;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface UserService {
    UserDto saveUser(User user);
    UserDto updateUser(UUID id, UserDto userDto);
    UserDto getUserByUsername(String username);
    UserPagedList getUsersPerPage(PageRequest pageRequest);
    UserDto addRoleToUser(String username, String roleName);
}
