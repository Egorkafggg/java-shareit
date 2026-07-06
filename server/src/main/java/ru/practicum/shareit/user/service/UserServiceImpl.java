package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.CreateNewUserDto;
import ru.practicum.shareit.user.dto.UpdateExistsUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public UserDto createNewUser(CreateNewUserDto newUser) {
        log.debug("Добавление нового пользователя");

        Optional<User> findUser = repository.findByEmail(newUser.getEmail());
        if (findUser.isPresent()) {
            throw new DuplicateEmailException(String.format("@-адрес \"%s\" уже присвоен другому пользователю", newUser.getEmail()));
        }

        User user = UserMapper.mapToUser(newUser);
        user = repository.save(user);

        return UserMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateExistsUser(Long userId, UpdateExistsUserDto newUser) {
        log.debug("Обновление пользователя");

        Optional<User> findUser = repository.findByEmail(newUser.getEmail());
        if (findUser.isPresent()) {
            throw new DuplicateEmailException(String.format("@-адрес \"%s\" уже присвоен другому пользователю", newUser.getEmail()));
        }

        User updatedUser = UserMapper.updateUserFields(findById(userId), newUser);
        updatedUser = repository.save(updatedUser);

        return UserMapper.mapToUserDto(updatedUser);
    }

    @Override
    @Transactional
    public boolean deleteUser(Long userId) {
        User delUser = findById(userId);
        log.debug("Удаление пользователя {}", delUser.toString());
        repository.delete(delUser);
        return true;
    }

    private User findById(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь id = %d не найден", userId)));
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.mapToUserDto(findById(userId));
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.debug("Получение полного списка пользователей");
        return repository.findAll().stream().map(UserMapper::mapToUserDto).toList();
    }

}
