package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.CreateNewUserDto;
import ru.practicum.shareit.user.dto.UpdateExistsUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(@Qualifier("UserStorageInMemory") UserStorage userStorage) {
      this.userStorage = userStorage;
    }

    @Override
    public UserDto createNewUser(CreateNewUserDto newUser) {
        log.debug("Добавление нового пользователя");

        if (userStorage.isEmailAlreadyUsed(newUser.getEmail())) {
            throw new DuplicateEmailException(String.format("@-адрес \"%s\" уже присвоен другому пользователю", newUser.getEmail()));
        }

        User user = UserMapper.mapToUser(newUser);
        user = userStorage.insert(user);

        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto updateExistsUser(Long userId, UpdateExistsUserDto newUser) {
        log.debug("Обновление пользователя");

        if (userId == null) {
            throw new ValidationException("Не указан id пользователя");
        }

        if (userStorage.isEmailAlreadyUsed(newUser.getEmail())) {
            throw new DuplicateEmailException(String.format("@-адрес \"%s\" уже присвоен другому пользователю", newUser.getEmail()));
        }

        User updatedUser = UserMapper.updateUserFields(userStorage.selectOne(userId), newUser);
        updatedUser = userStorage.update(updatedUser);

        return UserMapper.mapToUserDto(updatedUser);
    }

    @Override
    public boolean deleteUser(Long userId) {
        User user = userStorage.selectOne(userId);
        log.debug("Удаление пользователя {}", user.toString());
        return userStorage.delete(userId);
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.mapToUserDto(userStorage.selectOne(userId));
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.debug("Получение полного списка пользователей");
        return userStorage.selectAll().stream().map(UserMapper::mapToUserDto).toList();
    }

}
