package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateNewUserDto;
import ru.practicum.shareit.user.dto.UpdateExistsUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;
    private final String idParamPath = "/{id}";

    @PostMapping
    public UserDto create(@Valid @RequestBody CreateNewUserDto newUser) {
        return userService.createNewUser(newUser);
    }

    @PatchMapping(idParamPath)
    public UserDto update(@PathVariable("id") Long userId,
                          @Valid @RequestBody UpdateExistsUserDto newUser) {
        return userService.updateExistsUser(userId, newUser);
    }

    @DeleteMapping(idParamPath)
    public boolean delete(@PathVariable("id") Long userId) {
        return userService.deleteUser(userId);
    }

    @GetMapping(idParamPath)
    public UserDto findUser(@PathVariable("id") Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getAllUsers();
    }

}
