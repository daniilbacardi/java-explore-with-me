package ru.practicum.service.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.service.user.dto.NewUserRequest;
import ru.practicum.service.user.dto.UserDto;
import ru.practicum.service.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User fromDtoToModel(NewUserRequest userDto);

    UserDto fromModelToDto(User user);
}
