package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

@Data
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateNewUserDto {
    Long id;
    @NotBlank(message = "@-адрес не должен быть пустым")
    @Email(message = "@-адрес должен быть в формате user@server.domain")
    String email;
    @NotBlank(message = "имя (логин) пользователя  не должно быть пустым")
    String name;
}