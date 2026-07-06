package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.common.Utils;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component("UserStorageInMemory")
public class UserStorageInMemory implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User insert(User newUser) {
        newUser.setId(Utils.getNewId(users.keySet()));
        log.trace("Создание/сохранение пользователя {}", newUser.toString());
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public User update(User newUser) {
        users.put(newUser.getId(), newUser);
        log.trace("Обновление пользователя {}", newUser.toString());
        return newUser;
    }

    @Override
    public boolean delete(Long userId) {
        log.trace("Удаление пользователя id={}", userId);
        users.remove(userId);
        return Optional.ofNullable(users.get(userId)).isPresent();
    }

    @Override
    public User selectOne(Long userId) {
        return Optional.ofNullable(users.get(userId))
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь id = %d не найден", userId)));
    }

    @Override
    public List<User> selectAll() {
        return users.values().stream().toList();
    }

    @Override
    public boolean isEmailAlreadyUsed(String email) {
        return users.values().stream().anyMatch(u -> u.getEmail().equals(email));
    }

}
