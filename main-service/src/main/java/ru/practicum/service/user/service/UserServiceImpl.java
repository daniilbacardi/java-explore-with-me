package ru.practicum.service.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.exception.EntityNotFoundException;
import ru.practicum.service.user.dto.NewUserRequest;
import ru.practicum.service.user.dto.UserDto;
import ru.practicum.service.user.mapper.UserMapper;
import ru.practicum.service.user.model.User;
import ru.practicum.service.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto addUser(NewUserRequest newUserRequest) {
        log.info("UserServiceImpl: addUser вызван");
        UserDto user = userMapper.fromModelToDto(userRepository.save(userMapper.fromDtoToModel(newUserRequest)));
        log.info("UserServiceImpl: addUser выполнено {}", user);
        return user;
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        log.info("UserServiceImpl: getUsers вызван");
        Pageable page = PageRequest.of(from / size, size);
        log.info("UserServiceImpl: getUsers выполнено");
        if (ids == null || ids.isEmpty()) {
            return userRepository.findAll(page).getContent().stream()
                    .map(userMapper::fromModelToDto).collect(Collectors.toList());
        } else {
            return userRepository.findAllByIdIn(ids, page).getContent().stream()
                    .map(userMapper::fromModelToDto).collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.info("UserServiceImpl: deleteUser вызван");
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        log.info("UserServiceImpl: deleteUser выполнено {}", userId);
        userRepository.deleteById(userId);
    }

    @Override
    public User findUserById(Long userId) {
        log.info("UserServiceImpl: findUserById выполнено {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    }
}
