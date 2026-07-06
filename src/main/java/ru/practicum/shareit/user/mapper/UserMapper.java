package ru.practicum.shareit.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.CreateNewUserDto;
import ru.practicum.shareit.user.dto.UpdateExistsUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        return userDto;
    }

    public static User mapToUser(CreateNewUserDto newUser) {
        User user = new User();
        user.setEmail(newUser.getEmail());
        user.setName(newUser.getName());
        return user;
    }

    public static User updateUserFields(User user, UpdateExistsUserDto newUser) {
        if (newUser.hasEmail()) {
            user.setEmail(newUser.getEmail());
        }
        if (newUser.hasName()) {
            user.setName(newUser.getName());
        }
        return user;
    }
}
