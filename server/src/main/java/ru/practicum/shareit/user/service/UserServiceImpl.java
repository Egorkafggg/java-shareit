package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.dto.user.UserDto;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repo.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        checkEmailDuplicate(userDto.getEmail(), null);
        User user = UserMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);
        return UserMapper.toDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
        User existingUser = userOpt.get();

        if (userDto.getEmail() != null) {
            checkEmailDuplicate(userDto.getEmail(), userId);
        }

        User updatedUser = UserMapper.updateEntity(existingUser, userDto);
        userRepository.save(updatedUser);
        return UserMapper.toDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
        return UserMapper.toDto(userOpt.get());
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
