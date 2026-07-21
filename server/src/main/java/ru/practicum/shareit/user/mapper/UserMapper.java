package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.dto.user.UserDto;
import ru.practicum.shareit.user.User;

public class UserMapper {

    private UserMapper() {
        // private constructor to prevent instantiation
    }

    public static UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

    public static User updateEntity(User existingUser, UserDto userDto) {
        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            existingUser.setEmail(userDto.getEmail());
        }
        return existingUser;
    }
}