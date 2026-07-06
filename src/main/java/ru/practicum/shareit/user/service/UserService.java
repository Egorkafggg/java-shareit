package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateNewUserDto;
import ru.practicum.shareit.user.dto.UpdateExistsUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createNewUser(CreateNewUserDto newUser);

    UserDto getUserById(Long userId);

    List<UserDto> getAllUsers();

    UserDto updateExistsUser(Long userId, UpdateExistsUserDto newUser);

    boolean deleteUser(Long userId);

}
