package ru.practicum.shareit.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.CreateNewUserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(ErrorHandler.class)
class ErrorHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void handleNotFound_shouldReturn404_withErrorBody() throws Exception {
        when(userService.getUserById(99L))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Пользователь не найден"));
    }

    @Test
    void handleDuplicateEmail_shouldReturn409_withErrorBody() throws Exception {
        when(userService.createNewUser(any()))
                .thenThrow(new DuplicateEmailException("Email уже занят"));

        CreateNewUserDto dto = new CreateNewUserDto();
        dto.setEmail("dup@mail.ru");
        dto.setName("Dup");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Email уже занят"));
    }

    @Test
    void handleValidation_shouldReturn400_withErrorBody() throws Exception {
        when(userService.createNewUser(any()))
                .thenThrow(new ValidationException("Не указан id пользователя"));

        CreateNewUserDto dto = new CreateNewUserDto();
        dto.setEmail("v@v.ru");
        dto.setName("V");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Не указан id пользователя"));
    }

    @Test
    void handleWrongStatus_shouldReturn400_withErrorBody() throws Exception {
        when(userService.createNewUser(any()))
                .thenThrow(new WrongBookingStatusException("Вещь уже занята!"));

        CreateNewUserDto dto = new CreateNewUserDto();
        dto.setEmail("ws@ws.ru");
        dto.setName("WS");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Вещь уже занята!"));
    }

    @Test
    void handleForbidden_shouldReturn403_withErrorBody() throws Exception {
        when(userService.createNewUser(any()))
                .thenThrow(new OtherOwnerItemEditingException("Только владелец может изменять"));

        CreateNewUserDto dto = new CreateNewUserDto();
        dto.setEmail("f@f.ru");
        dto.setName("F");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Только владелец может изменять"));
    }

    @Test
    void handleException_shouldReturn500_withGenericMessage() throws Exception {
        when(userService.createNewUser(any()))
                .thenThrow(new RuntimeException("Непредвиденная ошибка"));

        CreateNewUserDto dto = new CreateNewUserDto();
        dto.setEmail("err@err.ru");
        dto.setName("Err");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is5xxServerError());
    }
}