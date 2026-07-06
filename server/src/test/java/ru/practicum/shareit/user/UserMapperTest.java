package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.CreateNewUserDto;
import ru.practicum.shareit.user.dto.UpdateExistsUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class UserMapperTest {
    private final CreateNewUserDto newUser = new CreateNewUserDto(100500L, "user@server.domain", "Test Testov");
    private final UpdateExistsUserDto updExUser = new UpdateExistsUserDto(100500L, "user@server.domain", "Test Testov");
    private final User user = new User(100500L, "user@server.domain", "Test Testov");
    private final UserDto userDto = new UserDto(100500L, "user@server.domain", "Test Testov");

    private final UpdateExistsUserDto emptyUpdUser = new UpdateExistsUserDto(100500L, "", "");

    @Test
    public void testMapToUserDto() {
        UserDto userDto = UserMapper.mapToUserDto(user);
        assertThat(userDto, equalTo(userDto));
    }

    @Test
    public void toUserTest() {
        User us = UserMapper.mapToUser(newUser);
        assertThat(us.getName(), equalTo(user.getName()));
        assertThat(us.getEmail(), equalTo(user.getEmail()));
        assertThat(us.getName(), equalTo(user.getName()));
    }

    @Test
    public void updateUserFieldsTest() {
        User us = UserMapper.updateUserFields(user, updExUser);
        assertThat(us.getId(), equalTo(user.getId()));
        assertThat(us.getName(), equalTo(user.getName()));
        assertThat(us.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    public void updateUserEmptyFieldsTest() {
        User us = UserMapper.updateUserFields(user, emptyUpdUser);
        assertThat(us.getId(), equalTo(user.getId()));
        assertThat(us.getName(), equalTo(user.getName()));
        assertThat(us.getEmail(), equalTo(user.getEmail()));
    }
}
