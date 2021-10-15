package com.security.jwt.service;

import com.security.jwt.domain.Role;
import com.security.jwt.domain.User;
import com.security.web.v1.model.UserDto;
import com.security.web.v1.model.UserPagedList;
import org.springframework.data.domain.PageRequest;

public interface UserService {
    UserDto saveUser(User user);
    UserDto getUser(String username);
    UserPagedList getUsersPerPage(PageRequest pageRequest);
    Role saveRole(Role role);
    void addRoleToUser(String username, String roleName);
}
