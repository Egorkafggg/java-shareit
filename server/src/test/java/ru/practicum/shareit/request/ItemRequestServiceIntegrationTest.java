package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.CreateNewUserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceIntegrationTest {

    private final ItemRequestService itemRequestService;
    private final UserService userService;

    @Test
    void create_shouldSaveRequestWithTimestamp() {
        Long userId = createUser("user1@mail.ru", "User1");
        NewItemRequestDto dto = new NewItemRequestDto();
        dto.setDescription("Нужна дрель");

        ItemRequestDto result = itemRequestService.create(userId, dto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Нужна дрель");
        assertThat(result.getCreated()).isNotNull();
        assertThat(result.getRequestorId()).isEqualTo(userId);
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    void findAllByRequestor_shouldReturnOnlyOwnRequests() {
        Long userId1 = createUser("user2@mail.ru", "User2");
        Long userId2 = createUser("user3@mail.ru", "User3");

        createRequest(userId1, "Запрос 1");
        createRequest(userId1, "Запрос 2");
        createRequest(userId2, "Чужой запрос");

        List<ItemRequestDto> result = new ArrayList<>(itemRequestService.findAllByRequestorId(userId1));

        assertThat(result).hasSize(2);
        // Сортировка: новые → старые
        assertThat(result.get(0).getDescription()).isEqualTo("Запрос 2");
        assertThat(result.get(1).getDescription()).isEqualTo("Запрос 1");
    }

    @Test
    void findAllOthers_shouldReturnOtherUsersRequests() {
        Long userId1 = createUser("user4@mail.ru", "User4");
        Long userId2 = createUser("user5@mail.ru", "User5");

        createRequest(userId1, "Мой запрос");
        createRequest(userId2, "Чужой запрос");

        List<ItemRequestDto> result = new ArrayList<>(itemRequestService.findAllOfAnotherRequestors(userId1));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("Чужой запрос");
    }

    @Test
    void create_shouldThrowNotFoundException_whenUserNotExists() {
        NewItemRequestDto dto = new NewItemRequestDto();
        dto.setDescription("Запрос");

        assertThatThrownBy(() -> itemRequestService.create(999L, dto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findItemRequest_shouldReturnRequestWithItems() {
        Long userId = createUser("user6@mail.ru", "User6");
        ItemRequestDto created = createRequest(userId, "Запрос с вещами");

        ItemRequestDto result = itemRequestService.findItemRequest(created.getId()); //, userId);

        assertThat(result.getId()).isEqualTo(created.getId());
        assertThat(result.getItems()).isNotNull();
    }

    // -- helpers --

    private Long createUser(String email, String name) {
        CreateNewUserDto dto = new CreateNewUserDto();
        dto.setEmail(email);
        dto.setName(name);
        return userService.createNewUser(dto).getId();
    }

    private ItemRequestDto createRequest(Long userId, String description) {
        NewItemRequestDto dto = new NewItemRequestDto();
        dto.setDescription(description);
        return itemRequestService.create(userId, dto);
    }
}