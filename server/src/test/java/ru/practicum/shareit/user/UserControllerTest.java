package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    private final String userUrlTemplate = "/users";

    @Test
    void createNewUserTest() throws Exception {
        UserDto userDto = new UserDto(100500L, "user@server.domain", "User Name");

        when(userService.createNewUser(any())).thenReturn(userDto);

        mvc.perform(post(userUrlTemplate)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class));
    }

    @Test
    void deleteUserTest() throws Exception {
        mvc.perform(delete(userUrlTemplate + "/" + anyLong()))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(anyLong());
    }

    @Test
    void updateExistsUserTest() throws Exception {
        UserDto userDto = new UserDto(100500L, "user@server.domain", "User Name");

        when(userService.updateExistsUser(anyLong(), any())).thenReturn(userDto);

        mvc.perform(patch(userUrlTemplate + "/" + userDto.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class));
    }

    @Test
    void getUserByIdTest() throws Exception {
        UserDto userDto = new UserDto(100500L, "user@server.domain", "User Name");

        when(userService.getUserById(anyLong())).thenReturn(userDto);

        mvc.perform(get(userUrlTemplate + "/" + userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class));
    }

    @Test
    void getAllUsersTest() throws Exception {
        List<UserDto> newUsers = List.of(
                new UserDto(100500L, "user@server.domain", "User Name"),
                new UserDto(200600L, "user2@server2.domain2", "User2 Name2"),
                new UserDto(300700L, "user3@server3.domain3", "User3 Name3")
                );

        when(userService.getAllUsers()).thenReturn(newUsers);

        mvc.perform(get(userUrlTemplate)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(is(newUsers.getFirst().getId()), Long.class))
                .andExpect(jsonPath("$[0].email").value(is(newUsers.getFirst().getEmail())))
                .andExpect(jsonPath("$[0].name").value(is(newUsers.getFirst().getName())))
                .andExpect(jsonPath("$[2].id").value(is(newUsers.getLast().getId()), Long.class))
                .andExpect(jsonPath("$[2].email").value(is(newUsers.getLast().getEmail())))
                .andExpect(jsonPath("$[2].name").value(is(newUsers.getLast().getName())));
    }

}
