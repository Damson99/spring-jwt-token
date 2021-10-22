package com.security.web.v1.mapper;

import com.security.jwt.domain.User;
import com.security.web.v1.model.UserDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(
    componentModel = "spring")
@DecoratedWith(UserMapperDecorator.class)
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    User userDtoToUser(UserDto userDto);

    UserDto userToUserDto(User user);
}
