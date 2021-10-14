package com.security.jwt.service;

import com.security.jwt.domain.Role;
import com.security.jwt.domain.User;
import com.security.jwt.repository.RoleRepository;
import com.security.jwt.repository.UserRepository;
import com.security.web.exceptions.InvalidCredentialsException;
import com.security.web.exceptions.RoleNotFoundException;
import com.security.web.exceptions.UserNotFoundException;
import com.security.web.v1.mapper.UserMapper;
import com.security.web.v1.model.UserDto;
import com.security.web.v1.model.UserPagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.transaction.Transactional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto saveUser(User user) {
        throwExceptionIfObjectEmpty(user, "invalid user: ");
        return userMapper.userToUserDto(userRepository.save(user));
    }

    @Override
    public UserDto getUser(String username) {
        throwExceptionIfObjectEmpty(username, "invalid username: ");
        return userMapper.userToUserDto(userRepository.findByUsername(username).orElseThrow(()->
                new UserNotFoundException(username+" not found.")));
    }

    @Override
    public UserPagedList usersPerPage(PageRequest pageRequest) {
        return buildPagedList(userRepository.findAll(pageRequest));
    }

    private UserPagedList buildPagedList(Page<User> userPage) {
        return new UserPagedList(userPage.getContent().stream().map(userMapper::userToUserDto).collect(Collectors.toList()),
                PageRequest.of(userPage.getPageable().getPageNumber(),
                        userPage.getPageable().getPageSize()),
                userPage.getTotalElements());
    }

    @Override
    @Transactional
    public Role saveRole(Role role) {
        throwExceptionIfObjectEmpty(role, "invalid role: ");
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public void addRoleToUser(String username, String roleName) {
        throwExceptionIfObjectEmpty(username, "invalid username: ");
        throwExceptionIfObjectEmpty(roleName, "invalid role name: ");

        UserDto userDto = userMapper.userToUserDto(userRepository.findByUsername(username).orElseThrow(()->
                new UserNotFoundException(username+" not found.")));
        Role role = roleRepository.findByName(roleName).orElseThrow(()->
                new RoleNotFoundException(roleName+" not found."));
        userDto.getRoles().add(role);

        userRepository.save(userMapper.userDtoToUser(userDto));
    }

    private void throwExceptionIfObjectEmpty(Object obj, String s){
        if(!ObjectUtils.isEmpty(obj))
            invalidCredentialsException(s+obj.toString());
    }

    private void invalidCredentialsException(String s){
        throw new InvalidCredentialsException(s);
    }
}
