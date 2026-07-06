// UserService.java (изменённая версия)
package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(mapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID=" + id + " не найден!"));
        return mapper.toUserDto(user);
    }

    @Transactional
    public UserDto create(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistsException("Пользователь с E-mail=" + userDto.getEmail() + " уже существует!");
        }
        User user = mapper.toUser(userDto);
        user.setId(null); // для создания нового пользователя
        return mapper.toUserDto(userRepository.save(user));
    }

    @Transactional
    public UserDto update(UserDto userDto, Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID=" + id + " не найден!"));

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new UserAlreadyExistsException("Пользователь с E-mail=" + userDto.getEmail() + " уже существует!");
            }
            existingUser.setEmail(userDto.getEmail());
        }

        return mapper.toUserDto(userRepository.save(existingUser));
    }

    @Transactional
    public void delete(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователь с ID=" + userId + " не найден!");
        }
        userRepository.deleteById(userId);
    }
}