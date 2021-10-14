package com.security.web.v1.mapper;

import com.security.jwt.domain.User;
import com.security.web.v1.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class UserMapperDecorator implements UserMapper
{
    private UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User userDtoToUser(UserDto userDto) {
        return userMapper.userDtoToUser(userDto);
    }

    @Override
    public UserDto userToUserDto(User user) {
        return userMapper.userToUserDto(user);
    }
}
