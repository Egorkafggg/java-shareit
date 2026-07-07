package ru.practicum.shareit.user.sevice;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repo.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        checkEmailDuplicate(userDto.getEmail(), null);
        User user = userMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User existingUser = userRepository.findById(userId);
        if (existingUser == null) {
            throw new NotFoundException("User with id " + userId + " not found");
        }

        if (userDto.getEmail() != null) {
            checkEmailDuplicate(userDto.getEmail(), userId);
        }

        User updatedUser = userMapper.updateEntity(existingUser, userDto);
        userRepository.update(updatedUser);
        return userMapper.toDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    private void checkEmailDuplicate(String email, Long userId) {
        if (userId == null) {
            if (userRepository.existsByEmail(email)) {
                throw new DuplicateEmailException("User with email " + email + " already exists");
            }
        } else {
            if (userRepository.existsByEmailAndIdNot(email, userId)) {
                throw new DuplicateEmailException("User with email " + email + " already exists");
            }
        }
    }
}

