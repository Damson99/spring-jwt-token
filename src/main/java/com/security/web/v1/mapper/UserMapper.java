package com.security.web.v1.mapper;

import com.security.jwt.domain.User;
import com.security.web.v1.model.UserDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
@DecoratedWith(UserMapperDecorator.class)
public interface UserMapper {
    User userDtoToUser(UserDto userDto);

    UserDto userToUserDto(User user);
}
