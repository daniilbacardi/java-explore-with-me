package ru.practicum.service.user.service;

import ru.practicum.service.user.dto.NewUserRequest;
import ru.practicum.service.user.dto.UserDto;
import ru.practicum.service.user.model.User;

import java.util.List;

public interface UserService {
    UserDto addUser(NewUserRequest newUserRequest);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    void deleteUser(Long userId);

    User findUserById(Long userId);
}
