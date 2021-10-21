package com.security.jwt.service;

import com.security.jwt.domain.Role;
import com.security.jwt.domain.User;
import com.security.jwt.domain.UserStatus;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl extends ObjectOperations implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        throwExceptionIfObjectEmpty(username, "invalid username: ");
        Optional<User> userExists = userRepository.findByUsername(username);
        if(userExists.isEmpty())
            throw new UsernameNotFoundException("user "+username+" does not exist.");

        User user = userExists.get();
        log.debug("user load by username: "+username);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role ->
                authorities.add(new SimpleGrantedAuthority(role.getName())));

        return new org.springframework.security.core.userdetails.User(username,
                user.getPassword(), authorities);
    }



    @Override
    @Transactional
//    todo pipeline with CompletableFuture - async
    public UserDto saveUser(User user) {
        throwExceptionIfObjectEmpty(user, "invalid user: ");
        isUserWithEmailExists(user, user.getEmail());
        isUserWithUsernameExists(user, user.getUsername());
        isUserWithPhoneExists(user, user.getPhone());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.debug("saving new user: "+user.getUsername());
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
            UserStatus userStatus=UserStatus.NONE;
            User userExists = isUserExists.get();
            if(!userExists.getEmail().equals(userDto.getEmail())){
                isUserWithEmailExists(userExists, userDto.getEmail());
                userStatus=UserStatus.UPDATE;
            }
            if(!userExists.getUsername().equals(userDto.getUsername())){
                isUserWithUsernameExists(userExists, userDto.getUsername());
                userStatus=UserStatus.UPDATE;
            }
            if(!userExists.getPhone().equals(userDto.getPhone())){
                isUserWithPhoneExists(userExists, userDto.getPhone());
                userStatus=UserStatus.UPDATE;
            }

            if(userStatus==UserStatus.UPDATE){
                log.debug("updating user with username: "+userDto.getUsername());
                return userMapper.userToUserDto(userRepository.save(userMapper.userDtoToUser(userDto)));
            }
        }
        log.debug("user credentials are the same as before for: "+userDto.getUsername());
        return userDto;
    }

    private void isUserWithEmailExists(User userExists, String email){
        if(!email.isEmpty() && email.length()>3){
            Optional<User> userEmailExists = userRepository.findByEmail(email);
            if(userEmailExists.isPresent())
                throw new UserAlreadyExistsException("email "+email+" is already taken");
            else
                userExists.setEmail(email);
        }
    }

    private void isUserWithUsernameExists(User userExists, String username){
        if(!username.isEmpty() && username.length()>3){
            Optional<User> userUsernameExists = userRepository.findByUsername(username);
            if(userUsernameExists.isPresent())
                throw new UserAlreadyExistsException("username "+username+" is already taken");
            else
                userExists.setUsername(username);
        }
    }

    private void isUserWithPhoneExists(User userExists, String phone) {
        if(!phone.isEmpty() && phone.replaceAll("\\s","").length()!=9){
            Optional<User> userPhoneExists = userRepository.findByPhone(phone);
            if(userPhoneExists.isPresent())
                throw new UserAlreadyExistsException("phone "+phone+" is already taken");
            else
                userExists.setPhone(phone);
        }
    }

    @Override
    public UserDto getUserByUsername(String username) {
        throwExceptionIfObjectEmpty(username, "invalid username: ");
        log.debug("get user by username: "+username);
        return userMapper.userToUserDto(userRepository.findByUsername(username).orElseThrow(()->
                new UserNotFoundException(username+" not found.")));
    }

    @Override
    public UserPagedList getUsersPerPage(PageRequest pageRequest) {
        log.debug("taking user paged list");
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

        log.debug("adding role: "+roleName+" for user "+username);
        return userMapper.userToUserDto(userRepository.save(userMapper.userDtoToUser(userDto)));
    }
}
