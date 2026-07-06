package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    // УДАЛИТЕ: private final CheckConsistencyService checker;

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("Получен GET-запрос к эндпоинту: '/users' на получение всех пользователей");
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("Получен GET-запрос к эндпоинту: '/users/{}' на получение пользователя", userId);
        return userService.getUserById(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Получен POST-запрос к эндпоинту: '/users' на добавление пользователя с email='{}'",
                userDto.getEmail());
        return userService.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@Valid @RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("Получен PATCH-запрос к эндпоинту: '/users/{}' на обновление пользователя", userId);
        return userService.update(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        log.info("Получен DELETE-запрос к эндпоинту: '/users/{}' на удаление пользователя", userId);
        userService.delete(userId);
        // УДАЛИТЕ: checker.deleteItemsByUser(userId);
        // Теперь удаление вещей происходит через каскадное удаление в БД
    }
}