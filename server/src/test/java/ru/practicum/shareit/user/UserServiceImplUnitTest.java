package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.CreateNewUserDto;
import ru.practicum.shareit.user.dto.UpdateExistsUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createNewUser_shouldSaveAndReturn_whenEmailNotDuplicated() {
        when(userRepository.findByEmail("alice@mail.ru")).thenReturn(Optional.empty());
        User saved = new User(1L, "alice@mail.ru", "Alice");
        when(userRepository.save(any())).thenReturn(saved);

        CreateNewUserDto dto = new CreateNewUserDto();
        dto.setEmail("alice@mail.ru");
        dto.setName("Alice");

        UserDto result = userService.createNewUser(dto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("alice@mail.ru");
        verify(userRepository).save(any());
    }

    @Test
    void createNewUser_shouldThrowDuplicateEmailException_whenEmailExists() {
        User existing = new User(1L, "dup@mail.ru", "Existing");
        when(userRepository.findByEmail("dup@mail.ru")).thenReturn(Optional.of(existing));

        CreateNewUserDto dto = new CreateNewUserDto();
        dto.setEmail("dup@mail.ru");
        dto.setName("New");

        assertThatThrownBy(() -> userService.createNewUser(dto))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("dup@mail.ru");
    }

    @Test
    void getUserById_shouldReturnDto_whenFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "a@a.ru", "A")));

        UserDto result = userService.getUserById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getUserById_shouldThrowNotFoundException_whenMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getAllUsers_shouldReturnMappedList() {
        when(userRepository.findAll())
                .thenReturn(List.of(new User(1L, "a@a.ru", "A"), new User(2L, "b@b.ru", "B")));

        List<UserDto> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void getAllUsers_shouldReturnEmptyList_whenNoUsers() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDto> result = userService.getAllUsers();

        assertThat(result).isEmpty();
    }

    @Test
    void updateExistsUser_shouldThrowDuplicateEmailException_whenEmailAlreadyTaken() {
        User other = new User(2L, "taken@mail.ru", "Other");
        when(userRepository.findByEmail("taken@mail.ru")).thenReturn(Optional.of(other));

        assertThatThrownBy(() ->
                userService.updateExistsUser(1L, new UpdateExistsUserDto(null, "taken@mail.ru", "X")))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void updateExistsUser_shouldThrowNotFoundException_whenUserMissing() {
        when(userRepository.findByEmail("new@mail.ru")).thenReturn(Optional.empty());
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                userService.updateExistsUser(99L, new UpdateExistsUserDto(null, "new@mail.ru", "X")))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateExistsUser_shouldUpdateEmail_whenHasEmailTrue() {
        User existing = new User(1L, "old@mail.ru", "OldName");
        when(userRepository.findByEmail("new@mail.ru")).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any())).thenReturn(new User(1L, "new@mail.ru", "OldName"));

        UserDto result = userService.updateExistsUser(1L, new UpdateExistsUserDto(null, "new@mail.ru", ""));

        assertThat(result.getEmail()).isEqualTo("new@mail.ru");
    }

    @Test
    void updateExistsUser_shouldUpdateName_whenHasNameTrue() {
        User existing = new User(1L, "old@mail.ru", "OldName");
        when(userRepository.findByEmail("")).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any())).thenReturn(new User(1L, "old@mail.ru", "NewName"));

        UserDto result = userService.updateExistsUser(1L, new UpdateExistsUserDto(null, "", "NewName"));

        assertThat(result.getName()).isEqualTo("NewName");
    }

    @Test
    void updateExistsUser_shouldNotChangeFields_whenBothBlank() {
        User existing = new User(1L, "old@mail.ru", "OldName");
        when(userRepository.findByEmail("")).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any())).thenReturn(existing);

        userService.updateExistsUser(1L, new UpdateExistsUserDto(null, "", ""));

        verify(userRepository).save(existing);
    }

    @Test
    void deleteUser_shouldCallRepositoryDelete_andReturnTrue() {
        User user = new User(1L, "del@mail.ru", "Del");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        boolean result = userService.deleteUser(1L);

        assertThat(result).isTrue();
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_shouldThrowNotFoundException_whenUserMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(99L))
                .isInstanceOf(NotFoundException.class);
    }
}