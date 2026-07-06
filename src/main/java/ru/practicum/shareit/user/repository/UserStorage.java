package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User insert(User newUser);

    User update(User newUser);

    boolean delete(Long userId);

    User selectOne(Long userId);

    List<User> selectAll();

    boolean isEmailAlreadyUsed(String email);

}
