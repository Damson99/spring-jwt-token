package com.security.jwt.service;

import com.security.jwt.domain.Role;
import com.security.jwt.domain.User;
import com.security.jwt.repository.RoleRepository;
import com.security.jwt.repository.UserRepository;
import com.security.jwt.utils.ObjectOperations;
import com.security.web.exceptions.*;
import com.security.web.v1.mapper.UserMapper;
import com.security.web.v1.model.UserDto;
import com.security.web.v1.model.UserPagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl extends ObjectOperations implements UserService {
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
    @Transactional
//    todo pipeline with CompletableFuture - async
    public UserDto updateUser(UUID id, UserDto userDto) {
        throwExceptionIfObjectEmpty(userDto, "invalid user: ");
        Optional<User> isUserExists = Optional.ofNullable(userRepository.findById(userDto.getId()).orElseThrow(() ->
                new UserNotFoundException("user "+userDto.getId() + " not found.")));

        if(isUserExists.isPresent()){
            User userExists = isUserExists.get();
            if(!userDto.getEmail().isEmpty() && userDto.getEmail().length()>3
                    && !userExists.getEmail().equals(userDto.getEmail())){
                Optional<User> userEmailExists = userRepository.findByEmail(userDto.getEmail());
                if(userEmailExists.isPresent())
                    throw new UserAlreadyExistsException("email "+userDto.getEmail()+" is already taken");
                else
                    userExists.setEmail(userDto.getEmail());
            }

            if(!userDto.getUsername().isEmpty() && userDto.getUsername().length()>3
                    && !userExists.getUsername().equals(userDto.getUsername())){
                Optional<User> userUsernameExists = userRepository.findByUsername(userDto.getUsername());
                if(userUsernameExists.isPresent())
                    throw new UserAlreadyExistsException("username "+userDto.getUsername()+" is already taken");
                else
                    userExists.setEmail(userDto.getUsername());
            }

            if(!userDto.getPhone().isEmpty() && userDto.getPhone().replaceAll("\\s","").length()!=9
                    && !userExists.getPhone().equals(userDto.getPhone())){
                Optional<User> userPhoneExists = userRepository.findByPhone(userDto.getPhone());
                if(userPhoneExists.isPresent())
                    throw new UserAlreadyExistsException("phone "+userDto.getPhone()+" is already taken");
                else
                    userExists.setEmail(userDto.getPhone());
            }
            return userMapper.userToUserDto(userRepository.save(userMapper.userDtoToUser(userDto)));
        }
        return userDto;
    }

    @Override
    public UserDto getUserByUsername(String username) {
        throwExceptionIfObjectEmpty(username, "invalid username: ");
        return userMapper.userToUserDto(userRepository.findByUsername(username).orElseThrow(()->
                new UserNotFoundException(username+" not found.")));
    }

    @Override
    public UserPagedList getUsersPerPage(PageRequest pageRequest) {
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
//    todo pipeline with CompletableFuture - async
    public UserDto addRoleToUser(String username, String roleName) {
        throwExceptionIfObjectEmpty(username, "invalid username: ");
        throwExceptionIfObjectEmpty(roleName, "invalid role name: ");

        UserDto userDto = userMapper.userToUserDto(userRepository.findByUsername(username).orElseThrow(()->
                new UserNotFoundException(username+" not found.")));
        Role role = roleRepository.findByName(roleName).orElseThrow(()->
                new RoleNotFoundException(roleName+" not found."));
        userDto.getRoles().add(role);

        return userMapper.userToUserDto(userRepository.save(userMapper.userDtoToUser(userDto)));
    }
}
